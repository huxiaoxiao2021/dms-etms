package com.jd.bluedragon.distribution.sorting.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.MonitorAlarm;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.fastRefund.domain.FastRefundBlockerComplete;
import com.jd.bluedragon.distribution.fastRefund.service.FastRefundService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
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
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.alibaba.fastjson.JSON;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service("sortingService")
public class SortingServiceImpl implements SortingService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static Integer DELIVERY_INFO_EXPIRE_SCONDS = 30 * 60; //半小时

	public static final int TASK_1200_EX_TIME_5_S = 5;//1200分拣任务防重复提交执行，10秒时间
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
	private WaybillPickupTaskApi waybillPickupTaskApi;

	@Autowired
	private InspectionDao inspectionDao;

	@Qualifier("bdBlockerCompleteMQ")
	@Autowired
	private DefaultJMQProducer bdBlockerCompleteMQ;

	@Qualifier("blockerComOrbrefundRqMQ")
	@Autowired
	private DefaultJMQProducer blockerComOrbrefundRqMQ;

	@Autowired
	private InspectionECDao inspectionECDao;

	@Autowired
	private RedisManager redisManager;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	WaybillService waybillService;

	@Autowired
	FastRefundService fastRefundService;


	@Autowired
	private InspectionExceptionService inspectionExcpetionService;

	@Autowired
	private InspectionService inspectionService;

	@Autowired
	private SendMDao sendMDao;

	@Autowired
	private WaybillPackageManager waybillPackageManager;

    /**
     * sorting任务处理告警时间，单位:ms，默认值100
     */
	@Value("${beans.SortingServiceImpl.sortingDealWarnTime:100}")
	private long sortingDealWarnTime;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(Sorting sorting) {
		return this.sortingDao.add(SortingDao.namespace, sorting);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(Sorting sorting) {
		return this.sortingDao.update(SortingDao.namespace, sorting);
	}

	public boolean existSortingByPackageCode(Sorting sorting) {
		if (this.sortingDao.existSortingByPackageCode(sorting) > 0) {
			return true;
		} else {
			return false;
		}
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
		//fixme sorting send_d 分布式事务问题
		boolean result = this.sortingDao.canCancel2(sorting)
				&& this.deliveryService.canCancel2(this.parseSendDetail(sorting));
		if (result) {
			this.addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL);
			//发送取消建箱全程跟踪，MQ
			this.sendSortingCancelWaybillTrace(sorting);
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
		sendDetail.setReceiveSiteCode(sorting.getReceiveSiteCode());
		if (StringUtils.isNotBlank(sorting.getBoxCode())) {
			sendDetail.setBoxCode(sorting.getBoxCode());
		}
		return sendDetail;
	}

    private WaybillStatus parseWaybillStatus(Sorting sorting, BaseStaffSiteOrgDto createSite,
                                             BaseStaffSiteOrgDto receiveSite) {

        WaybillStatus waybillStatus = new WaybillStatus();

        waybillStatus.setWaybillCode(sorting.getWaybillCode());
        if(StringUtils.isNotBlank(sorting.getPackageCode())){
			waybillStatus.setPackageCode(sorting.getPackageCode());
		}else{
			waybillStatus.setPackageCode(sorting.getWaybillCode());
		}
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

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean doSorting(Task task) {

		CallerInfo doSorting = ProfilerHelper.registerInfo("DMSWORKER.SortingService.doSorting",
				Constants.UMP_APP_NAME_DMSWORKER);
		//记录本次分拣处理的包裹数量
		int sortingNum = 0;
		//记录本次分拣处理的结果
		boolean result = false;
		long beginTime = System.currentTimeMillis();
		//1、pre单独加入监控点
		CallerInfo step1Monitor = ProfilerHelper.registerInfo("DMSWORKER.SortingService.doSorting.prepareSorting",
				Constants.UMP_APP_NAME_DMSWORKER);
		List<Sorting> sortings = this.prepareSorting(task);
		Profiler.registerInfoEnd(step1Monitor);
		long preEndTime = System.currentTimeMillis();
		if (sortings != null) {
			sortingNum = sortings.size();
		}
		//2、doSorting监控
		CallerInfo step2Monitor = ProfilerHelper.registerInfo(
				ProfilerHelper.genKeyByQuantity("DMSWORKER.SortingService.doSorting.deal", sortingNum),
				Constants.UMP_APP_NAME_DMSWORKER);
		//离线取消分拣：如果取消失败，则隔15分钟重新处理
		if (sortingNum > 0) {
			result = this.taskToSorting(sortings);
		} else {
			logger.warn("fail-doSorting:本次处理的包裹数为0，task:"+JsonHelper.toJson(task));
		}
		Profiler.registerInfoEnd(step2Monitor);
		//耗时较长时，打印日志
		long costTimeTotal = System.currentTimeMillis() - beginTime;
		if(costTimeTotal >= sortingDealWarnTime){
			long preCostTime = preEndTime - beginTime;
			logger.warn("warn-doSorting-处理的包裹数:"+sortingNum+" 耗时：【pre:"+preCostTime+" ms,total:"+costTimeTotal+" ms】"+"task:"+JsonHelper.toJson(task));
		}
		Profiler.registerInfoEnd(doSorting);
		return result;
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

			BaseEntity<BigWaybillDto> waybill = this.waybillQueryManager.getWaybillAndPackByWaybillCode(sorting
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
		List<SendDetail> sendDList = new ArrayList<SendDetail>();
		for (Sorting sorting : sortings) {
			if (sorting.getIsCancel().equals(SORTING_CANCEL_NORMAL)) {
				this.b2bPushInspection(sorting);//B网建箱自动触发验货全程跟踪
				this.addSorting(sorting, null); // 添加分拣记录
				this.addSortingAdditionalTask(sorting); // 添加回传分拣的运单状态
				// this.updatedBoxStatus(sorting); // 将箱号更新为分拣状态
				// 添加发货记录 FIXME:非主线任务
				sendDList.add(this.addSendDetail(sorting));
			} else if (sorting.getIsCancel().equals(SORTING_CANCEL)) {// 离线取消分拣
				return this.canCancel(sorting);
			}
		}
		this.fixSendDAndSendTrack(sortings.get(0), sendDList);
		return true;
	}

	public SendM getSendMSelective(Sorting sorting){
        CallerInfo info = Profiler.registerInfo("Bluedragon_dms_center.dms.method.sorting.getSendMSelective", Constants.UMP_APP_NAME_DMSWEB,false, true);
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
			Profiler.functionError(info);
			logger.error("find sendm selective error ", ex);
		}finally {
			Profiler.registerInfoEnd(info);
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

	/**
	 * 添加回传分拣的运单状态
	 * @param sorting
	 */
	@Override
	public void addSortingAdditionalTask(Sorting sorting) {
		//added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.addSortingAdditionalTask", false, true);

		// prepare:
		// 拆分分析字段
		Integer createSiteCode = sorting.getCreateSiteCode();
		Integer receiveSiteCode = sorting.getReceiveSiteCode();

		if ((StringHelper.isEmpty(sorting.getPackageCode())&&StringHelper.isEmpty(sorting.getWaybillCode())) || !NumberHelper.isPositiveNumber(createSiteCode)
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

	public void fillSortingIfPickup(Sorting sorting) {
		if (WaybillUtil.isSurfaceCode(sorting.getPackageCode())) {
//            sorting.setPackageCode(SerialRuleUtil.getWaybillCode(sorting.getPackageCode()));
			//包裹号写到运单字段bug修改    packagecode存包裹号  waybillcode存换单后单号即W单      add by lhc   2016.12.21
//			sorting.setPackageCode(sorting.getPackageCode());
			if(WaybillUtil.isPickupCodeWW(sorting.getPackageCode()))
			{
				// sorting.setPickupCode(pickup.getData().getPickupCode());
				sorting.setWaybillCode(sorting.getPackageCode());
			} else {
				BaseEntity<PickupTask> pickup = this.getPickup(SerialRuleUtil.getWaybillCode(sorting.getPackageCode()));
				if (pickup != null&&pickup.getData()!=null) {
					sorting.setPickupCode(pickup.getData().getPickupCode());
//                  sorting.setWaybillCode(pickup.getData().getOldWaybillCode());
					sorting.setWaybillCode(pickup.getData().getSurfaceCode());
				}}
		}
	}

	public void saveOrUpdate(Sorting sorting) {
		if (Constants.NO_MATCH_DATA == this.update(sorting).intValue()) {
			this.add(sorting);
		}
	}

	/**
	 * 验货异常比对表插入数据
	 *
	 * @param sorting
	 */
	public void saveOrUpdateInspectionEC(Sorting sorting) {//FIXME:包装构建方法

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
			wChoice.setQueryWaybillE(false);
			wChoice.setQueryWaybillM(false);
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(wayBillCode, wChoice);
			if(baseEntity != null && baseEntity.getData() != null){
				Waybill waybill = baseEntity.getData().getWaybill();
				if(waybill != null){
					String waybillsign = waybill.getWaybillSign();
					if(waybillsign != null && waybillsign.length()>0){
						//waybillsign  1=T  ||  waybillsign  15=6表示逆向订单
						if((waybill.getWaybillSign().charAt(0)=='T' || waybill.getWaybillSign().charAt(14)=='6')){
							if(BusinessUtil.isSick(waybill.getWaybillSign())){
								//TODO 上线观察一段时间 可删除该log
								this.logger.error("分拣中心逆向病单屏蔽快退MQ,运单号：" + waybill.getWaybillCode());
								return;
							}
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
			BaseEntity<Waybill> wayBillOld = waybillQueryManager.getWaybillByReturnWaybillCode(sorting.getWaybillCode());
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

	/**
	 * 添加send_d明细数据
	 *
	 * @param sorting
	 * @return
	 */
	public SendDetail addSendDetail(Sorting sorting) {
		// added by huangliang
		CallerInfo info = Profiler.registerInfo("DMSWORKER.SortingService.addSendDetail", false, true);
		SendDetail sendDetail = SendDetail.toSendDatail(sorting);
		sendDetail.setOperateTime(new Date(sorting.getOperateTime().getTime() + Constants.DELIVERY_DELAY_TIME));
		this.fillSendDetailIfPickup(sendDetail);
		/* 补齐包裹重量 */
		// String retrieveFlag =
		// PropertiesHelper.newInstance().getValue("enablePackageRetrieve");
		// if( StringUtils.isNotBlank(retrieveFlag) &&
		// "sure".equals(retrieveFlag)){
		sendDetail = this.deliveryService.measureRetrieve(sendDetail);
		// }
		this.deliveryService.saveOrUpdate(sendDetail); // 更新或者插入发货明细表
		Profiler.registerInfoEnd(info);
		return sendDetail;
	}

	/**
	 * 分拣时已发货，补全中转发货数据
	 *
	 * @param sendM    发货sendM
	 * @param sendDetail 发货明细数据
	 */
	private SendDetail addTransitSendDetail(SendDetail sendDetail, SendM sendM) {
		SendDetail transitSendD = new SendDetail();
		BeanUtils.copyProperties(sendDetail, transitSendD);

		transitSendD.setCreateSiteCode(sendM.getCreateSiteCode());
		transitSendD.setReceiveSiteCode(sendM.getReceiveSiteCode());
		// 补全批次号SendCode
		transitSendD.setSendCode(sendM.getSendCode());

		// 更新或者插入发货明细表
		this.deliveryService.saveOrUpdate(transitSendD);

		transitSendD.setYn(1);

		// 补全全程跟踪数据，取sendM创建人，作为全程跟踪发货人，以及操作时间 sendM发货时间小于操作时间取实际操作时间    update by lhc 2017.12.14
		if (sendM.getOperateTime().getTime() < sendDetail.getOperateTime().getTime()) {
			transitSendD.setOperateTime(sendDetail.getOperateTime());
		} else {
			transitSendD.setOperateTime(sendM.getOperateTime());
		}

		transitSendD.setCreateUser(sendM.getCreateUser());
		transitSendD.setCreateUserCode(sendM.getCreateUserCode());
		transitSendD.setBoardCode(sendM.getBoardCode());
		transitSendD.setBizSource(sendM.getBizSource());
		return transitSendD;
	}

	/**
	 * B网建箱自动触发验货全程跟踪
	 * 推验货任务
	 * @param sorting
	 */
	public void b2bPushInspection(Sorting sorting) {
		BaseStaffSiteOrgDto createSite = null;
		try {
			createSite = this.baseMajorManager.getBaseSiteBySiteId(sorting.getCreateSiteCode());
		} catch (Exception e) {
			this.logger.error("sortingServiceImpl.pushInspection"+e.getMessage(), e);
		}
		//B网建箱自动触发验货全程跟踪
		if (createSite==null ||Constants.B2B_SITE_TYPE!=createSite.getSubType()){
			return;
		}
		Inspection inspectionQ=new Inspection();
		inspectionQ.setWaybillCode(sorting.getWaybillCode());
		inspectionQ.setPackageBarcode(sorting.getPackageCode());
		inspectionQ.setCreateSiteCode(sorting.getCreateSiteCode());
		boolean have=inspectionDao.haveInspectionByPackageCode(inspectionQ);
		//如果已经验过货  就不用补了
		if (have){
			return;
		}
		InspectionRequest inspection=new InspectionRequest();
		inspection.setUserCode(sorting.getCreateUserCode());
		inspection.setUserName(sorting.getCreateUser());
		inspection.setSiteCode(sorting.getCreateSiteCode());
		inspection.setSiteName(createSite.getSiteName());
		//验货操作提前5秒
		inspection.setOperateTime(DateHelper.formatDateTime(new Date(sorting.getOperateTime().getTime()-5000)));
		inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
		inspection.setPackageBarOrWaybillCode(sorting.getPackageCode());

		TaskRequest request=new TaskRequest();
		request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
		request.setKeyword1(String.valueOf(sorting.getCreateUserCode()));
		request.setKeyword2(sorting.getPackageCode());
		request.setType(Task.TASK_TYPE_INSPECTION);
		request.setOperateTime(inspection.getOperateTime());
		request.setSiteCode(sorting.getCreateSiteCode());
		request.setSiteName(createSite.getSiteName());
		request.setUserCode(sorting.getCreateUserCode());
		request.setUserName(sorting.getCreateUser());
		//request.setBody();
		String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
				+ JsonHelper.toJson(inspection)
				+ Constants.PUNCTUATION_CLOSE_BRACKET;
		Task task=this.taskService.toTask(request, eachJson);

		int result= this.taskService.add(task, true);
		if(logger.isDebugEnabled()){
			logger.debug("B网建箱自动触发验货全程跟踪-验货任务插入条数:"+result+"条,请求参数:"+JsonHelper.toJson(task));
		}
        addBusinessLog(sorting,task);
	}
	/**
	 * 记录业务日志
	 *
	 * @param sorting
	 */
	private void addBusinessLog(Sorting sorting,Task task) {
		//写入自定义日志
		BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
		businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
		businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_B_INSPECTION);
		businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_INSPECTION);
		businessLogProfiler.setOperateRequest(JsonHelper.toJson(sorting));
		businessLogProfiler.setOperateResponse(JsonHelper.toJson(task));
		businessLogProfiler.setTimeStamp(System.currentTimeMillis());
		BusinessLogWriter.writeLog(businessLogProfiler);
	}
	/**
	 * 1.补中转发货sendD发货数据并且发送全程跟踪
	 * 2.判断是否已发货，若已发货补SendD表数据并且发送全程跟踪
	 *
	 * @param sorting
	 * @param sendDs
	 */
	public void fixSendDAndSendTrack(Sorting sorting, List<SendDetail> sendDs){
		if (sendDs.size() > 0) {
			List<SendM> sendMs = new ArrayList<SendM>();
			List<SendM> transitSendMs = new ArrayList<SendM>();
			// 获取直接发货和中转发货的SendM数据
			this.setListByTransitOrDirect(sorting, sendMs, transitSendMs);
			// 判断是否存在跨分拣发货数据，则视为先发货后分拣，需要补中转的sendD发货明细数据和全程跟踪
			if (transitSendMs.size() > 0) {
				List<SendDetail> transitSendDs = new ArrayList<SendDetail>();
				for (SendM sendM : transitSendMs) {
					for (SendDetail sendDetail : sendDs) {
					    // 只有按箱操作才存在跨分拣的情况
						if (BusinessHelper.isBoxcode(sorting.getBoxCode())) {
							transitSendDs.add(this.addTransitSendDetail(sendDetail, sendM));
						}
					}
				}
				// 批量回传全程跟踪
				this.deliveryService.updateWaybillStatus(transitSendDs);
			}
            // 判断直发分拣类型是否已经发货，若存在sendM数据，则视为先发货后分拣，需要补直发的sendD发货明细数据和全程跟踪
			if (sendMs.size() > 0) {
				// 正常情况，分拣与发货一致的sendM仅有一条数据
				for (SendDetail sendDetail : sendDs) {
					// 补全发货数据
					this.fixSendDetail(sendDetail, sendMs.get(0));
				}
				// 批量回传全程跟踪
				this.deliveryService.updateWaybillStatus(sendDs);
			}
		}
	}

	/**
	 * 获取直接发货和中转发货的SendM数据
	 *
	 * @param sorting
	 * @param sendMs
	 * @param transitSendMs
	 */
	private void setListByTransitOrDirect(Sorting sorting, List<SendM> sendMs, List<SendM> transitSendMs) {
		List<SendM> sendList = this.deliveryService.getSendMListByBoxCode(sorting.getBoxCode());
		if (null != sendList && sendList.size() > 0) {
			Iterator<SendM> iterator = sendList.iterator();
			while (iterator.hasNext()) {
				SendM sendM = iterator.next();
				// 判断分拣始发站点与箱号的始发站点，目的站点是否都一致，不一致则为中转发货
				if (sendM.getCreateSiteCode().equals(sorting.getCreateSiteCode()) && sendM.getReceiveSiteCode().equals(sorting.getReceiveSiteCode())) {
					// 避免第一次发货封车一小时后再次发货，获取到第一次的批次号和操作信息
					if (sendM.getOperateTime().before(sorting.getOperateTime())) {
						logger.info("[分拣任务]过滤发货在前，分拣在后数据，运单号：" + sorting.getWaybillCode());
						continue;
					}
					// 直发分拣
					logger.info("[分拣任务]始发和目的站点一致补全，运单号：" + sorting.getWaybillCode());
					sendMs.add(sendM);
				} else {
					// 跨分拣发货
					transitSendMs.add(sendM);
					logger.info("[分拣任务]分拣中转全程跟踪补全发货，批次号为：" + sendM.getSendCode() + "，运单号为" + sorting.getPackageCode());
				}
			}
		}
	}

	/**
	 * 分拣时已发货，补发货数据
	 *
	 * @param sendDetail
	 * @param sendM
	 */
	private void fixSendDetail(SendDetail sendDetail, SendM sendM) {
		// 如果是正向、已经发货，则需要直接更新发货明细表（send_d)发货批次号(sendCode)
		/* updated by wangtingwei@jd.com  正向逆向三方发货全部补全数据，下线com.jd.bluedragon.distribution.worker.delivery.ToSendwaybillTask该WORKER*/
		sendDetail.setSendCode(sendM.getSendCode()); // 补全sendCode
		//this.deliveryService.saveOrUpdate(sendDetail); // 更新或者插入发货明细表

		sendDetail.setYn(1);
		/* 取sendM创建人，作为全程跟踪发货人，以及操作时间  sendM发货时间小于操作时间取实际操作时间    update by lhc 2017.12.14*/
		if (sendM.getOperateTime().getTime() < sendDetail.getOperateTime().getTime()) {
//			sendDetail.setOperateTime(sendDetail.getOperateTime());
		} else {
			sendDetail.setOperateTime(sendM.getOperateTime());
		}
		sendDetail.setCreateUser(sendM.getCreateUser());
		sendDetail.setCreateUserCode(sendM.getCreateUserCode());
        sendDetail.setBoardCode(sendM.getBoardCode());
		sendDetail.setBizSource(sendM.getBizSource());
	}

	private void fillSendDetailIfPickup(SendDetail sendDetail) {
		if (WaybillUtil.isSurfaceCode(sendDetail.getPackageBarcode())) {
			if(WaybillUtil.isPickupCodeWW(sendDetail.getPackageBarcode())) {
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

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.WEB.SortingServiceImpl.findOrderDetail", mState = JProEnum.TP)
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
				String wayBillCode = sorting.getWaybillCode();
				WChoice wChoice = new WChoice();
				wChoice.setQueryWaybillC(true);
				wChoice.setQueryWaybillE(false);
				wChoice.setQueryWaybillM(false);
				BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(wayBillCode, wChoice);
				if(baseEntity != null && baseEntity.getData() != null) {
					Waybill waybill = baseEntity.getData().getWaybill();
					if (waybill != null) {
						String waybillsign = waybill.getWaybillSign();
						if (waybillsign != null && waybillsign.length() > 0) {
							if(BusinessUtil.isSick(waybill.getWaybillSign())){
								//TODO 上线观察一段时间 可删除该log
								this.logger.error("分拣中心逆向病单屏蔽退款100分MQ,运单号：" + waybill.getWaybillCode());
								return;
							}
						}
						String refundMessage = this.refundMessage(sorting.getWaybillCode(),
								DateHelper.formatDateTimeMs(sorting.getOperateTime()));
						//bd_blocker_complete的MQ
						this.bdBlockerCompleteMQ.send( sorting.getWaybillCode(),refundMessage);
						this.logger.info("退款100分MQ消息推送成功,运单号：" + waybill.getWaybillCode());
						//【逆向分拣理货】增加orbrefundRqMQ  add by lhc  2016.8.17
						//这里需要暂时注释掉 逆向取件单不应该发送快退的mq,属于售后的范围  modified by zhanglei 20161025
						//fastRefundService.execRefund(sorting);
					}else{
						logger.info(wayBillCode + "对应的运单信息为空！");
					}
				}
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

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     * @param createSiteCode
     * @param packageCode
     * @return
     */
	@Override
	public List<Sorting>  findByPackageCode(Integer createSiteCode, String packageCode){
	    Sorting sorting = new Sorting();
	    sorting.setPackageCode(packageCode);
	    sorting.setCreateSiteCode(createSiteCode);
	    return sortingDao.findByPackageCode(sorting);
    }

	/**
	 * 根据箱号，当前站点查询有限的分拣记录
	 * @param boxCode
	 * @param createSiteCode
	 * @param fetchNum
	 * @return
	 */
	public List<Sorting>  findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum){
        if (boxCode == null || boxCode.isEmpty() || createSiteCode <= 0 || fetchNum <=0){
            return null;
        }
	    return sortingDao.findByBoxCodeAndFetchNum(boxCode,createSiteCode,fetchNum);
    }

    @Override
    public List<Sorting> findByWaybillCodeOrPackageCode(Integer createSiteCode,String waybillCode, String packageCode) {
	    if(StringUtils.isNotEmpty(waybillCode) || StringUtils.isNotEmpty(packageCode)){
	        Sorting sorting = new Sorting();
	        sorting.setCreateSiteCode(createSiteCode);
	        sorting.setPackageCode(packageCode);
	        sorting.setWaybillCode(waybillCode);
	        return sortingDao.findByWaybillCodeOrPackageCode(sorting);
        }
        return null;
    }

	@Override
	public List<Sorting> findPageSorting(Map<String, Object> params) {
		logger.info("SortingServiceImpl.findPageSorting begin...");
		return sortingDao.findPageSorting(params);
	}


	public final static String TASK_SORTING_FINGERPRINT_1200_5S = "TASK_1200_FP_5S_"; //5前缀

	@Autowired
	@Qualifier("jimdbCacheService")
	private CacheService cacheService;

	private static final String SPLIT_CHAR="$";

	@Autowired
	private SortingFactory sortingFactory;

	/**
	 * 处理任务数据
	 * @param task
	 * @return 成功与否
	 */
	public boolean processTaskData(Task task){
		CallerInfo process1200TaskData = ProfilerHelper.registerInfo("DMSWORKER.SortingService.processTaskData",
				Constants.UMP_APP_NAME_DMSWORKER);
		String fingerPrintKey = TASK_SORTING_FINGERPRINT_1200_5S + task.getCreateSiteCode() +"|"+ task.getBoxCode() +"|"+ task.getKeyword2();
		try{
			//判断是否重复分拣, 10秒内如果同操作场地、同目的地、同扫描号码即可判断为重复操作。立刻置失败，转到下一次执行。只使用key存不存在做防重
			Boolean isSucdess = cacheService.setNx(fingerPrintKey, "1", TASK_1200_EX_TIME_5_S, TimeUnit.SECONDS);
			if(!isSucdess){//说明有重复任务
				this.logger.warn("1200分拣任务重复："+task.getBody());
				return false;
			}
		}catch(Exception e){
			this.logger.error("获得1200分拣任务指纹失败"+task.getBody(), e);
		}

		boolean result = Boolean.FALSE;
		try {
			this.logger.info("task id is " + task.getId());
			result = this.doSorting(task);
		} catch (Exception e) {
			StringBuilder builder=new StringBuilder("task id is");
			builder.append(task.getId());
			builder.append(SPLIT_CHAR).append(task.getBoxCode());
			builder.append(SPLIT_CHAR).append(task.getKeyword1());
			builder.append(SPLIT_CHAR).append(task.getKeyword2());
			this.logger.error(builder.toString());
			this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
			result = Boolean.FALSE;
		}

		cacheService.del(fingerPrintKey);
		Profiler.registerInfoEnd(process1200TaskData);
		return result;
	}

	@Autowired
	private SysConfigService sysConfigService;

	/**
	 * 获取开启新分拣的分拣中心
	 * @param siteCode
	 * @return
	 */
	public boolean useNewSorting(Integer siteCode){
		SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_NEW_SORTING_OPEN_DMS_CODES);
		if (content != null) {
			if (content.getMasterSwitch() || content.getSiteCodes().contains(siteCode)) {
				return true;
			}
		}
		return false;
	}


    @Override
    public List<String> getWaybillCodeListByBoxCode(String boxCode) {
        Box box = this.boxService.findBoxByCode(boxCode);
        if (box == null) {
            return null;
        }
        Sorting queryParam = new Sorting();
        queryParam.setCreateSiteCode(box.getCreateSiteCode());
        queryParam.setBoxCode(boxCode);
        List<Sorting> sortingList = this.findByBoxCode(queryParam);
        if (sortingList.size() > 0) {
            Set<String> waybillCodeSet = new HashSet<>();
            for (Sorting sorting : sortingList) {
                waybillCodeSet.add(sorting.getWaybillCode());
            }
            return new ArrayList<>(waybillCodeSet);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

	/**
	 * 发送全称跟踪
	 *
	 */
	private void sendSortingCancelWaybillTrace(Sorting sorting) {
		CallerInfo info = Profiler.registerInfo("DMSWEB.SortingServiceImpl.sendSortingCancelWaybillTrace", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try {
			String boxCode = sorting.getBoxCode();
			String packageCode = sorting.getPackageCode();
			Integer createSiteCode = sorting.getCreateSiteCode();

			WaybillStatus waybillStatus = new WaybillStatus();
			//设置站点相关属性
			waybillStatus.setPackageCode(packageCode);
			waybillStatus.setWaybillCode(sorting.getWaybillCode());
			waybillStatus.setBoxCode(boxCode);
			waybillStatus.setCreateSiteCode(createSiteCode);
			waybillStatus.setOperatorId(sorting.getUpdateUserCode());
			waybillStatus.setOperator(sorting.getUpdateUser());
			waybillStatus.setOperateTime(sorting.getOperateTime() == null ? new Date() : sorting.getOperateTime());
			waybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_SORTING_CANCEL);
			waybillStatus.setRemark("取消建箱，箱号：" + boxCode);

			Task task = new Task();
			task.setTableName(Task.TABLE_NAME_POP);
			task.setSequenceName(Task.getSequenceName(task.getTableName()));
			task.setKeyword1(packageCode);
			task.setKeyword2(WaybillStatus.WAYBILL_TRACK_SORTING_CANCEL.toString());
			task.setCreateSiteCode(createSiteCode);
			task.setBody(JsonHelper.toJson(waybillStatus));
			task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
			task.setOwnSign(BusinessHelper.getOwnSign());

			// 添加到task表
			taskService.add(task);

		} catch (Exception e) {
			Profiler.functionError(info);
			logger.error("取消分拣发送全称跟踪失败.", e);
		} finally {
			Profiler.registerInfoEnd(info);
		}
	}

	public SortingResponse doCancelSorting(Sorting sorting){
		List<Sorting> sortingRecords = new ArrayList<Sorting>();

		SortingResponse response = getSortingRecords(sorting,sortingRecords);
		if(!response.getCode().equals(SortingResponse.CODE_OK)){
			return response;
		}

		Boolean canCancel = false;
		if (sortingRecords != null) {
			for (Sorting eachSorting : sortingRecords) {
				eachSorting.setOperateTime(sorting.getOperateTime());
				eachSorting.setUpdateUserCode(sorting.getUpdateUserCode());
				eachSorting.setUpdateUser(sorting.getUpdateUser());
				canCancel |= canCancel2(eachSorting);
			}
		}

		if(canCancel){
			return SortingResponse.ok();
		}
		return SortingResponse.sortingNotFund();
	}

	public SortingResponse getSortingRecords(Sorting sorting,List<Sorting> sortingRecords){
		if (StringUtils.isNotBlank(sorting.getBoxCode())) {
			// 校验是否发货，如果已经发货，则提示不能取消分拣
			SendM sendM = new SendM();
			sendM.setBoxCode(sorting.getBoxCode());
			sendM.setCreateSiteCode(sorting.getCreateSiteCode());
			List<SendM> sendMList = sendMDao.findSendMByBoxCode2(sendM);
			if (null != sendMList && !sendMList.isEmpty()) {
				return SortingResponse.sortingSended();
			}
			// 若三方分拣，校验是否验货，若已经验货，则提示不能取消
			if (sorting.getType() == Constants.BUSSINESS_TYPE_THIRD_PARTY) {
				Inspection inspection = new Inspection.Builder(null, sorting.getCreateSiteCode())
						.boxCode(sorting.getBoxCode()).inspectionType(sorting.getType()).build();
				int inspectionCount = inspectionService.inspectionCount(inspection);
				if (inspectionCount > 0) {
					return SortingResponse.sortingInspected();
				}
			}
			sortingRecords.addAll(sortingDao.findByBoxCode(sorting));
			if (sortingRecords != null && sortingRecords.size() > DmsConstants.MAX_NUMBER) {
				logger.warn(sorting.getPackageCode() + "的包裹数：" + sortingRecords.size() + "，大于两万，已反馈现场提报IT");
				return SortingResponse.packageNumLimit();
			}
		} else {
			sortingRecords.addAll(queryByCode2(sorting));
			if (sortingRecords == null || sortingRecords.isEmpty()) {
				logger.warn("取消分拣--->包裹已经发货");
				addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL, "包裹已经发货");
				return SortingResponse.sortingSended();
			} else if (sortingRecords.size() > DmsConstants.MAX_NUMBER) {
				logger.warn(sorting.getPackageCode() + "的包裹数：" + sortingRecords.size() + "，大于两万，已反馈现场提报IT");
				return SortingResponse.packageNumLimit();
			}

			if (Constants.BUSSINESS_TYPE_THIRD_PARTY == sorting.getType()) {
				int unfilledOrdersCount = 0;
				for (Sorting eachSorting : sortingRecords) {
					// 如果已经验货，则exception_status为0，则不能取消分拣，需要在异常处理里进行少验取消的操作
					InspectionEC inspectionEC = new InspectionEC.Builder(eachSorting.getPackageCode(),
							eachSorting.getCreateSiteCode()).waybillCode(eachSorting.getWaybillCode())
							.boxCode(eachSorting.getBoxCode()).receiveSiteCode(eachSorting.getReceiveSiteCode())
							.inspectionType(eachSorting.getType()).inspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE)
							.yn(1).build();
					Integer inspectionCount = inspectionExcpetionService.inspectionCount(inspectionEC);

					if (inspectionCount > 0) {
						unfilledOrdersCount++;
						addOpetationLog(sorting, OperationLog.LOG_TYPE_SORTING_CANCEL, "已经三方验货或者差异处理");
					}
				}
				if (unfilledOrdersCount == sortingRecords.size()) {
					return SortingResponse.sortingInspected();
				}
			}
		}
		return SortingResponse.ok();
	}

	/**
	 * 分拣核心操作成功后的补充操作
	 *
	 * @param task
	 * @return
	 */
	public boolean executeSortingSuccess(Task task) {
		try {
			SortingObjectExtend sorting = JSONObject.parseObject(task.getBody(), SortingObjectExtend.class);

			if (sorting.getPackagePageIndex() == 0 || com.jd.common.util.StringUtils.isNotBlank(sorting.getDmsSorting().getPackageCode())) {
				doSortingAfter(sorting.getDmsSorting());

			} else {
				//分批后的任务需要调用运单接口获取包裹数据
				BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(sorting.getDmsSorting().getWaybillCode(), sorting.getPackagePageIndex(), sorting.getPackagePageSize());

				List<DeliveryPackageD> packageDList = baseEntity.getData();

				for (DeliveryPackageD packageD : packageDList) {
					sorting.getDmsSorting().setPackageCode(packageD.getPackageBarcode());
					doSortingAfter(sorting.getDmsSorting());
				}
			}

			return true;
		} catch (Exception e) {
			logger.error("执行executeSortingSuccess任务异常.参数：" + JSON.toJSONString(task), e);
			return false;
		}
	}

	private void doSortingAfter(Sorting sorting) {
		fillSortingIfPickup(sorting);
		sortingAddInspection(sorting);
		sortingAddSend(sorting);
		addOpetationLog(sorting,OperationLog.LOG_TYPE_SORTING);
	}

	/**
	 * 分拣补验货
	 * 1.B网发验货任务
	 * 2.补验货差异表inspection_ec
	 * @param sorting
	 */
	private void sortingAddInspection(Sorting sorting){
		b2bPushInspection(sorting);
		saveOrUpdateInspectionEC(sorting);
	}

	/**
	 * 分拣补发货
	 * 1.补send_d表
	 * 2.补发货全称跟踪
	 * @param sorting
	 */
	private void sortingAddSend(Sorting sorting) {
		List<SendDetail> sendDList = new ArrayList<>();

		sendDList.add(addSendDetail(sorting));
		//补发货
		fixSendDAndSendTrack(sorting, sendDList);
	}
}
