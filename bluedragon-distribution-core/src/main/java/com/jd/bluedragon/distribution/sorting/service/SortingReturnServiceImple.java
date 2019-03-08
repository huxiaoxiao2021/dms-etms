package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.ReturnsRequest;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.packageToMq.service.IPushPackageToMqService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.dao.SortingReturnDao;
import com.jd.bluedragon.distribution.sorting.domain.SortingReturn;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service("sortingReturnService")
public class SortingReturnServiceImple implements SortingReturnService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private Pattern checkInteger = Pattern.compile("^\\d+$");
	/**
	 * 错误消息的分隔符 1-xxxxxxxxxx 错误编码 + "-" + 错误消息
	 */
	private static final String ERROR_CODE_MSG_SPLIT = "-";

	/**
	 * 退款100分MQ消费消息体定义 MQ KEY
	 */
	private final static String SORTINGRET_MQ_KEY = "bd_blocker_complete";

	/**
	 * 退款100分的 类型编码
	 */
	private final static String RETURN_100 = PropertiesHelper.newInstance().getValue("RETURN_100");
	/**
	 * 外单拦截类型
	 */
	private final static String INTERCEPT = PropertiesHelper.newInstance().getValue("INTERCEPT");
	/**
	 * 外呼退库的 类型编码（业务同正常分拣退货）
	 */
	public final static String ABNORMAL = "ABNORMAL";
	public final static Integer ABNORMAL_INTEGER = -10;

	/**
	 * 记录拦截订单操作数据
	 */
	public final static Integer INTERCEPT_RECORD_TYPE = -1;
	
	@Autowired
	private TaskService taskService;

    @Autowired
    @Qualifier("bdBlockerCompleteMQ")
    private DefaultJMQProducer bdBlockerCompleteMQ;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private SortingReturnDao sortingReturnDao;

	@Autowired
	private OperationLogService operationLogService;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private IPushPackageToMqService pushMqService;
	
//	@Autowired
//	private BizServiceInterface bizService;
	
	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@JProfiler(jKey= "DMSWORKER.SortingReturnService.doSortingReturnForTask",mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void doSortingReturn(Task task) throws Exception {
			this.execReturns(task);	
	}

	/********************************************* 基础逻辑 ******************************************/

	/**
	 * 执行数据导入功能
	 *
	 * @param  task
	 * @throws Exception
	 */
	private void execReturns(Task task) throws Exception {
		/** 如果数据为空则返回 */
		if (StringHelper.isEmpty(task.getBody())) {
			return;
		}
		/** 将JSON信息实例化为分拣退货信息 */
		ReturnsRequest[] array = JsonHelper.jsonToArray(task.getBody(), ReturnsRequest[].class);
		/** 转化为数组 */
		Set<ReturnsRequest> returnsRequests = new CollectionHelper<ReturnsRequest>().toSet(array);
		/** 生成（更新）数据 */
		ArrayList<SortingReturn> returnList = addReturnLog(returnsRequests); // 添加分拣记录

		pushBlockerMqQueue(returnList);

		sendModifyWaybillStatusNotify(returnList);
	}

	/**
	 * 增加或更新分拣退货信息
	 *
	 * @param  returnsRequests
	 */
	private ArrayList<SortingReturn> addReturnLog(Set<ReturnsRequest> returnsRequests) {
		ArrayList<SortingReturn> resultList = new ArrayList<SortingReturn>();
		for (ReturnsRequest request : returnsRequests) {
			SortingReturn sorting = SortingReturn.parse(request);
            if(sorting.getBusinessType()==null || !sorting.getBusinessType().equals(INTERCEPT_RECORD_TYPE)){
	          	/*拦截记录饿信息不回传运单*/
            	resultList.add(sorting);
	        }

			this.saveOrUpdate(sorting);
		}

		return resultList;
	}

	/**
	 * 根据站点号，查询站点信息
	 *
	 * @param  sitecode
	 * @return BaseStaffSiteOrgDto
	 */
	private BaseStaffSiteOrgDto getSiteDtoBySiteCode(Integer sitecode) {
		//去除自己写的缓存，改为单个查（自带缓存）
		BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(sitecode);
		/** 可能为空 */
		return dto;
	}

	/**
	 * 增加或更新分拣退货信息
	 *
	 * @param returns
	 */
	private void saveOrUpdate(SortingReturn returns) {
		/** 如果更新的数据位空，则插入数据 */
		if (returns.getBusinessType().equals(INTERCEPT_RECORD_TYPE)||Constants.NO_MATCH_DATA == this.update(returns).intValue()) {
			/** 插入数据 */
			this.add(returns);
			addOperationLog(returns);
		}
	}

	private Integer add(SortingReturn returns) {
		return this.sortingReturnDao.add(SortingReturnDao.namespace, returns);
	}

	/**
	 * 更新数据
	 *
	 * @param   returns
	 * @return
	 */
	private Integer update(SortingReturn returns) {
		return this.sortingReturnDao.update(SortingReturnDao.namespace, returns);
	}

	/**
	 * 插入pda操作日志表
	 *
	 * @param sortingReturn
	 */
	private void addOperationLog(SortingReturn sortingReturn) {
		OperationLog operationLog = new OperationLog();
		operationLog.setCreateSiteCode(sortingReturn.getSiteCode());
		operationLog.setCreateTime(sortingReturn.getCreateTime());
		operationLog.setCreateUser(sortingReturn.getUserName());
		operationLog.setCreateUserCode(sortingReturn.getUserCode());
		if(sortingReturn.getBusinessType()!=null && sortingReturn.getBusinessType().equals(INTERCEPT_RECORD_TYPE)){
			operationLog.setLogType(OperationLog.LOG_TYPE_SORTING_INTERCEPT_LOG);
		}else{
			operationLog.setLogType(OperationLog.LOG_TYPE_SORTING_RET);
		}
		operationLog.setOperateTime(sortingReturn.getCreateTime());
		operationLog.setPackageCode(sortingReturn.getPackageCode());
		operationLog.setUpdateTime(sortingReturn.getUpdateTime());
		operationLog.setWaybillCode(sortingReturn.getWaybillCode());
		operationLogService.add(operationLog);
	}

	/********************************************* 运单转换 ********************************************************/
	private void sendModifyWaybillStatusNotify(List<SortingReturn> _datas) throws Exception {
		/** 站点信息匹配的数据 */
		ArrayList<SortingReturn> normalreturnsal = new ArrayList<SortingReturn>();
		/** 转换为包裹参数的数据 */
		ArrayList<SortingReturn> erroral = new ArrayList<SortingReturn>();
		/********************* STEP1: 设置运单处理参数 *************************/
		for (SortingReturn returns : _datas) {
			SortingReturn tmp = returns;
			/** 返回站点信息 */
			BaseStaffSiteOrgDto dto = this.getSiteDtoBySiteCode(tmp.getSiteCode());
			/** 站点信息无法匹配，跳转到下一个数据 */
			if (dto == null) {
				this.logger.error("分拣退货ID：" + returns.getId() + " 无法找到站点编码[" + tmp.getSiteCode()
				        + "]，无法处理数据");
				erroral.add(tmp);
				continue;
			}
			/** 转换为运单信息参数 */
			this.logger.info("分拣退货ID：" + returns.getId() + " 为运单数据");
			/** 运单 */
			normalreturnsal.add(tmp);
		}
		/** 打印错误信息 */
		if (erroral.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (SortingReturn errorreturns : erroral) {
				sb.append("-" + errorreturns.getId());
			}
			this.logger.error("获取siteCode信息失败数据Id=：[" + sb.toString() + "]");
		}
		/********************* STEP2: 调用运单接口更新状态 **********************/
		if (normalreturnsal.size() > 0) {
			addUpdateWaybillTask(normalreturnsal);// 运单
		}
	}

	/******************************** 推送更新运单任务 ***************************************/
	/**
	 * 处理运单
	 *
	 * @param  normalreturnsal_waybill
	 */
	private void addUpdateWaybillTask(ArrayList<SortingReturn> normalreturnsal_waybill) {
		try {
			for (SortingReturn returns : normalreturnsal_waybill) {
				addSortingAdditionalTask(returns);
			}
		} catch (Exception e) {
			this.logger.error(
			        "调用waybillWS.batchUpdateWaybillByWaybillCode(paramal_waybill,60)接口失败", e);
		}
	}

	private void addSortingAdditionalTask(SortingReturn returns) {
		// prepare:
		// 拆分分析字段
		Integer createSiteCode = returns.getSiteCode();
		Integer receiveSiteCode = -1;/* 分拣退货-收获站点默认 -1 */

		if (StringHelper.isEmpty(returns.getPackageCode())
		        || !NumberHelper.isPositiveNumber(createSiteCode)
		        || !NumberHelper.isPositiveNumber(returns.getUserCode())
		        || StringHelper.isEmpty(returns.getUserName())) {
			this.logger.error("分拣退货记录某数据项为空 id = " + returns.getId());
			return;
		}

		BaseStaffSiteOrgDto createSite = null;
		try {
			createSite = this.baseMajorManager.getBaseSiteBySiteId(createSiteCode);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (createSite == null) {
			this.logger.info("创建站点或接收站点信息为空.");
			this.logger.info("创建站点：" + createSiteCode);
			this.logger.info("接收站点：" + receiveSiteCode);
			return;
		}
		WaybillStatus waybillStatus = parseWaybillStatus(returns, createSite, null);
		String jsonStr = JsonHelper.toJson(waybillStatus);
		Task task = new Task();
		task.setBody(jsonStr);
		task.setFingerprint(Md5Helper.encode(returns.getSiteCode() + "_" + -1 + "_"
		        + returns.getBusinessType() + "_" + returns.getWaybillCode() + "_"
		        + returns.getPackageCode() + "_" + System.currentTimeMillis()));
		task.setBoxCode(null);/* 分拣退货-箱号 */
		task.setCreateSiteCode(returns.getSiteCode());
		task.setCreateTime(new java.util.Date());
		task.setKeyword1(returns.getWaybillCode());
		task.setKeyword2(returns.getPackageCode());
		task.setReceiveSiteCode(-1);
		task.setType(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING_RETURN);
		task.setTableName(Task.TABLE_NAME_WAYBILL);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setOwnSign(BusinessHelper.getOwnSign());
		taskService.add(task);
	}

	private WaybillStatus parseWaybillStatus(SortingReturn returns, BaseStaffSiteOrgDto createSite,
	        BaseStaffSiteOrgDto receiveSite) {

		WaybillStatus waybillStatus = new WaybillStatus();

		waybillStatus.setWaybillCode(returns.getWaybillCode());
		waybillStatus.setPackageCode(returns.getPackageCode());

		waybillStatus.setOrgId(createSite.getOrgId());
		waybillStatus.setOrgName(createSite.getOrgName());

		waybillStatus.setCreateSiteCode(returns.getSiteCode());
		waybillStatus.setCreateSiteName(returns.getSiteName());
		waybillStatus.setCreateSiteType(createSite.getSiteType());

		waybillStatus.setReceiveSiteCode(-1);
		waybillStatus.setReceiveSiteName(null);
		waybillStatus.setReceiveSiteType(-1);

		waybillStatus.setOperatorId(returns.getUserCode());
		waybillStatus.setOperator(returns.getUserName());
		waybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING_RETURN);
		waybillStatus.setOperateTime(returns.getCreateTime());
		return waybillStatus;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer doAddReturn(SortingReturn returns) {
		/** 设置查询参数 */
		SendDetail tSendDatail = new SendDetail();
		// 运单号
		tSendDatail.setWaybillCode(returns.getWaybillCode());
		// 包裹号
		tSendDatail.setPackageBarcode(returns.getPackageCode());
		try {
			/** 是否发货 */
			this.logger.info("ReturnsService开始处理分拣退货，调用DeliveryService判断是否已经发货");
			boolean isSend = this.deliveryService.checkSend(tSendDatail);
			this.logger.info("DeliveryService返回发货结果[发货false,未发货true]：" + isSend);
			if (isSend) {
				this.sortingReturnDao.add(SortingReturnDao.class.getName(), returns);
				this.logger.info("包裹/运单号[" + returns.getWaybillCode() + "/"
				        + returns.getPackageCode() + "]分拣退货信息保存成功");
				return SortingReturnService.ADDRETURN_OK;
			} else {
				return SortingReturnService.ADDRETURN_ISSEND;
			}
		} catch (Exception e) {
			this.logger.error("调用SortingReturnService.doAddReturn失败", e);
			return SortingReturnService.ADDRETURN_ERROR;
		}
	}

	/*********************************** 退款100分MQ消费消息体定义 ********************************/

	/**
	 * 验证不需要推送MQ的消息
	 * 如果是退款100分或者外单拦截类型的消息返回false,不需要推送的返回true
	 * @param shieldsType
	 * @return
	 */
	private boolean isNotToPushBlockerMqQueue(String shieldsType){
		if(shieldsType==null){
			return true;
		}else if(SortingReturnServiceImple.RETURN_100 != null &&
				shieldsType.equals(SortingReturnServiceImple.RETURN_100)){
			return false;
		}else if(SortingReturnServiceImple.INTERCEPT != null &&
				shieldsType.equals(SortingReturnServiceImple.INTERCEPT)){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 前面Task_sorting 已经去掉重复数据... 所以包裹数据至推送包裹运单第一个包裹号生成的MQ消息...
	 * 注意：分拣拦截生成的分拣退货不会推送运单快退消息,详见{@link #addReturnLog(Set)}
     * @see #addReturnLog(Set)
	 * @param _datas
	 */
	private void pushBlockerMqQueue(List<SortingReturn> _datas) {
        long timeId = System.currentTimeMillis();
        logger.info("SortingReturnServiceImple.pushBlockerMqQueue[" + timeId + "] 预处理");
        for (SortingReturn ret : _datas) {
            try {
                logger.info("SortingReturnServiceImple.pushBlockerMqQueue[" + timeId + "] 开始处理包裹号:"
                        + ret.getPackageCode());
                String shieldsError = ret.getShieldsError();
                String[] datas = shieldsError.split(SortingReturnServiceImple.ERROR_CODE_MSG_SPLIT);
                String[] packageMsg = ret.getPackageCode().split("-");
                String shieldsType = datas.length >= 2 && checkIntegerValue(datas[0]) ? datas[0]
                        .trim() : null;
                /*
				 * 包裹号 xxxxx-数量序号-总数量-滑道号 只有数量序号为1的才发送MQ
				 * （一单多件中的第一件：PDA只有在包裹齐全的时候才会推送数据）
				 */
                boolean isNotFirstPackage = packageMsg.length >= 3 && packageMsg[1].equals("1")
                        || packageMsg.length == 1 ? false : true;
                if (isNotFirstPackage || isNotToPushBlockerMqQueue(shieldsType)) {
                    logger.info("SortingReturnServiceImple.pushBlockerMqQueue[" + timeId
                            + "] 包裹号排除:" + ret.getPackageCode());
					/* 只有退款100分的数据才处理，类型为空不处理 */
                    continue;
                } else {
					/* 去重处理 */
                    logger.info("SortingReturnServiceImple.pushBlockerMqQueue[" + timeId
                            + "] 包裹号去重优化:" + ret.getPackageCode());
                    String orderId = ret.getWaybillCode();
                    String operateTime = DateHelper.formatDateTime(ret.getCreateTime());
                    //Integer orderType = ret.getBusinessType() == null ? 0 : ret.getBusinessType();
                    String mqbody = ret.getBusinessType() == null ?
                            createMqBody(orderId, operateTime, 0) :
                            createMqBody(orderId, operateTime, ret.getBusinessType());
                    //pushMqService.pubshMq(SortingReturnServiceImple.SORTINGRET_MQ_KEY, mqbody, orderId);
                    bdBlockerCompleteMQ.send(orderId,mqbody);
                    logger.info("SortingReturnServiceImple.pushBlockerMqQueue[" + timeId
                            + "] 推送MQ操作完成 orderId:" + orderId);
                }
            } catch (Exception e) {
			        /* 记录异常，但不处理，避免影响运单操作 */
                logger.error("退款100分MQ消息推送失败[" + timeId + "]", e);
                try{
                    SystemLogUtil.log(ret.getWaybillCode(),"BLOCKER_QUEUE_BD_DMS_ST",ret.getShieldsError(),ret.getShieldsType(),e.getMessage(),Long.valueOf(12201));
                }catch (Exception ex){
                    logger.error("退款100分MQ消息推送记录日志失败", ex);
                }
            }
        }


    }

	private String createMqBody(String orderId, String operateTime, Integer orderType) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
		sb.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
		sb.append("<OrderId>" + orderId + "</OrderId>");
		sb.append("<OrderType>" + orderType + "</OrderType>");
		sb.append("<MessageType>BLOCKER_QUEUE_BD_DMS_ST</MessageType>");
		sb.append("<OperatTime>" + operateTime + "</OperatTime>");
		sb.append("</OrderTaskInfo>");

		return sb.toString();
	}

	/**
	 * 判断是否整数类型
	 *
	 * @param value
	 * @return
	 */
	private boolean checkIntegerValue(String value) {
		return checkInteger.matcher(value).matches();
	}
	
	/**
	 * 查询是否已经操作了分拣退货
	 * @param packageCodeOrWaybillCode
	 * @return
	 */
	public Boolean exists(String packageCodeOrWaybillCode) {
		SortingReturn returns = new SortingReturn();
		if(WaybillUtil.isPackageCode(packageCodeOrWaybillCode)){
			returns.setWaybillCode(WaybillUtil.getWaybillCode(packageCodeOrWaybillCode));
			returns.setPackageCode(packageCodeOrWaybillCode);
		}else{
			returns.setWaybillCode(packageCodeOrWaybillCode);
		}
		return sortingReturnDao.exists(returns);
	}
	
	/**
	 * 查询包裹是否进行过站点发调度操作
	 * @param packageCode
	 * @return
	 */
	public Integer checkReDispatch(String packageCode){
		String waybillCode = WaybillUtil.getWaybillCode(packageCode);
		return this.waybillQueryManager.checkReDispatch(waybillCode);
	}
}
