package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.MonitorAlarm;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefund;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefundBlockerComplete;
import com.jd.bluedragon.distribution.fastRefund.service.FastRefundService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.dao.SendMReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("sortingService")
public class SortingServiceImpl implements SortingService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static String MQ_KEY_REFUND = "bd_blocker_complete";

	private final static Integer DELIVERY_INFO_EXPIRE_SCONDS = 30 * 60; //半小时

	@Autowired
	private SortingDao sortingDao;

	@Autowired
	private BoxService boxService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private OperationLogService operationLogService;

	@Autowired
	WaybillQueryApi waybillQueryApi;

	@Autowired
	private WaybillPickupTaskApi waybillPickupTaskApi;

	@Qualifier("bdBlockerCompleteMQ")
    @Autowired
    private DefaultJMQProducer bdBlockerCompleteMQ;
	
	@Qualifier("blockerComOrbrefundRqMQ")
    @Autowired
    private DefaultJMQProducer blockerComOrbrefundRqMQ;

	@Autowired
	private WaybillCancelService waybillCancelService;

	@Autowired
	private InspectionECDao inspectionECDao;

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private SendMReadDao sendMReadDao;

	@Autowired
	private RedisManager redisManager;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
//	@Autowired
//	private WaybillCommonService waybillCommonService;
	
	@Autowired
	private WaybillQueryManager waybillQueryManager;
	
	@Autowired
	WaybillService waybillService;
	
	@Autowired
	FastRefundService fastRefundService;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(Sorting sorting) {
		return this.sortingDao.add(SortingDao.namespace, sorting);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(Sorting sorting) {
		return this.sortingDao.update(SortingDao.namespace, sorting);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addSortingAndSendDetail(Sorting sorting) {
		this.addSorting(sorting, null);
		this.addSendDetail(sorting, null);
	}

	public boolean existSortingByPackageCode(Sorting sorting) {
		if (this.sortingDao.existSortingByPackageCode(sorting) > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Sorting> findSortingPackages(Sorting sorting) {
		String boxCodes = this.getBoxCodes(sorting);
		if (StringHelper.isEmpty(boxCodes)) {
			return Collections.emptyList();
		}

		sorting.setBoxCodes(boxCodes);
		return this.sortingDao.findSortingPackages(sorting);
	}

	private String getBoxCodes(Sorting sorting) {
		Box box = new Box();
		box.setType(Box.BOX_TYPE_FORWARD);
		box.setReceiveSiteCode(sorting.getReceiveSiteCode());
		box.setStatuses(Box.BOX_STATUS_SORT + Constants.SEPARATOR_COMMA + Box.BOX_STATUS_INSPECT_PROCESSING);
		List<Box> boxes = this.boxService.findBoxes(box);

		return StringHelper.join(boxes, "getCode", Constants.SEPARATOR_COMMA, Constants.SEPARATOR_APOSTROPHE);
	}

	public List<Sorting> findByBoxCode(Sorting sorting) {
		return this.sortingDao.findByBoxCode(sorting);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean canCancel(Sorting sorting) {
		//added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.canCancel", false, true);
				
		// sorting & send_d ---> cancel=1
		boolean result = this.canCancelSorting(sorting);

		if (Constants.BUSSINESS_TYPE_THIRD_PARTY == sorting.getType()) {
			// 更新三方验货异常比对表，由少验修改为正常
			this.canCancelInspectionEC(sorting);
		}
		Profiler.registerInfoEnd(info);
		//added end
		
		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean canCancelSorting(Sorting sorting) {
		boolean result = this.sortingDao.canCancel(sorting)
				&& this.deliveryService.canCancel(this.parseSendDetail(sorting));
		if (result) {
			this.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL);
		}
		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean canCancelSorting2(Sorting sorting) {
		boolean result = this.sortingDao.canCancel2(sorting)
				&& this.deliveryService.canCancel2(this.parseSendDetail(sorting));
		if (result) {
			this.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL);
		}
		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public Boolean canCancelSortingFuzzy(Sorting sorting) {
		boolean result = this.sortingDao.canCancelFuzzy(sorting)
				&& this.deliveryService.canCancelFuzzy(this.parseSendDetail(sorting));
		if (result) {
			this.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL);
		}
		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Boolean canCancelInspectionEC(Sorting sorting) {
		InspectionEC inspectionEC = new InspectionEC.Builder(sorting.getPackageCode(), sorting.getCreateSiteCode())
				.waybillCode(sorting.getWaybillCode()).boxCode(sorting.getBoxCode())
				.receiveSiteCode(sorting.getReceiveSiteCode()).updateUser(sorting.getUpdateUser())
				.updateUserCode(sorting.getUpdateUserCode())
				.updateTime(null == sorting.getUpdateTime() ? new Date() : sorting.getUpdateTime()).build();

		return this.inspectionECDao.updateYnByWaybillCode(inspectionEC) > 0;
	}

	private SendDetail parseSendDetail(Sorting sorting) {
		SendDetail sendDetail = new SendDetail();
		sendDetail.setSendType(sorting.getType());
		sendDetail.setCreateSiteCode(sorting.getCreateSiteCode());
		sendDetail.setPackageBarcode(sorting.getPackageCode());
		sendDetail.setWaybillCode(sorting.getWaybillCode());
		if (StringUtils.isNotBlank(sorting.getBoxCode())) {
			sendDetail.setBoxCode(sorting.getBoxCode());
		}
		return sendDetail;
	}

	private WaybillStatus parseWaybillStatus(Sorting sorting, BaseStaffSiteOrgDto createSite,
			BaseStaffSiteOrgDto receiveSite) {

		WaybillStatus waybillStatus = new WaybillStatus();

		waybillStatus.setWaybillCode(sorting.getWaybillCode());
		waybillStatus.setPackageCode(sorting.getPackageCode());
		waybillStatus.setBoxCode(sorting.getBoxCode());

		waybillStatus.setOrgId(createSite.getOrgId());
		waybillStatus.setOrgName(createSite.getOrgName());

		waybillStatus.setCreateSiteCode(sorting.getCreateSiteCode());
		waybillStatus.setCreateSiteName(sorting.getCreateSiteName());
		waybillStatus.setCreateSiteType(createSite.getSiteType());

		waybillStatus.setReceiveSiteCode(sorting.getReceiveSiteCode());
		waybillStatus.setReceiveSiteName(sorting.getReceiveSiteName());
		waybillStatus.setReceiveSiteType(receiveSite.getSiteType());

		waybillStatus.setOperatorId(sorting.getCreateUserCode());
		waybillStatus.setOperator(sorting.getCreateUser());
		waybillStatus.setOperateType(true == sorting.isForward() ? WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_SORTING
				: WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING);
		waybillStatus.setOperateTime(sorting.getOperateTime());
		return waybillStatus;
	}

	@SuppressWarnings("unused")
	private String getResult(BaseEntity<List<String>> sortingResult) {
		return sortingResult.getResultCode() + Constants.SEPARATOR_COMMA + sortingResult.getMessage()
				+ Constants.SEPARATOR_COMMA + sortingResult.getData().isEmpty();
	}

    @JProfiler(jKey= "DMSWORKER.SortingService.doSorting",mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean doSorting(Task task) {
        List<Sorting> sortings = this.prepareSorting(task);
        if (null == sortings || sortings.isEmpty()) {
            return Boolean.FALSE;
        } else {
            if(!this.taskToSorting(sortings)){
            	//离线取消分拣：如果取消失败，则隔15分钟重新处理
            	return Boolean.FALSE;
            }else{
            	return Boolean.TRUE;
            }
        }
    }

	private List<Sorting> prepareSorting(Task task) {
		if (StringHelper.isEmpty(task.getBody())) {
			return Collections.emptyList();
		}

		List<Sorting> sortings = new ArrayList<Sorting>();
		Sorting sorting = this.toSorting(task);

		//added by hanjiaxing
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.ReceiveSiteCodeMismatch", false, true);
		Box box = boxService.findBoxByCode(sorting.getBoxCode());
		if (box != null) {
			int boxReceiveSiteCode = box.getReceiveSiteCode();
			if (sorting.getReceiveSiteCode() != boxReceiveSiteCode) { //FIXME 可以不通过比较 直接使用box的receive_site_code
				logger.warn("sorting报文中的ReceiveSiteCode不匹配，报文内容：" + task.getBody());
				logger.warn("错误的ReceiveSiteCode：" + sorting.getReceiveSiteCode() + " 正确的ReceiveSiteCode：" + boxReceiveSiteCode);
				sorting.setReceiveSiteCode(boxReceiveSiteCode);
			}
		}
		Profiler.registerInfoEnd(info);
		//added end

		//added by huangliang
		CallerInfo info1 = Profiler.registerInfo("DMSWORKER.SortingService.getWaybillAndPackByWaybillCode", false, true);
		if (StringHelper.isEmpty(sorting.getPackageCode())) { // 按运单分拣
			this.logger.info("从运单系统获取包裹信息，运单号为：" + sorting.getWaybillCode());

			BaseEntity<BigWaybillDto> waybill = this.waybillQueryApi.getWaybillAndPackByWaybillCode(sorting
					.getWaybillCode());
			if (waybill != null && waybill.getData() != null) {
				List<DeliveryPackageD> packages = waybill.getData().getPackageList();
				if (BusinessHelper.checkIntNumRange(packages.size())) {
					for (DeliveryPackageD aPackage : packages) {
						this.logger.info("运单包裹号为：" + aPackage.getPackageBarcode());
						Sorting assemblyDomain = new Sorting();
						BeanUtils.copyProperties(sorting, assemblyDomain);//FIXME:性能
						assemblyDomain.setPackageCode(aPackage.getPackageBarcode());
						if (!BusinessHelper.isBoxcode(sorting.getBoxCode())) {
							assemblyDomain.setBoxCode(aPackage.getPackageBarcode());
						}
						sortings.add(assemblyDomain);
					}
				} else {
					MonitorAlarm.pushAlarm(MonitorAlarm.UMP_WORKER_SORINTG_WAYBILL_EMPTY, "分拣Worker运单为空，运单号为:"
							+ sorting.getWaybillCode());
				}
			}
		} else { // 按包裹分拣
			sortings.add(sorting);
		}
		Profiler.registerInfoEnd(info1);
		//added end
		
		Collections.sort(sortings);
		return sortings;
	}

	@Override
	public boolean taskToSorting(List<Sorting> sortings) {
		for (Sorting sorting : sortings) {
			if (sorting.getIsCancel().equals(SORTING_CANCEL_NORMAL)) {
				this.addSorting(sorting, null); // 添加分拣记录
				this.addSendDetail(sorting, null); // 添加发货记录 FIXME:非主线任务
				this.addSortingAdditionalTask(sorting);// 添加回传分拣的运单状态
				// this.updatedBoxStatus(sorting); // 将箱号更新为分拣状态
			} else if (sorting.getIsCancel().equals(SORTING_CANCEL)) {// 离线取消分拣
				return this.canCancel(sorting);
			}
		}
		return true;
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.sorting.getSendMSelective", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	public SendM getSendMSelective(Sorting sorting){
		SendM result = null;
		try{
			SendM sendM = new SendM();
			sendM.setCreateSiteCode(sorting.getCreateSiteCode());
			sendM.setBoxCode(sorting.getBoxCode());
			sendM.setReceiveSiteCode(sorting.getReceiveSiteCode());
			logger.warn("first. find sendms from redis");
			result = getDeliveryInfoFromRedis(sendM);
			if(null == result) {
				logger.warn("second. find sendms from db");
				result = getDeliveryInfoFromDB(sendM);
				if(null != result){
					logger.warn("third. save sendm from db to redis");
					saveDeliveryInfo2Redis(result);
				}
			}
		}catch (Throwable ex){
			logger.error("find sendm selective error ", ex);
		}
		return result;
	}

	public SendM getDeliveryInfoFromDB(SendM sendM){
		List<SendM> sendMs = sendMDao.findSendMByBoxCode(sendM);
		if(null != sendMs && !sendMs.isEmpty()) {
			logger.warn("find senm from db success value <"
						+ JsonHelper.toJson(sendMs.get(0)) + ">");
			return sendMs.get(0);
		}
		logger.warn("find senm from db fail value <null>");
		return null;
	}

	public void saveDeliveryInfo2Redis(SendM sendM){
		String cachedKey = CacheKeyConstants.REDIS_KEY_IS_DELIVERY
				+ sendM.getCreateSiteCode()
				+ sendM.getBoxCode();
		Boolean isExist = redisManager.exists(cachedKey);
		Long result = redisManager.lpushCache(cachedKey, JsonHelper.toJson(sendM));
		if(result <= 0){
			logger.warn("save to redis of key <" + cachedKey
						+ "> value <" + JsonHelper.toJson(sendM) + "> fail");

		}else{
			logger.warn("save to redis of key <" + cachedKey
					+ "> value <" + JsonHelper.toJson(sendM) + "> success");
			if(!isExist){
				// 如果是列表key是第一次插入的，则设置整体的超时时间
				Boolean expireResult = redisManager.expire(cachedKey, DELIVERY_INFO_EXPIRE_SCONDS);
				if(!expireResult){
					logger.warn("set expire of key <" + cachedKey
							+ "> second <" + DELIVERY_INFO_EXPIRE_SCONDS
							+ "> fail. result " + expireResult);
				}else{
					logger.warn("set expire of key <" + cachedKey
							+ "> second <" + DELIVERY_INFO_EXPIRE_SCONDS
							+ "> success. result " + expireResult);
				}
			}
		}

	}

	public SendM getDeliveryInfoFromRedis(SendM sendM){
		String cachedKey = CacheKeyConstants.REDIS_KEY_IS_DELIVERY
							+ sendM.getCreateSiteCode()
							+ sendM.getBoxCode();
		try {
			for (String sendmJson : redisManager.lrangeCache(cachedKey, 0, -1)) {
				SendM cachedSenm = JsonHelper.fromJson(sendmJson, SendM.class);
				if (cachedSenm.getReceiveSiteCode().equals(sendM.getReceiveSiteCode())) {
					logger.warn("find sendm by key <" + cachedKey
								+ "> value <" + sendmJson + "> success");
					return cachedSenm;
				}
			}
		} catch (Exception ex){
			logger.warn("find sendm by key <" + cachedKey + "> fail error <"
						+ ex.getMessage() + ">");
			return null;
		}

		return null;
	}

	private void addSortingAdditionalTask(Sorting sorting) {
		//added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.addSortingAdditionalTask", false, true);
				
		// prepare:
		// 拆分分析字段
		Integer createSiteCode = sorting.getCreateSiteCode();
		Integer receiveSiteCode = sorting.getReceiveSiteCode();

		if (StringHelper.isEmpty(sorting.getPackageCode()) || !NumberHelper.isPositiveNumber(createSiteCode)
				|| !NumberHelper.isPositiveNumber(receiveSiteCode)
				|| !NumberHelper.isPositiveNumber(sorting.getUpdateUserCode())
				|| StringHelper.isEmpty(sorting.getUpdateUser())) {
			this.logger.error("分拣记录某数据项为空");
			return;
		}

		BaseStaffSiteOrgDto createSite = null;
		BaseStaffSiteOrgDto receiveSite = null;
		try {
			createSite = this.baseMajorManager.getBaseSiteBySiteId(createSiteCode);
			receiveSite = this.baseMajorManager.getBaseSiteBySiteId(receiveSiteCode);
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
		}
		if (createSite == null)
			createSite = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(createSiteCode));
		
		if (receiveSite == null)
			receiveSite = baseMajorManager.queryDmsBaseSiteByCodeDmsver(String.valueOf(receiveSiteCode));
			
		if (createSite == null || receiveSite == null) {
			this.logger.warn("创建站点或接收站点信息为空.");
			this.logger.info("创建站点：" + createSiteCode);
			this.logger.info("接收站点：" + receiveSiteCode);
			return;
		}
		WaybillStatus waybillStatus = this.parseWaybillStatus(sorting, createSite, receiveSite);
		String jsonStr = JsonHelper.toJson(waybillStatus);
		Task task = new Task();
		task.setBody(jsonStr);
		task.setFingerprint(Md5Helper.encode(sorting.getCreateSiteCode() + "_" + sorting.getReceiveSiteCode() + "_"
				+ sorting.getType() + "_" + sorting.getWaybillCode() + "_" + sorting.getPackageCode() + "_"
				+ System.currentTimeMillis()));
		task.setBoxCode(sorting.getBoxCode());
		task.setCreateSiteCode(sorting.getCreateSiteCode());
		task.setCreateTime(sorting.getOperateTime());
		task.setKeyword1(sorting.getWaybillCode());
		task.setKeyword2(sorting.getPackageCode());
		task.setReceiveSiteCode(sorting.getReceiveSiteCode());
		task.setType(true == sorting.isForward() ? WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_SORTING
				: WaybillStatus.WAYBILL_STATUS_CODE_REVERSE_SORTING);
		task.setTableName(Task.TABLE_NAME_WAYBILL);
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setOwnSign(BusinessHelper.getOwnSign());
		this.taskService.add(task);
		
		Profiler.registerInfoEnd(info);
		//added end
	}

	public Sorting toSorting(Task task) {
		String body = task.getBody().substring(1, task.getBody().length() - 1);
		SortingRequest request = JsonHelper.jsonToArray(body, SortingRequest.class);
		if (request != null) {
			Sorting sorting = Sorting.toSorting(request);
			sorting.setStatus(Sorting.STATUS_DONE);// 运单回传状态默认为1，以后可以去掉
			return sorting;
		}
		return null;
	}

	private void fillSortingIfPickup(Sorting sorting) {
		if (BusinessHelper.isPickupCode(sorting.getPackageCode())) {
//            sorting.setPackageCode(SerialRuleUtil.getWaybillCode(sorting.getPackageCode()));
			//包裹号写到运单字段bug修改    packagecode存包裹号  waybillcode存换单后单号即W单      add by lhc   2016.12.21
			sorting.setPackageCode(sorting.getPackageCode());
            if(BusinessHelper.isPickupCodeWW(sorting.getPackageCode()))
            {
               // sorting.setPickupCode(pickup.getData().getPickupCode());
                sorting.setWaybillCode(sorting.getPackageCode());
			} else {
			BaseEntity<PickupTask> pickup = this.getPickup(SerialRuleUtil.getWaybillCode(sorting.getPackageCode()));
			if (pickup != null&&pickup.getData()!=null) {
				sorting.setPickupCode(pickup.getData().getPickupCode());
//				sorting.setWaybillCode(pickup.getData().getOldWaybillCode());
				sorting.setWaybillCode(pickup.getData().getSurfaceCode());
			}}
		}
	}
	
	private void saveOrUpdate(Sorting sorting) {
		if (Constants.NO_MATCH_DATA == this.update(sorting).intValue()) {
			this.add(sorting);
		}
	}

	/**
	 * 验货异常比对表插入数据
	 * 
	 * @param sorting
	 */
	private void saveOrUpdateInspectionEC(Sorting sorting) {//FIXME:包装构建方法

		if (Constants.BUSSINESS_TYPE_THIRD_PARTY != sorting.getType()) {
			return;
		}

		InspectionEC inspectionECSel = new InspectionEC.Builder(sorting.getPackageCode(), sorting.getCreateSiteCode())
				.boxCode(sorting.getBoxCode()).receiveSiteCode(sorting.getReceiveSiteCode())
				.inspectionType(sorting.getType()).yn(1).build();
		List<InspectionEC> preInspectionEC = this.inspectionECDao.selectSelective(inspectionECSel);
		if (!preInspectionEC.isEmpty()
				&& InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED <= preInspectionEC.get(0).getStatus()) {
			this.logger.info("包裹已经异常比较，再次分拣时不操作三方异常比对记录，包裹号：" + sorting.getPackageCode());
			return;
		}

		InspectionEC inspectionEC = new InspectionEC.Builder(sorting.getPackageCode(), sorting.getCreateSiteCode())
				.boxCode(sorting.getBoxCode()).receiveSiteCode(sorting.getReceiveSiteCode())
				.waybillCode(sorting.getWaybillCode()).inspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE)
				.status(InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED).inspectionType(sorting.getType())
				.createUser(sorting.getUpdateUser()).createUserCode(sorting.getUpdateUserCode())
				.createTime(sorting.getUpdateTime()).updateUser(sorting.getUpdateUser())
				.updateUserCode(sorting.getUpdateUserCode())
				.updateTime(null == sorting.getUpdateTime() ? new Date() : sorting.getUpdateTime()).build();

		if (!preInspectionEC.isEmpty()
				&& preInspectionEC.get(0).getInspectionECType() == InspectionEC.INSPECTIONEC_TYPE_MORE) {
			this.inspectionECDao.updateOne(inspectionEC);
		} else if (preInspectionEC.isEmpty()) {
			// insert表示该记录还不存在，验货时没有插入，在此验货异常类型为少验
			inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
			inspectionEC.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_UNHANDLED);
			inspectionEC.setCreateTime(sorting.getOperateTime());
			this.inspectionECDao.add(InspectionECDao.namespace, inspectionEC);
		}
	}
	
	private void addSorting(Sorting sorting, DeliveryPackageD aPackage) {
		//added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.addSorting", false, true);
		if (aPackage != null) {
			sorting.setPackageCode(aPackage.getPackageBarcode());
			if (!BusinessHelper.isBoxcode(sorting.getBoxCode())) {
				sorting.setBoxCode(aPackage.getPackageBarcode());
			}
		}

		this.fillSortingIfPickup(sorting);
		this.saveOrUpdate(sorting);
		this.saveOrUpdateInspectionEC(sorting);//FIXME:差异处理拿出
		this.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING);//日志拿出
		this.notifyBlocker(sorting);//FIXME:可以异步发送拿出
		this.backwardSendMQ(sorting);
		Profiler.registerInfoEnd(info);
		//added end
	}
	
	/**
	 * 正向【分拣理货】所有逆向订单发送topic是blockerComOrbrefundRq的mq,供快退系统和拦截系统消费.
	 * add by lhc
	 * 2016.08.17
	 * @param sorting
	 */
	public void backwardSendMQ(Sorting sorting){
		String wayBillCode = sorting.getWaybillCode();
//		String wayBillCode = "T42747129215";//VA00080450101
		// 验证运单号
		if(wayBillCode != null){
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(wayBillCode, wChoice);
			if(baseEntity != null && baseEntity.getData() != null){
				Waybill waybill = baseEntity.getData().getWaybill();
				if(waybill != null){
					String waybillsign = waybill.getWaybillSign();
					if(waybillsign != null && waybillsign.length()>0){
						//waybillsign  1=T  ||  waybillsign  15=6表示逆向订单
						if((waybill.getWaybillSign().charAt(0)=='T' || waybill.getWaybillSign().charAt(14)=='6') && waybill.getWaybillSign().charAt(34) != '2'){
							//组装FastRefundBlockerComplete
							FastRefundBlockerComplete frbc = toMakeFastRefundBlockerComplete(sorting);
							String json = JsonHelper.toJson(frbc);
							this.logger.info("分拣中心逆向订单快退:MQ[" + json + "]");
							try {
								blockerComOrbrefundRqMQ.send(wayBillCode,json);
							} catch (Exception e) {
								this.logger.error("分拣中心逆向订单快退MQ失败[" + json + "]:" + e.getMessage(), e);
							}
						}else{
							logger.info(waybillsign + "对应的订单为非逆向订单！");
						}
					}
				}else{
					logger.info(wayBillCode + "对应的运单信息为空！");
				}
			}
		}
	}
	
	/**
	 * 
	 * @param sorting
	 * @return
	 */
	private FastRefundBlockerComplete toMakeFastRefundBlockerComplete(Sorting sorting){
		FastRefundBlockerComplete frbc = new FastRefundBlockerComplete();
		//新运单号获取老运单号的所有信息  参数返单号
		try{
			BaseEntity<Waybill> wayBillOld = waybillQueryApi.getWaybillByReturnWaybillCode(sorting.getWaybillCode());
			if(wayBillOld.getData() != null){
				String vendorId = wayBillOld.getData().getVendorId();
				if(vendorId == null || "".equals(vendorId)){
					frbc.setOrderId("0");//没有订单号的外单,是非京东平台上下的订单
				}else{
					frbc.setOrderId(vendorId);
				}
			}else{
				frbc.setOrderId("0");
			}
		}catch(Exception e){
			this.logger.error("发送blockerComOrbrefundRq的MQ时新运单号获取老运单号失败,waybillcode:[" + sorting.getWaybillCode() + "]:" + e.getMessage(), e);
		}
		frbc.setWaybillcode(sorting.getWaybillCode());
		frbc.setApplyReason("分拣中心快速退款");
		frbc.setApplyDate(sorting.getOperateTime().getTime());
		frbc.setSystemId(87);//blockerComOrbrefundRq的systemId设定为87
		frbc.setReqErp(String.valueOf(sorting.getCreateUserCode()));
		frbc.setReqName(sorting.getCreateUser());
		frbc.setOrderType(sorting.getType());
		frbc.setMessageType("BLOCKER_QUEUE_DMS_REVERSE_PRINT");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		frbc.setOperatTime(dateFormat.format(sorting.getOperateTime()));
		frbc.setSys("ql.dms");
		
		return frbc;
	}

	@Override
	public void addOpetationLog(Sorting sorting, Integer logType) {
		OperationLog operationLog = this.parseOperationLog(sorting, logType, "");
		this.operationLogService.add(operationLog);
	}

	@Override
	public void addOpetationLog(Sorting sorting, Integer logType, String remark) {
		OperationLog operationLog = this.parseOperationLog(sorting, logType, remark);
		this.operationLogService.add(operationLog);
	}

	private OperationLog parseOperationLog(Sorting sorting, Integer logType, String remark) {
		OperationLog operationLog = new OperationLog();
		operationLog.setBoxCode(sorting.getBoxCode());
		operationLog.setWaybillCode(sorting.getWaybillCode());
		operationLog.setPackageCode(sorting.getPackageCode());
		operationLog.setCreateSiteCode(sorting.getCreateSiteCode());
		operationLog.setCreateSiteName(sorting.getCreateSiteName());
		operationLog.setReceiveSiteCode(sorting.getReceiveSiteCode());
		operationLog.setReceiveSiteName(sorting.getReceiveSiteName());
		String createUser = StringUtils.isBlank(sorting.getCreateUser()) ? sorting.getUpdateUser() : sorting
				.getCreateUser();
		operationLog.setCreateUser(createUser);
		Integer createUserCode = null == sorting.getCreateUserCode() || sorting.getCreateUserCode() > 0 ? sorting
				.getUpdateUserCode() : sorting.getCreateUserCode();
		operationLog.setCreateUserCode(createUserCode);
		operationLog.setCreateTime(new Date());
		operationLog.setOperateTime(sorting.getOperateTime());
		operationLog.setLogType(logType);
		operationLog.setRemark(remark);
		return operationLog;
	}

	private void addSendDetail(Sorting sorting, DeliveryPackageD aPackage) {
		//added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.addSendDetail", false, true);
		
		SendDetail sendDetail = SendDetail.toSendDatail(sorting);
        sendDetail.setOperateTime(new Date(sorting.getOperateTime().getTime() + 30000));
		if (aPackage != null) {
			sendDetail.setPackageBarcode(aPackage.getPackageBarcode());
			if (!BusinessHelper.isBoxcode(sorting.getBoxCode())) {
				sendDetail.setBoxCode(aPackage.getPackageBarcode());
			}
		}

		this.fillSendDetailIfPickup(sendDetail);
		/* 补齐包裹重量 */
		// String retrieveFlag =
		// PropertiesHelper.newInstance().getValue("enablePackageRetrieve");
		// if( StringUtils.isNotBlank(retrieveFlag) &&
		// "sure".equals(retrieveFlag)){
		sendDetail = this.deliveryService.measureRetrieve(sendDetail);
		// }
        SendM sendM = getSendMSelective(sorting);
        // 如果是正向、已经发货，则需要直接更新发货明细表（send_d)发货批次号(sendCode)
        /*updated by wangtingwei@jd.com  正向逆向三方发货全部补全数据，下线com.jd.bluedragon.distribution.worker.delivery.ToSendwaybillTask该WORKER*/
        if(null != sendM){
            sendDetail.setSendCode(sendM.getSendCode()); // 补全sendcode
			this.deliveryService.saveOrUpdate(sendDetail);       // 更新或者插入发货明细表
			sendDetail.setYn(1);
            /*取SENDM创建人，作为全程跟踪发货人，以及操作时间*/
			sendDetail.setOperateTime(sendM.getOperateTime());
			sendDetail.setCreateUser(sendM.getCreateUser());
			sendDetail.setCreateUserCode(sendM.getCreateUserCode());
			List<SendDetail> sendDetails = new ArrayList<SendDetail>();
			sendDetails.add(sendDetail);
			deliveryService.updateWaybillStatus(sendDetails);	 // 回传发货全程跟踪
        }else{
            this.deliveryService.saveOrUpdate(sendDetail);
        }
        try {
            if(BusinessHelper.isBoxcode(sorting.getBoxCode())) {
                List<SendM> sendList = this.deliveryService.getSendMListByBoxCode(sorting.getBoxCode());
                if (null != sendList && sendList.size() > 0) {
                    for (SendM sendM1 : sendList) {
                        if (sendM1.getCreateSiteCode().equals(sorting.getCreateSiteCode()) && sendM1.getReceiveSiteCode().equals(sorting.getReceiveSiteCode())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("过滤站点一致补全" + sorting.getWaybillCode());
                            }
                            continue;
                        }
                        if (sendM1.getOperateTime().before(sorting.getOperateTime())) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("过滤发货在前分拣在后数据" + sorting.getWaybillCode());
                            }
                            continue;
                        }
                        if (logger.isInfoEnabled()) {
                            logger.info("分拣中转全程跟踪补全发货批次号为：" + sendM1.getSendCode() + "运单号为" + sorting.getPackageCode());
                        }
                        sendDetail.setCreateSiteCode(sendM1.getCreateSiteCode());
                        sendDetail.setReceiveSiteCode(sendM1.getReceiveSiteCode());
                        sendDetail.setSendCode(sendM1.getSendCode()); // 补全sendcode
                        this.deliveryService.saveOrUpdate(sendDetail);       // 更新或者插入发货明细表
                        sendDetail.setYn(1);
                /*取SENDM创建人，作为全程跟踪发货人，以及操作时间*/
                        sendDetail.setOperateTime(sendM1.getOperateTime());
                        sendDetail.setCreateUser(sendM1.getCreateUser());
                        sendDetail.setCreateUserCode(sendM1.getCreateUserCode());
                        List<SendDetail> sendDetails = new ArrayList<SendDetail>();
                        sendDetails.add(sendDetail);
                        deliveryService.updateWaybillStatus(sendDetails);     // 回传发货全程跟踪
                    }
                }
            }
        }catch (Exception ex){
            if(logger.isErrorEnabled()) {
                logger.error("分拣补中转发货异常" + sorting.getPackageCode());
            }
        }
		Profiler.registerInfoEnd(info);
		//added end
	}

	@JProfiler(jKey = "Bluedragon_dms_center.dms.method.sorting.getReadSendM", mState = {
			JProEnum.TP, JProEnum.FunctionError })
	private SendM getReadOnlySendM(Sorting sorting){
		try {
			SendM sendM = new SendM();
			sendM.setCreateSiteCode(sorting.getCreateSiteCode());
			sendM.setBoxCode(sorting.getBoxCode());
			sendM.setReceiveSiteCode(sorting.getReceiveSiteCode());
			List<SendM> sendMs = sendMReadDao.findSendMByBoxCode(sendM);
			if (null != sendMs && !sendMs.isEmpty()) {
				logger.warn("find senm from db success value <"
						+ JsonHelper.toJson(sendMs.get(0)) + ">");
				return sendMs.get(0);
			}
		} catch (Exception ex) {
			logger.warn("find senm from db throws exception ", ex);
			return null;
		}
		logger.warn("find senm from db fail value <null>");
		return null;
	}

	private void fillSendDetailIfPickup(SendDetail sendDetail) {
		if (BusinessHelper.isPickupCode(sendDetail.getPackageBarcode())) {
            if(BusinessHelper.isPickupCodeWW(sendDetail.getPackageBarcode())) {
                sendDetail.setWaybillCode(sendDetail.getPackageBarcode());
			} else {
//			BaseEntity<PickupTask> pickup = this.getPickup(sendDetail.getPackageBarcode());
			//包裹号写到运单字段bug修改    packagecode存包裹号  waybillcode存换单后单号即W单      add by lhc   2016.12.21
			BaseEntity<PickupTask> pickup = this.getPickup(SerialRuleUtil.getWaybillCode(sendDetail.getPackageBarcode()));
			if (pickup != null&&pickup.getData()!=null) {
				sendDetail.setPickupCode(pickup.getData().getPickupCode());
//				sendDetail.setWaybillCode(pickup.getData().getOldWaybillCode());
				sendDetail.setWaybillCode(pickup.getData().getSurfaceCode());
				}
			}
		}
	}

	private BaseEntity<PickupTask> getPickup(String packageCode) {
		BaseEntity<PickupTask> pickup = this.waybillPickupTaskApi.getDataBySfCode(packageCode);
		if (pickup != null&&pickup.getData()!=null) {
            if(logger.isInfoEnabled()) {
                this.logger.info("取件单号码为：" + pickup.getData().getPickupCode());
                this.logger.info("取件单对应运单号码为：" + pickup.getData().getOldWaybillCode());
            }
		}
		return pickup;
	}

	public List<Sorting> findOrderDetail(Sorting sorting) {
		return this.sortingDao.findOrderDetail(sorting);
	}

	public List<Sorting> findOrder(Sorting sorting) {
		return this.sortingDao.findOrder(sorting);
	}

	@Override
	public int findBoxPack(Integer createSiteCode, String boxCode) {
		this.logger.debug("根据箱号获取包裹信息 --> 开始获取包裹总数");
		return this.sortingDao.findPackCount(createSiteCode, boxCode);
	}

	@Override
	public Sorting findBoxDescSite(Integer createSiteCode, String boxCode) {
		this.logger.debug("根据箱号获取包裹信息 --> 根据箱号获取包裹分拣目的站点信息");
		return this.sortingDao.findBoxDescSite(createSiteCode, boxCode);
	}

	@Override
	public List<Sorting> findBoxPackList(Sorting sorting) {
		this.logger.debug("获取包裹信息 --> 根据订单号或包裹号查询箱号包裹信息");
		return this.sortingDao.findBoxPackList(sorting);
	}

	/**
	 * 根据包裹号或者运单号查询箱子、create_site_code、receive_site_code
	 * 
	 * @param sorting
	 * @return
	 */
	public List<Sorting> queryByCode(Sorting sorting) {
		this.logger.debug("获取包裹信息 --> 根据订单号或包裹号查询箱号、创建站点、接收站点");
		return this.sortingDao.queryByCode(sorting);
	}
	
	/**
	 * 根据包裹号或者运单号查询箱子、create_site_code、receive_site_code
	 *
	 * @param sorting
	 * @return
	 */
	public List<Sorting> queryByCode2(Sorting sorting) {
		this.logger.debug("获取包裹信息 --> 根据订单号或包裹号查询箱号、创建站点、接收站点");
		return this.sortingDao.queryByCode2(sorting);
	}

	/**
	 * @param sorting
	 * @return
	 */
	public Boolean canCancel2(Sorting sorting) {
		// sorting & send_d ---> cancel=1
		boolean result = this.canCancelSorting2(sorting);

		if (Constants.BUSSINESS_TYPE_THIRD_PARTY == sorting.getType()) {
			// 更新三方验货异常比对表，由少验修改为正常
			this.canCancelInspectionEC(sorting);
		}
		return result;
	}

	public void notifyBlocker(Sorting sorting) {
		try {
			if (Sorting.TYPE_REVERSE.equals(sorting.getType())) {
//					&& waybillCancelService.isRefundWaybill(sorting.getWaybillCode())) {
                String refundMessage = this.refundMessage(sorting.getWaybillCode(),
						DateHelper.formatDateTimeMs(sorting.getOperateTime()));
                //bd_blocker_complete的MQ
				this.bdBlockerCompleteMQ.send( sorting.getWaybillCode(),refundMessage);
				//【逆向分拣理货】增加orbrefundRqMQ  add by lhc  2016.8.17
				//这里需要暂时注释掉 逆向取件单不应该发送快退的mq,属于售后的范围  modified by zhanglei 20161025
//				fastRefundService.execRefund(sorting);
			}
		} catch (Exception e) {
			this.logger.error("回传退款100分逆向分拣信息失败，运单号：" + sorting.getWaybillCode(), e);
            try{
                SystemLogUtil.log(sorting.getWaybillCode(),"BLOCKER_QUEUE_DMS","",sorting.getType(),e.getMessage(),Long.valueOf(12201));
            }catch (Exception ex){
                logger.error("退款100分MQ消息推送记录日志失败", ex);
            }
		}
	}
	
	public BigWaybillDto queryWaybillByCode(String waybillCode){
		BigWaybillDto dto = waybillService.getWaybill(waybillCode);
		if(dto!=null){
			return dto;
		}else{
			return null;
		}
	}

	public String refundMessage(String waybillCode, String operateTime) {
		StringBuilder message = new StringBuilder();
		message.append("<?xml version=\"1.0\" encoding=\"utf-16\"?>");
		message.append("<OrderTaskInfo xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
		message.append("<OrderId>" + waybillCode + "</OrderId>");
		message.append("<OrderType>20</OrderType>");
		message.append("<MessageType>BLOCKER_QUEUE_DMS</MessageType>");
		message.append("<OperatTime>" + operateTime + "</OperatTime>");
		message.append("</OrderTaskInfo>");

		return message.toString();
	}

	@Override
	public List<Sorting> findByBsendCode(Sorting sorting) {
		// TODO Auto-generated method stub
		return this.sortingDao.findByBsendCode(sorting);
	}
	
	public static void main(String args[]){
//		SortingServiceImpl impl = new SortingServiceImpl();
//		Sorting sorting = new Sorting();
//		sorting.setWaybillCode("T42747129215");
//		impl.backwardSendMQ(sorting);
		String waybillsign = "T0000000000000200000000000000000000000000000000000";
		String b = waybillsign.substring(15,16);
		String a = waybillsign.substring(0,1);
		if("T".equals(a) || "6".equals(b)){
			System.out.println(a);
		}
		
	}
}
