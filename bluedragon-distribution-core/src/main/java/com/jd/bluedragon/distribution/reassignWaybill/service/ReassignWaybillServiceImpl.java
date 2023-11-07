package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyApproveStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExScrapApproveStageEnum;
import com.jd.bluedragon.common.dto.sysConfig.response.ProvinceAreaApprovalConfigDto;
import com.jd.bluedragon.common.dto.sysConfig.response.ReassignWaybillProvinceAreaApprovalConfigDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ExpressDispatchServiceManager;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.security.log.SecurityLogWriter;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.enums.ReassignWaybillApproveStageEnum;
import com.jd.bluedragon.distribution.api.enums.ScheduleAfterTypeEnum;
import com.jd.bluedragon.distribution.api.enums.ReassignWaybillReasonTypeEnum;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;

import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.log.BusinessLogProfilerBuilder;
import com.jd.bluedragon.distribution.print.domain.DmsPaperSize;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.distribution.reassignWaybill.dao.ReassignWaybillApprovalRecordDao;
import com.jd.bluedragon.distribution.reassignWaybill.dao.ReassignWaybillDao;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecord;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecordMQ;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.alibaba.fastjson.JSONObject;
import com.jd.jim.cli.Cluster;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.ApproveRequestOrder;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.lsb.flow.domain.jme.JmeFile;
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.dms.utils.BusinessUtil.getHideAddress;

@Service("reassignWaybill")
public class ReassignWaybillServiceImpl implements ReassignWaybillService {
	private static final Logger log = LoggerFactory.getLogger(ReassignWaybillServiceImpl.class);

	private static final String ADDRESS = "address";
	private static final String REASSIGNADDRESS = "reassignAddress";

	@Autowired
	private LogEngine logEngine;



	@Autowired
	private ReassignWaybillDao reassignWaybillDao;
	@Autowired
	private CenConfirmService cenConfirmService;
	@Autowired
	private DefaultJMQProducer reassignWaybillMQ;
	@Autowired
	@Qualifier("waybillSiteChangeProducer")
	private DefaultJMQProducer waybillSiteChangeProducer;

	@Autowired
	private TaskService taskService;

	@Autowired
	private WaybillService waybillService;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private ExpressDispatchServiceManager expressDispatchServiceManager;

	@Autowired
	private JdCommandService commandService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private ReassignWaybillApprovalRecordDao reassignWaybillApprovalRecordDao;

	@Autowired
	@Qualifier("reassignWaybillApprovalProducer")
	private DefaultJMQProducer reassignWaybillApprovalProducer;

	@Autowired
	private FlowServiceManager flowServiceManager;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	@Qualifier("redisClientOfJy")
	private Cluster redisClientOfJy;


	private Boolean add(ReassignWaybill packTagPrint) {
		long startTime=new Date().getTime();

		Assert.notNull(packTagPrint, "packTagPrint must not be null");
		CenConfirm cenConfirm=new CenConfirm();
		cenConfirm.setWaybillCode(packTagPrint.getWaybillCode());
		cenConfirm.setCreateSiteCode(packTagPrint.getSiteCode());
		cenConfirm.setPackageBarcode(packTagPrint.getPackageBarcode());
		cenConfirm.setOperateType(Constants.OPERATE_TYPE_RCD);
		cenConfirm.setType(Integer.valueOf(Constants.BUSSINESS_TYPE_RCD).shortValue());
		cenConfirm.setOperateTime(packTagPrint.getOperateTime());
		cenConfirm.setOperateUser(packTagPrint.getUserName());
		cenConfirm.setOperateUserCode(packTagPrint.getUserCode());
		cenConfirm.setReceiveSiteCode(packTagPrint.getChangeSiteCode());
		cenConfirmService.syncWaybillStatusTask(cenConfirm);
		//添加返调度运单信息到本地库
		sendReassignWaybillMq(packTagPrint);

		if(WaybillUtil.getCurrentPackageNum(packTagPrint.getPackageBarcode()) == 1){
			//每个运单只需要发一次就可以
			SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
			siteChangeMqDto.setWaybillCode(packTagPrint.getWaybillCode());
			siteChangeMqDto.setNewSiteId(packTagPrint.getChangeSiteCode());
			siteChangeMqDto.setNewSiteName(packTagPrint.getChangeSiteName());
			siteChangeMqDto.setNewSiteRoadCode("0"); // 此操作无法触发预分拣 故传默认值0
			siteChangeMqDto.setOperatorId(packTagPrint.getUserCode());
			siteChangeMqDto.setOperatorName(packTagPrint.getUserName());
			siteChangeMqDto.setOperatorSiteId(packTagPrint.getSiteCode());
			siteChangeMqDto.setOperatorSiteName(packTagPrint.getSiteName());
			siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
			try {
				waybillSiteChangeProducer.sendOnFailPersistent(packTagPrint.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
				if(log.isDebugEnabled()){
					log.debug("发送预分拣站点变更mq消息成功(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto));
				}
			} catch (Exception e) {
				log.error("发送预分拣站点变更mq消息失败(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto), e);
			}finally{

				long endTime = new Date().getTime();

				JSONObject response=new JSONObject();
				response.put("keyword2", String.valueOf(siteChangeMqDto.getOperatorId()));
				response.put("keyword3", waybillSiteChangeProducer.getTopic());
				response.put("keyword4", siteChangeMqDto.getOperatorSiteId()==null?0:siteChangeMqDto.getOperatorSiteId().longValue());
				response.put("content", JsonHelper.toJsonUseGson(siteChangeMqDto));

				JSONObject request=new JSONObject();
				request.put("waybillCode",siteChangeMqDto.getWaybillCode());
				request.put("packageCode",siteChangeMqDto.getPackageCode());
				request.put("operatorName",siteChangeMqDto.getOperatorName());

				BusinessLogProfiler businessLogProfiler = new BusinessLogProfilerBuilder()
						.operateTypeEnum(BusinessLogConstans.OperateTypeEnum.SORTING_PRE_SITE_CHANGE)
						.methodName("ReassignWaybillServiceImpl#add")
						.operateRequest(request)
						.operateResponse(response)
						.build();
				businessLogProfiler.setProcessTime(endTime-startTime);

				logEngine.addLog(businessLogProfiler);

				SystemLogUtil.log(siteChangeMqDto.getWaybillCode(), String.valueOf(siteChangeMqDto.getOperatorId()), waybillSiteChangeProducer.getTopic(),
						siteChangeMqDto.getOperatorSiteId()==null?0:siteChangeMqDto.getOperatorSiteId().longValue(), JsonHelper.toJsonUseGson(siteChangeMqDto), SystemLogContants.TYPE_SITE_CHANGE_MQ_OF_OTHER);
			}

		}

		return reassignWaybillDao.add(packTagPrint);
	}

	/**
	 * 添加返调度运单信息到运单本地库
	 * 这里需要添加try catch 为了防止报错之后 后边的不能用
	 *
	 * 只有在分拣中心返调度的才会正常落数据 站点返调度不处理
	 * */
	private boolean sendReassignWaybillMq(ReassignWaybill packTagPrint){
		try {
			String json = JsonHelper.toJsonUseGson(packTagPrint);
			reassignWaybillMQ.sendOnFailPersistent(packTagPrint.getWaybillCode(), json);
		}catch (Exception e){
			return false;
		}
		return true;
	}

    @Override
    @JProfiler(jKey= "DMSWEB.ReassignWaybillService.packLastScheduleSite")
    public ReassignWaybill queryByPackageCode(String packageCode) {
        Assert.notNull(packageCode, "packageCode must not be null");
        return reassignWaybillDao.queryByPackageCode(packageCode);
    }

	@Override
	public ReassignWaybill queryByWaybillCode(String waybillCode) {
		 Assert.notNull(waybillCode, "waybillCode must not be null");
	        return reassignWaybillDao.queryByWaybillCode(waybillCode);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public JdResult<Boolean> backScheduleAfter(ReassignWaybillRequest reassignWaybillRequest) {
    	JdResult<Boolean> jdResult = new JdResult<Boolean>();
		jdResult.setData(Boolean.FALSE);
		jdResult.toFail();
		if (reassignWaybillRequest == null 
				|| (StringUtils.isBlank(reassignWaybillRequest.getPackageBarcode())
						&& CollectionUtils.isEmpty(reassignWaybillRequest.getPackageCodeList()))) {
			log.warn("backScheduleAfter --> 传入参数非法");
			jdResult.toFail(JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
			return jdResult;
		}
		if(ScheduleAfterTypeEnum.PACKAGE_CODE_LIST.getCode().equals(reassignWaybillRequest.getScheduleAfterType())) {
			List<ReassignWaybill> packList = toReassignWaybillList(reassignWaybillRequest);
			if(!CollectionUtils.isEmpty(packList)) {
				for(ReassignWaybill item: packList) {
					add(item);
				}
				jdResult.toSuccess();
				jdResult.setData(Boolean.TRUE);
			}
		}else {
			log.info("backScheduleAfter--> packageBarcode is [{}]",reassignWaybillRequest.getPackageBarcode());
			ReassignWaybill packTagPrint = ReassignWaybill.toReassignWaybill(reassignWaybillRequest);
			if (add(packTagPrint)) {
				jdResult.toSuccess();
				jdResult.setData(Boolean.TRUE);
			} else {
				jdResult.toFail(308, "处理失败");
			}
		}
		return jdResult;
	}

	@Override
	public Boolean addToDebon(ReassignWaybill packTagPrint) {
		if(WaybillUtil.getCurrentPackageNum(packTagPrint.getPackageBarcode()) == 1){
			//每个运单只需要发一次就可以
			SiteChangeMqDto siteChangeMqDto = new SiteChangeMqDto();
			siteChangeMqDto.setWaybillCode(packTagPrint.getWaybillCode());
			siteChangeMqDto.setNewSiteId(packTagPrint.getChangeSiteCode());
			siteChangeMqDto.setNewSiteName(packTagPrint.getChangeSiteName());
			siteChangeMqDto.setNewSiteRoadCode("0"); // 此操作无法触发预分拣 故传默认值0
			siteChangeMqDto.setOperatorId(packTagPrint.getUserCode());
			siteChangeMqDto.setOperatorName(packTagPrint.getUserName());
			siteChangeMqDto.setOperatorSiteId(packTagPrint.getSiteCode());
			siteChangeMqDto.setOperatorSiteName(packTagPrint.getSiteName());
			siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
			try {
				log.info("addToDebon-siteChangeMqDto-信息-{}",JsonHelper.toJson(siteChangeMqDto));
				waybillSiteChangeProducer.sendOnFailPersistent(packTagPrint.getWaybillCode(), JsonHelper.toJsonUseGson(siteChangeMqDto));
				if(log.isDebugEnabled()){
					log.debug("发送预分拣站点变更mq消息成功(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto));
				}
			} catch (Exception e) {
				log.error("发送预分拣站点变更mq消息失败(现场预分拣)：{}",JsonHelper.toJsonUseGson(siteChangeMqDto), e);
			}
		}
		return reassignWaybillDao.add(packTagPrint);
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.ReassignWaybillServiceImpl.getReassignOrderInfo", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<ReassignOrder> getReassignOrderInfo(String packageCode, List<String> decryptFields) {
		log.info("getReassignOrderInfo 入参-{}",JSON.toJSONString(packageCode));
		JdResult<ReassignOrder> result = new JdResult<>();
		result.toSuccess();
		ReassignOrder  reassignOrder = new ReassignOrder();
		OrderResponse orderResponse = this.waybillService.getDmsWaybillInfoAndCheck(packageCode);
		result.setCode(orderResponse.getCode());
		result.setMessage(orderResponse.getMessage());
		if(!OrderResponse.CODE_OK.equals(orderResponse.getCode())){
			return result;
		}
		BeanCopyUtil.copy(orderResponse,reassignOrder);
		getHideInfo(orderResponse,decryptFields);
		SecurityLogWriter.orderResourceGetOrderResponseWrite(packageCode, orderResponse);
		return result;
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.ReassignWaybillServiceImpl.stationMatchByAddress", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request) {
		log.info("stationMatchByAddress 入参-{}",JSON.toJSONString(request));
		return expressDispatchServiceManager.stationMatchByAddress(request);
	}

	@Override
	public JdResult<List<BaseStaffResponse>> getSiteByCodeOrName(String siteCodeOrName) {
		JdResult<List<BaseStaffResponse>> result = new JdResult<>();
		if (StringHelper.isEmpty(siteCodeOrName)) {
			return result;
		}
		List<BaseStaffResponse> responseList = new ArrayList<>();
		List<BaseStaffSiteOrgDto> resList = new ArrayList<>();
		if (NumberHelper.isNumber(siteCodeOrName)) {
			/* 站点code精确匹配 */
			resList.add(baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCodeOrName));
		} else {
			/* 站点名称模糊匹配 */
			resList.addAll(siteService.fuzzyGetSiteBySiteName(siteCodeOrName));
		}
		if(!CollectionUtils.isEmpty(resList)){
			for (BaseStaffSiteOrgDto dto : resList){
				BaseStaffResponse baseStaff = new BaseStaffResponse();
				baseStaff.setSiteCode(dto.getSiteCode());
				baseStaff.setSiteName(dto.getSiteName());
				responseList.add(baseStaff);
			}
		}
		result.setData(responseList);
		return result;
	}

	private void getHideInfo(OrderResponse orderResponse, List<String> decryptFields) {
		try{
			if (CollectionUtils.isEmpty(decryptFields)){
				orderResponse.setAddress(getHideAddress(orderResponse.getAddress()));
				orderResponse.setReassignAddress(getHideAddress(orderResponse.getReassignAddress()));
				return;
			}
			if (!decryptFields.contains(ADDRESS)){
				orderResponse.setAddress(getHideAddress(orderResponse.getAddress()));
			}
			if (!decryptFields.contains((REASSIGNADDRESS))){
				orderResponse.setReassignAddress(getHideAddress(orderResponse.getReassignAddress()));
			}
		}catch (Exception e){
			log.error("包裹{}敏感信息隐藏失败",orderResponse.getWaybillCode(),e);
		}

	}




	@Override
	@JProfiler(jKey = "DMS.BASE.ReassignWaybillServiceImpl.executeReassignWaybill", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<Boolean> executeReassignWaybill(ReassignWaybillReq req) {
		log.info("executeReassignWaybill 入参-{}",JSON.toJSONString(req));
		JdResult<Boolean> result = new JdResult<>();
		result.toSuccess("请求成功");
		checkParam(result,req);
		if(!JdResult.CODE_SUC.equals(result.getCode())){
			return result;
		}
		//现场预分拣拦截校验
		WaybillForPreSortOnSiteRequest preSortOnSiteRequest = buildWaybillForPreSortOnSiteRequest(req);
		InvokeResult<String> invokeResult = waybillService.checkWaybillForPreSortOnSite(preSortOnSiteRequest);
		if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
			result.setCode(invokeResult.getCode());
			result.setMessage(invokeResult.getMessage());
			return result;
		}
		//判断返调度原因类型
		ReassignWaybillReasonTypeEnum reasonTypeEnum = ReassignWaybillReasonTypeEnum.getEnum(req.getReasonType());
		switch (reasonTypeEnum){
			case UNABLE_TO_DELIVER :
			case CONTROL_CONTRABAND :
				//逻辑删除未审核通过的申请记录 / 取消OA申请单
				dealOldReassignWaybillApprovalRecord(req);
				//新增新的审核申请
				dealNewReassignWaybillApprovalRecord(req);
				String cacheKey = Constants.REASSIGN_WAYBILLCACHE_KEY_PREFIX + req.getBarCode();
				Boolean cache = redisClientOfJy.set(cacheKey, JSON.toJSONString(req), 2, TimeUnit.DAYS, true);
				if(log.isInfoEnabled()){
					log.info("审批前放入缓存结果-{}",cache);
				}
				break;
			case POSTAL_REJECTION :
				//邮政拒收
				BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(req.getOldSiteCode());
				if(baseSite == null){
					result.toError("获取原预分拣站点失败！");
					return result;
				}
				if(!(Constants.THIRD_SITE_TYPE.equals(baseSite.getSiteType())
						&& Constants.THIRD_SITE_SUB_TYPE.equals(baseSite.getSubType())
						&& Constants.THIRD_SITE_THIRD_TYPE_SMS.equals(baseSite.getThirdType()))){
					result.toError("此单非邮政单，禁止选择邮政拒收原因，请继续下传！");
					return result;
				}
				return returnPack(req);
			case NO_PRE_SORTING_STATION :
				if(req.getOldSiteCode() != null){
					result.toError("该单有预分拣站点，请继续下传！");
					return result;
				}
				return returnPack(req);

			default:
				log.warn("未知的返调度类型！");
		}
		return result;
	}

	@Override
	public void dealReassignWaybillApprove(ReassignWaybillApprovalRecordMQ mq) {

		ReassignWaybillApprovalRecord record = reassignWaybillApprovalRecordDao.selectByBarCode(mq.getBarCode());
		if(record == null){
			log.warn("未查到当前运单的申请记录-{}",record.getBarCode());
			return ;
		}
		// 提交审批
		Map<String, Object> oaMap = buildOA(mq); // OA数据
		Map<String, Object> businessMap = buildBusiness(mq); // 业务数据
		Map<String, Object> flowMap = buildFlow(mq); // 流程数据

		String approveOrderCode = flowServiceManager.startFlow(
				oaMap, businessMap, flowMap, FlowConstants.FLOW_CODE_REASSIGN_WAYBILL, mq.getApplicationUserErp(), mq.getBarCode());
		if (log.isInfoEnabled()) {
			log.info("提交审批完成，审批工单号:{}", approveOrderCode);
		}
		if(StringUtils.isNotBlank(approveOrderCode)){
			ReassignWaybillApprovalRecord updaDateRecord = new ReassignWaybillApprovalRecord();
			updaDateRecord.setBarCode(mq.getBarCode());
			updaDateRecord.setUpdateTime(new Date());
			updaDateRecord.setFirstChecker(mq.getFirstChecker());
			updaDateRecord.setFirstCheckStatus(JyApproveStatusEnum.TODO.getCode());
			updaDateRecord.setSecondChecker(mq.getSecondChecker());
			updaDateRecord.setSecondCheckStatus(JyApproveStatusEnum.TODO.getCode());
			if (log.isInfoEnabled()) {
				log.info("提交审批,更新返调度审批数据:{}", JSON.toJSONString(updaDateRecord));
			}
			reassignWaybillApprovalRecordDao.update(updaDateRecord);
		}
	}



	private Map<String, Object> buildFlow(ReassignWaybillApprovalRecordMQ mq) {
		Map<String, Object> flowControlMap = Maps.newHashMap();


		BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(mq.getSiteCode());

		String provinceAgencyCode = baseSite.getProvinceAgencyCode();
		String areaCode = baseSite.getAreaCode();

		StringJoiner joiner = new StringJoiner("_");
		joiner.add(provinceAgencyCode);
		if(StringUtils.isNotBlank(areaCode)){
			joiner.add(areaCode);
		}
		String provinceAreaCode = joiner.toString();
		SysConfig configContent = sysConfigService.findConfigContentByConfigName(Constants.REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG);
		if(configContent == null){
			return flowControlMap;
		}
		String firstChecker ="";
		String secondChecker ="";
		ReassignWaybillProvinceAreaApprovalConfigDto configDto = JSON.parseObject(configContent.getConfigContent(), ReassignWaybillProvinceAreaApprovalConfigDto.class);
		List<ProvinceAreaApprovalConfigDto> configList = configDto.getConfigList();
		if(!CollectionUtils.isEmpty(configList)){
			for (ProvinceAreaApprovalConfigDto config:configList) {
				if(Objects.equals(provinceAreaCode,config.getProvinceAreaCode())){
					firstChecker = config.getFirstChecker();
					secondChecker =config.getSecondChecker();
					break;
				}
			}
		}
		flowControlMap.put(FlowConstants.FLOW_DATA_MAP_FIRST_REASSIGN_WAYBILL_APPROVE_TRIGGER_ERP,firstChecker);
		flowControlMap.put(FlowConstants.FLOW_DATA_MAP_REASSIGN_WAYBILL_APPROVE_COUNT, JyExScrapApproveStageEnum.FIRST.getCount());
		flowControlMap.put(FlowConstants.FLOW_DATA_MAP_SECOND_REASSIGN_WAYBILL_APPROVE_TRIGGER_ERP, secondChecker);
		flowControlMap.put(FlowConstants.FLOW_DATA_MAP_REASSIGN_WAYBILL_APPROVE_COUNT, JyExScrapApproveStageEnum.SECOND.getCount());
		mq.setFirstChecker(firstChecker);
		mq.setSecondChecker(secondChecker);
		return flowControlMap;
	}

	/**
	 * 业务数据
	 *
	 * @param
	 * @return
	 */
	private Map<String, Object> buildBusiness(ReassignWaybillApprovalRecordMQ mq) {
		Map<String, Object> businessMap = Maps.newHashMap();
		// 设置业务唯一编码
		businessMap.put(FlowConstants.FLOW_BUSINESS_NO_KEY, mq.getBarCode());
		return businessMap;
	}

    /**
     * 构建OA流程参数
     *
     * @return oaMap 返回OA流程参数，类型为Map<String, Object>
     */

	private Map<String, Object> buildOA(ReassignWaybillApprovalRecordMQ mq) {
		Map<String, Object> oaMap = Maps.newHashMap();
		oaMap.put(FlowConstants.FLOW_OA_JMEREQNAME, FlowConstants.FLOW_FLOW_WORK_THEME_REASSIGN_WAYBILL);
		oaMap.put(FlowConstants.FLOW_OA_JMEREQCOMMENTS, FlowConstants.FLOW_FLOW_WORK_REMARK_REASSIGN_WAYBILL);
		List<String> mainColList = new ArrayList<>();
		mainColList.add("返调度申请单号:" + mq.getBarCode());
		mainColList.add("返调度操作场地:" + mq.getSiteName());
		mainColList.add("返调度操作申请人:" + mq.getApplicationUserErp());
		oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST, mainColList);
		return oaMap;
	}

	/**
	 * 处理历史申请记录
	 * @param req
	 */
	private void dealOldReassignWaybillApprovalRecord(ReassignWaybillReq req){
		Map<String, Object> params = new HashMap<>();
		params.put("barCode", req.getBarCode());
		params.put("updateUserErp", req.getOperateUserErp());
		params.put("updateTime", new Date());
		if(log.isInfoEnabled()){
			log.info("dealOldReassignWaybillApprovalRecord 入参-{}",JSON.toJSONString(params));
		}
		reassignWaybillApprovalRecordDao.updateReassignWaybilApprovalNotPassByBarCode(params);
		//取消OA申请
		flowServiceManager.cancelRequestOrder(req.getBarCode(),req.getOperateUserErp());
	}

	/**
     * 初始化重新分配运单审批记录
     * @param req 重新分配运单请求
     * @return record 重新分配运单审批记录
     */
	private void dealNewReassignWaybillApprovalRecord(ReassignWaybillReq req){
		ReassignWaybillApprovalRecord record = new ReassignWaybillApprovalRecord();
		record.setSiteCode(req.getOperateSiteCode());
		record.setSiteName(req.getOperateSiteName());
		record.setApplicationUserErp(req.getOperateUserErp());
		record.setBarCode(req.getBarCode());
		record.setSubmitTime(new Date());
		record.setChangeSiteReasonType(req.getReasonType());
		record.setFirstChecker("");
		record.setFirstCheckStatus(JyApproveStatusEnum.TODO.getCode());
		record.setReceiveSiteCode(req.getOldSiteCode());
		record.setReceiveSiteName(req.getOldSiteName());
		record.setChangeSiteCode(req.getSiteOfSchedulingOnSiteCode());
		record.setChangeSiteName(req.getSiteOfSchedulingOnSiteName());
		record.setCreateUserErp(req.getOperateUserErp());
		record.setCreateTime(new Date());
		if(log.isInfoEnabled()){
			log.info("initReassignWaybillApprovalRecord- {}",JSON.toJSONString(record));
		}
		Boolean add = reassignWaybillApprovalRecordDao.add(record);
		if(!add){
			throw new JyBizException("初始化添加运单返调度记录数据失败！");
		}
		ReassignWaybillApprovalRecordMQ recordMQ = new ReassignWaybillApprovalRecordMQ();
		BeanUtils.copyProperties(record,recordMQ);
		if(log.isInfoEnabled()){
			log.info("返调度审批异步消息发送!-{}",JSON.toJSONString(recordMQ) );
		}
		reassignWaybillApprovalProducer.sendOnFailPersistent(req.getBarCode(), JsonHelper.toJson(req));
		return ;
	}

	/**
	 * 返调度
	 * @param req
	 */
	private JdResult<Boolean>  returnPack(ReassignWaybillReq req){
		//获取打印信息
		String jsonCommand = buildJsonCommand(req);
		String printInfo = commandService.execute(jsonCommand);
		ReassignWaybillRequest request = bulidReassignWaybillRequest(printInfo, req);
		return backScheduleAfter(request);
	}


    /**
     * 构建重新指派运单请求对象
     *
     * @param printInfo 打印信息
     * @param req 重新指派运单请求
     * @return 重新指派运单请求对象
     */
	private ReassignWaybillRequest bulidReassignWaybillRequest(String printInfo,ReassignWaybillReq req){
		ReassignWaybillRequest request = new ReassignWaybillRequest();

		JSONObject jsonObject = JSON.parseObject(printInfo);
		String printAddress = jsonObject.get("printAddress").toString();
		String printTime = jsonObject.get("printTime").toString();
		JSONArray packList = jsonObject.getJSONArray("packList");

		List<String> packageCodeList = new ArrayList<>();
		for (int i = 0; i < packList.size(); i++) {
			JSONObject packListJSONObject = packList.getJSONObject(i);
			String packageCode = packListJSONObject.get("packageCode").toString();
			packageCodeList.add(packageCode);
		}

		request.setAddress(printAddress);
		request.setOperateTime(printTime);
		request.setPackageCodeList(packageCodeList);
		request.setSiteCode(req.getOperateSiteCode());
		request.setSiteName(req.getOperateSiteName());
		request.setUserCode(req.getOperateUserCode());
		request.setUserName(req.getOperateUserName());
		request.setChangeSiteCode(req.getSiteOfSchedulingOnSiteCode());
		request.setChangeSiteName(req.getSiteOfSchedulingOnSiteName());
		request.setReceiveSiteCode(req.getOldSiteCode());
		request.setReceiveSiteName(req.getOldSiteName());
		request.setInterfaceType(ScheduleAfterTypeEnum.PACKAGE_CODE_LIST.getCode());
		request.setScheduleAfterType(WaybillPrintOperateTypeEnum.RESCHEDULE_PRINT.getType());
		log.info("ReassignWaybillRequest -{}",JSON.toJSONString(request));
		return request;
	}

	/**
	 * 构建获取打印数据请求
	 * @param req
	 * @return
	 */
	private String buildJsonCommand(ReassignWaybillReq req){
		JdCommand<String> jsonCommand = new JdCommand<>();
		jsonCommand.setSystemCode(Constants.SYS_CODE_DMS);
		jsonCommand.setProgramType(Constants.PROGRAM_TYPE);
		jsonCommand.setBusinessType(Constants.BUSINESS_TYPE);
		jsonCommand.setOperateType(Constants.OPERATE_TYPE);

		Map<String,Object> paramMap = new HashMap<>();
		paramMap.put("barCode",req.getBarCode());
		paramMap.put("businessType",Constants.BUSINESS_TYPE);
		paramMap.put("operateType",Constants.OPERATE_TYPE);
		paramMap.put("paperSizeCode", DmsPaperSize.PAPER_SIZE_CODE_1005);
		paramMap.put("dmsSiteCode",req.getOperateSiteCode());
		paramMap.put("userCode",req.getOperateUserCode());
		paramMap.put("userName",req.getOperateUserName());
		paramMap.put("siteCode",req.getOperateSiteCode());
		paramMap.put("siteName",req.getOperateSiteName());
		paramMap.put("targetSiteCode",req.getSiteOfSchedulingOnSiteCode());

		String paramStr = JSON.toJSONString(paramMap);
		jsonCommand.setData(paramStr);
		if(log.isInfoEnabled()){
			log.info("buildJsonCommand -{}",JSON.toJSONString(jsonCommand));
		}
		return JSON.toJSONString(jsonCommand);
	}



    /**
     * 构建预分拣现场运单请求
     * @param req 重新分配运单请求对象
     * @return 预分拣现场运单请求对象
     */
	private WaybillForPreSortOnSiteRequest buildWaybillForPreSortOnSiteRequest(ReassignWaybillReq req){
		WaybillForPreSortOnSiteRequest request = new WaybillForPreSortOnSiteRequest();
		request.setWaybill(req.getBarCode());
		request.setSiteOfSchedulingOnSite(req.getSiteOfSchedulingOnSiteCode());
		request.setErp(req.getOperateUserErp());
		request.setSortingSite(req.getOperateSiteCode());
		if(log.isInfoEnabled()){
			log.info("buildWaybillForPreSortOnSiteRequest -{}",JSON.toJSONString(request));

		}
		return request;
	}

    /**
     * 检查参数是否合法
     * @param result JdResult<Boolean> 结果对象
     * @param req ReassignWaybillReq 重新分配运单请求对象
     */
	private void checkParam(JdResult<Boolean> result,ReassignWaybillReq req){
		if(req == null){
			result.toFail("入参不能为空");
			return ;
		}
		if(req.getOperateSiteCode() == null){
			result.toFail("操作场地编码不能为空");
			return ;
		}
		if(StringUtils.isBlank(req.getOperateSiteName())){
			result.toFail("操作场地编码不能为空");
			return ;
		}
		if(StringUtils.isBlank(req.getOperateUserErp())){
			result.toFail("操作人erp不能为空");
			return ;
		}
		if(StringUtils.isBlank(req.getOperateUserName())){
			result.toFail("操作人姓名不能为空");
			return ;
		}
		if(StringUtils.isBlank(req.getBarCode())){
			result.toFail("操作单号不能为空");
			return ;
		}

		if(req.getReasonType() == null){
			result.toFail("返调度原因不能为空");
			return ;
		}
		if(!ReassignWaybillReasonTypeEnum.exist(req.getReasonType())){
			result.toFail("返调度原因类型不存在！");
			return ;
		}
	}

	@Override
	public JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage(ReassignWaybillApprovalRecordQuery query) {
		if(log.isInfoEnabled()){
			log.info("获取运单返调度申请记录分页查询入参-{}",JSON.toJSONString(query));
		}
		JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> pageDtoJdResult = new JdResult<>();
		if(query == null
				|| query.getPageSize() == null
				|| query.getPageNumber() == null){
			pageDtoJdResult.toFail("入参不能为空!");
		}
		if(StringUtils.isNotBlank(query.getBarCode())){
			query.setBarCode(query.getBarCode().trim());
		}
		PageDto<ReassignWaybillApprovalRecordResponse> pageDto = new PageDto<>(query.getPageNumber(),query.getPageSize());
		//查询总数
		Integer total = reassignWaybillApprovalRecordDao.queryTotalByCondition(query);
		//查询记录列表
		List<ReassignWaybillApprovalRecord> approvalRecordPageList = reassignWaybillApprovalRecordDao.getApprovalRecordPageListByCondition(query);
		List<ReassignWaybillApprovalRecordResponse> recordList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(approvalRecordPageList)){
			for (ReassignWaybillApprovalRecord record:approvalRecordPageList) {
				ReassignWaybillApprovalRecordResponse recordResponse = new ReassignWaybillApprovalRecordResponse();
				recordResponse.setProvinceAgencyCode(record.getProvinceAgencyCode());
				recordResponse.setProvinceAgencyName(record.getProvinceAgencyName());
				recordResponse.setAreaHubCode(record.getAreaHubCode());
				recordResponse.setAreaHubName(record.getAreaHubName());
				recordResponse.setSiteCode(record.getSiteCode());
				recordResponse.setSiteName(record.getSiteName());
				recordResponse.setBarCode(record.getBarCode());
				recordResponse.setReceiveSiteCode(record.getReceiveSiteCode());
				recordResponse.setReceiveSiteName(record.getReceiveSiteName());
				recordResponse.setChangeSiteCode(record.getChangeSiteCode());
				recordResponse.setChangeSiteName(record.getChangeSiteName());
				recordResponse.setApplicationUserErp(record.getApplicationUserErp());
				recordResponse.setSubmitTime(record.getSubmitTime());
				recordResponse.setChangeSiteReasonTypeCode(record.getChangeSiteReasonType());
				recordResponse.setChangeSiteReasonTypeName(ReassignWaybillReasonTypeEnum.getNameByCode(record.getChangeSiteReasonType()));
				recordList.add(recordResponse);
			}
		}
		pageDto.setResult(recordList);
		pageDto.setTotalRow(total);
		pageDtoJdResult.setData(pageDto);
		return pageDtoJdResult;
	}

	@Override
	public JdResult<Integer> getReassignWaybillRecordCount(ReassignWaybillApprovalRecordQuery query) {
		JdResult<Integer> result = new JdResult<>();
		result.toSuccess();
		result.setData(reassignWaybillApprovalRecordDao.queryTotalByCondition(query));
		return result;
	}

	@Override
	public void dealApproveResult(HistoryApprove historyApprove) {

		// 审批工单号
		String processInstanceNo = historyApprove.getProcessInstanceNo();
		// 审批人ERP
		String approveErp = historyApprove.getApprover();
		// 当前审批节点编码
		String nodeName = historyApprove.getNodeName();
		// 当前节点的审批结果状态
		int approveStatus = Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue()) ? JyApproveStatusEnum.PASS.getCode()
				: Objects.equals(historyApprove.getState(), ApprovalResult.REJECT.getValue()) ? JyApproveStatusEnum.REJECT.getCode()
				: JyApproveStatusEnum.UNKNOWN.getCode();
		if(Objects.equals(approveStatus, JyApproveStatusEnum.UNKNOWN.getCode())){
			log.warn("当前节点:{}的审批状态为:{}不合法!", historyApprove.getNodeName(), historyApprove.getState());
			return;
		}
		ImmutablePair<Integer, String> pairResult = queryApproveData(processInstanceNo);
		if(pairResult == null){
			log.warn("根据审批工单号:{}未查询到审批工单详情!", processInstanceNo);
			return;
		}
		// 审批次数
		Integer approveCount = pairResult.left;
		// 业务单号
		String barCode = pairResult.right;

		// 审批流程是否完结标识
		// 1、任一节点驳回则审批流程结束
		// 2、多个节点的审批则最终节点的审批通过为流程结束
		boolean flowEndFlag = Objects.equals(historyApprove.getState(), ApprovalResult.REJECT.getValue());;
		// 审批流程最终结果:true-审批通过,false-审批驳回
		boolean approveFinalResult = Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
		switch (ReassignWaybillApproveStageEnum.convertApproveEnum(nodeName)) {
			case FIRST:
				log.info("返调度审批工单号:{}的一级审批结果:{}", barCode, historyApprove.getState());
				approveFinalResult = approveFinalResult && Objects.equals(approveCount, ReassignWaybillApproveStageEnum.FIRST.getCount())
						&& Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
				flowEndFlag = flowEndFlag || Objects.equals(approveCount, ReassignWaybillApproveStageEnum.FIRST.getCount());
				updateReassignWaybillApproveResult(barCode, approveStatus, approveErp, ReassignWaybillApproveStageEnum.FIRST.getCode());
				break;
			case SECOND:
				log.info("返调度审批工单号:{}的二级审批结果:{}", barCode, historyApprove.getState());
				approveFinalResult = approveFinalResult && Objects.equals(approveCount, ReassignWaybillApproveStageEnum.SECOND.getCount())
						&& Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
				flowEndFlag = flowEndFlag || Objects.equals(approveCount, ReassignWaybillApproveStageEnum.SECOND.getCount());
				updateReassignWaybillApproveResult(barCode, approveStatus, approveErp, ReassignWaybillApproveStageEnum.SECOND.getCode());
				break;
			default:
				log.warn("未知节点编码:{}", barCode);
				return;
		}
		if(flowEndFlag){
			return ;
		}
		if(approveFinalResult){
			//执行返调度
			String cacheKey = Constants.REASSIGN_WAYBILLCACHE_KEY_PREFIX + barCode;
			String cacheContent = redisClientOfJy.get(cacheKey);
			if(log.isInfoEnabled()){
				log.info("返调度审批缓存信息-{}",cacheContent);
			}
			if(StringUtils.isBlank(cacheContent)){
				return;
			}
			ReassignWaybillReq reassignWaybillReq = JSON.parseObject(cacheContent, ReassignWaybillReq.class);
			if(reassignWaybillReq == null){
				return ;
			}
			returnPack(reassignWaybillReq);

		}


	}

	private void updateReassignWaybillApproveResult(String barCode, int approveStatus, String approveErp, int approveStage){

		ReassignWaybillApprovalRecord approvalRecord = new ReassignWaybillApprovalRecord();
		approvalRecord.setUpdateTime(new Date());
		approvalRecord.setBarCode(barCode);
		if (Objects.equals(JyExScrapApproveStageEnum.FIRST.getCode(), approveStage)) {
			approvalRecord.setFirstChecker(approveErp);
			approvalRecord.setFirstCheckStatus(approveStatus);
			approvalRecord.setFirstCheckTime(new Date());
		}
		if (Objects.equals(JyExScrapApproveStageEnum.SECOND.getCode(), approveStage)) {
			approvalRecord.setSecondChecker(approveErp);
			approvalRecord.setSecondCheckStatus(approveStatus);
			approvalRecord.setSecondCheckTime(new Date());
		}
		reassignWaybillApprovalRecordDao.update(approvalRecord);

	}

	private ImmutablePair<Integer, String> queryApproveData(String processInstanceNo) {
		ApproveRequestOrder approveRequestOrder = flowServiceManager.getRequestOrder(processInstanceNo);
		if(approveRequestOrder == null || approveRequestOrder.getArgs() == null){
			log.warn("根据审批工单号{}未查询到生鲜报废的审批流程!", processInstanceNo);
			return null;
		}
		Map<String, Object> argsMap = approveRequestOrder.getArgs();
		Map<String, Object> flowControlMap = JsonHelper.json2MapNormal(JsonHelper.toJson(argsMap.get(FlowConstants.FLOW_DATA_MAP_KEY_FLOW_CONTROL)));
		if(flowControlMap == null){
			log.warn("根据申请单号{}未查询到设置的流程对象!", processInstanceNo);
			return null;
		}
		Map<String, Object> businessMap = JsonHelper.json2MapNormal(JsonHelper.toJson(argsMap.get(FlowConstants.FLOW_DATA_MAP_KEY_BUSINESS_DATA)));
		if(businessMap == null){
			log.warn("根据申请单号{}未查询到设置的业务数据对象!", processInstanceNo);
			return null;
		}
		return ImmutablePair.of(Integer.valueOf(String.valueOf(flowControlMap.get(FlowConstants.FLOW_DATA_MAP_REASSIGN_WAYBILL_APPROVE_COUNT))),
				String.valueOf(businessMap.get(FlowConstants.FLOW_BUSINESS_NO_KEY)));
	}

	private List<ReassignWaybill> toReassignWaybillList(ReassignWaybillRequest reassignWaybillRequest) {
		List<ReassignWaybill> packList = new ArrayList<>();
		if(reassignWaybillRequest.getPackageCodeList() == null) {
			return packList;
		}
		for(String packageCode:reassignWaybillRequest.getPackageCodeList()) {
			reassignWaybillRequest.setPackageBarcode(packageCode);
			packList.add(ReassignWaybill.toReassignWaybill(reassignWaybillRequest));
		}
		return packList;
	}

}
