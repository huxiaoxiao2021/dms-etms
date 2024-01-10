package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyApproveStatusEnum;
import com.jd.bluedragon.common.dto.sysConfig.response.ReassignWaybillProvinceAreaApprovalConfigDto;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ExpressDispatchServiceManager;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.security.log.SecurityLogWriter;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.enums.ReassignWaybillApproveStageEnum;
import com.jd.bluedragon.distribution.api.enums.ReassignWaybillCheckEndFlagEnum;
import com.jd.bluedragon.distribution.api.enums.ScheduleAfterTypeEnum;
import com.jd.bluedragon.distribution.api.enums.ReassignWaybillReasonTypeEnum;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;

import com.jd.bluedragon.distribution.jsf.domain.ReassignWaybillReq;
import com.jd.bluedragon.distribution.jsf.domain.StationMatchRequest;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
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
import com.jd.bluedragon.dms.utils.BusinessUtil;
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
import com.jd.ql.basic.domain.BaseSite;
import com.jd.ql.basic.domain.PsStoreInfo;
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

import static com.jd.bluedragon.Constants.REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG_FLOW_VERSION_NEW;
import static com.jd.bluedragon.Constants.SITE_TYPE_SPWMS;
import static com.jd.bluedragon.distribution.api.enums.ReassignWaybillReasonTypeEnum.JUDGMENT_REASSIGN;
import static com.jd.bluedragon.distribution.api.enums.ReassignWaybillReasonTypeEnum.UNABLE_TO_DELIVER;
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

	private Integer CHECKER_LIMIT = 3;


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
			siteChangeMqDto.setWaybillCode(packTagPrint.getWaybillCode().trim());
			siteChangeMqDto.setNewSiteId(packTagPrint.getChangeSiteCode());
			siteChangeMqDto.setNewSiteName(packTagPrint.getChangeSiteName());
			siteChangeMqDto.setNewSiteRoadCode("0"); // 此操作无法触发预分拣 故传默认值0
			siteChangeMqDto.setOperatorId(packTagPrint.getUserCode());
			siteChangeMqDto.setOperatorName(packTagPrint.getUserName());
			siteChangeMqDto.setOperatorSiteId(packTagPrint.getSiteCode());
			siteChangeMqDto.setOperatorSiteName(packTagPrint.getSiteName());
			siteChangeMqDto.setOperateTime(DateHelper.formatDateTime(new Date()));
			try {
				waybillSiteChangeProducer.sendOnFailPersistent(packTagPrint.getWaybillCode().trim(), JsonHelper.toJsonUseGson(siteChangeMqDto));
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
			siteChangeMqDto.setWaybillCode(packTagPrint.getWaybillCode().trim());
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
				waybillSiteChangeProducer.sendOnFailPersistent(packTagPrint.getWaybillCode().trim(), JsonHelper.toJsonUseGson(siteChangeMqDto));
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
		if(log.isInfoEnabled()){
			log.info("getReassignOrderInfo 入参-{}",JSON.toJSONString(packageCode));
		}
		JdResult<ReassignOrder> result = new JdResult<>();
		result.toSuccess();
		ReassignOrder  reassignOrder = new ReassignOrder();
		if(StringUtils.isBlank(packageCode)){
			result.toFail("入参不能为空！");
			return result;
		}
		try{
			OrderResponse orderResponse = this.waybillService.getDmsWaybillInfoAndCheck(packageCode);
			result.setCode(orderResponse.getCode());
			result.setMessage(orderResponse.getMessage());
			if(!OrderResponse.CODE_OK.equals(orderResponse.getCode())){
				return result;
			}
			getHideInfo(orderResponse,decryptFields);
			SecurityLogWriter.orderResourceGetOrderResponseWrite(packageCode, orderResponse);
			BeanCopyUtil.copy(orderResponse,reassignOrder);
			result.setData(reassignOrder);
		}catch (Exception e){
			log.error("获取运单信息异常!-单号-{}",packageCode,e);
			result.toError("获取运单信息异常!");
		}
		return result;
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.ReassignWaybillServiceImpl.stationMatchByAddress", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<StationMatchResponse> stationMatchByAddress(StationMatchRequest request) {
		if(log.isInfoEnabled()){
			log.info("stationMatchByAddress 入参-{}",JSON.toJSONString(request));
		}
		return expressDispatchServiceManager.stationMatchByAddress(request);
	}

	@Override
	@JProfiler(jKey = "DMS.BASE.ReassignWaybillServiceImpl.getSiteByCodeOrName", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<List<BaseStaffResponse>> getSiteByCodeOrName(String siteCodeOrName) {
		JdResult<List<BaseStaffResponse>> result = new JdResult<>();
		result.toSuccess();
		if (StringHelper.isEmpty(siteCodeOrName)) {
			result.toFail("查询内容不能为空!");
			return result;
		}
		siteCodeOrName =siteCodeOrName.trim();
		List<BaseStaffResponse> responseList = new ArrayList<>();
		List<BaseStaffSiteOrgDto> resList = new ArrayList<>();
		try{
			if (NumberHelper.isNumber(siteCodeOrName)) {
				log.info("getSiteByCodeOrName queryDmsBaseSiteByCodeDmsver -入参 {}",siteCodeOrName);
				/* 站点code精确匹配 */
				resList.add(baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCodeOrName));
			} else {
				log.info("getSiteByCodeOrName fuzzyGetSiteBySiteName -入参 {}",siteCodeOrName);
				/* 站点名称模糊匹配 */
				resList.addAll(siteService.fuzzyGetSiteBySiteName(siteCodeOrName));
			}
			if(CollectionUtils.isEmpty(resList)){
				result.toFail("未查询到相关站点信息!");
				return result;
			}
			for (BaseStaffSiteOrgDto dto : resList){
				if(dto != null){
					BaseStaffResponse baseStaff = new BaseStaffResponse();
					baseStaff.setSiteCode(dto.getSiteCode());
					baseStaff.setSiteName(dto.getSiteName());
					responseList.add(baseStaff);
				}
			}

			result.setData(responseList);
		}catch (Exception e){
			log.error("站点匹配接口异常-param-{}",siteCodeOrName,e);
			result.toError("站点匹配接口异常!");
		}

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
		if(log.isInfoEnabled()){
			log.info("executeReassignWaybill 入参-{}",JSON.toJSONString(req));
		}
		JdResult<Boolean> result = new JdResult<>();
		result.setData(Boolean.TRUE);
		result.toSuccess("请求成功");
		String lockKey = Constants.REASSIGN_WAYBILL_LOCK_KEY_PREFIX + req.getBarCode();
		try{

			//加锁防止重复提交
			Boolean lockFlag = redisClientOfJy.set(lockKey, "1", 10, TimeUnit.SECONDS, false);
			if (!lockFlag) {
				result.setData(Boolean.FALSE);
				result.toFail("单号"+req.getBarCode()+"正在执行返调度，请稍后重新");
				return result;
			}
			checkParam(result,req);
			if(!JdResult.CODE_SUC.equals(result.getCode())){
				result.setData(Boolean.FALSE);
				return result;
			}

			//现场预分拣拦截校验
			WaybillForPreSortOnSiteRequest preSortOnSiteRequest = buildWaybillForPreSortOnSiteRequest(req);
			InvokeResult<String> invokeResult = waybillService.checkWaybillForPreSortOnSiteForApprove(preSortOnSiteRequest);
			if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
				result.setCode(invokeResult.getCode());
				result.setMessage(invokeResult.getMessage());
				return result;
			}
			req.setOperateSiteName(preSortOnSiteRequest.getSiteName());
			req.setProvinceAgencyCode(preSortOnSiteRequest.getProvinceAgencyCode());
			req.setProvinceAgencyName(preSortOnSiteRequest.getProvinceAgencyName());
			req.setAreaHubCode(preSortOnSiteRequest.getAreaHubCode());
			req.setAreaHubName(preSortOnSiteRequest.getAreaHubName());
			if(log.isInfoEnabled()){
				log.info("ReassignWaybillReq 入参-{}",JSON.toJSONString(req));
			}
			List<String> checker = getChecker(req.getReturnGroupFlag());
			if(CollectionUtils.isEmpty(checker)){
				String msg = "当前场地未查询到审核人信息! 请联系分拣小秘!";
				result.toFail(msg);
				result.setData(Boolean.FALSE);
				return result;
			}

			if (checker.size() > CHECKER_LIMIT) {
				result.toFail("当前场地审核人超过"+ CHECKER_LIMIT +"! 请联系分拣小秘!");
				result.setData(Boolean.FALSE);
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
					dealNewReassignWaybillApprovalRecord(req,true);
					break;
				case POSTAL_REJECTION :
					//邮政拒收
					BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(req.getOldSiteCode());
					if(!SiteHelper.isPostalSite(baseSite)){
						result.setData(Boolean.FALSE);
						result.toFail("此单非邮政单，禁止选择邮政拒收原因，请继续下传！");
						return result;
					}
					//逻辑删除未审核通过的申请记录 / 取消OA申请单
					dealOldReassignWaybillApprovalRecord(req);
					//新增新的审核申请
					dealNewReassignWaybillApprovalRecord(req,false);
					return returnPack(req);
				case NO_PRE_SORTING_STATION :

					if(checkSiteIsEfficient(req.getOldSiteCode())){
						result.setData(Boolean.FALSE);
						result.toFail("该单有预分拣站点，请继续下传！");
						return result;
					}
					//逻辑删除未审核通过的申请记录 / 取消OA申请单
					dealOldReassignWaybillApprovalRecord(req);
					//新增新的审核申请
					dealNewReassignWaybillApprovalRecord(req, false);
					return returnPack(req);
				case RECOMMENDS_WAREHOUSE_NOT_ACC:
				case JUDGMENT_REASSIGN:
				case NO_ROUTING:
					boolean needApproval = true;
					// 若反调度目的为备件库，不需要审批 直接提交成功
					PsStoreInfo psStoreInfo = baseMajorManager.selectBaseStoreByDmsSiteId(req.getSiteOfSchedulingOnSiteCode());
					if (psStoreInfo != null && SITE_TYPE_SPWMS.equals(psStoreInfo.getDsmStoreType())) needApproval = false;

					// 若反调度目的为仓，不需要审批 直接提交成功
					BaseStaffSiteOrgDto siteOfSchedulingOnSite = baseMajorManager.getBaseSiteBySiteId(req.getSiteOfSchedulingOnSiteCode());
					if (BusinessUtil.isWmsSite(siteOfSchedulingOnSite.getSiteType())) needApproval = false;

					//逻辑删除未审核通过的申请记录 / 取消OA申请单
					dealOldReassignWaybillApprovalRecord(req);
					//新增新的审核申请
					dealNewReassignWaybillApprovalRecord(req, needApproval);

					if (!needApproval) {
						return returnPack(req);
					}
					break;
				default:
					log.warn("未知的返调度类型！");
					result.setData(Boolean.FALSE);
					result.toFail("未知的返调度类型！");
					return result;
			}
		}catch (Exception e){
			log.error("返调度执行出现异常-param-{}",JSON.toJSONString(req),e);
			result.setData(Boolean.FALSE);
			result.toError("返调度执行出现异常!");
		}finally {
			redisClientOfJy.del(lockKey);
		}
		return result;
	}


	/**
	 * 校验
	 * @param siteCode
	 * @return true  有效站点（营业部 、分拣中心、仓） false :无效
	 */
	private boolean checkSiteIsEfficient(Integer siteCode){
		if(siteCode != null && siteCode >0){
			//查询营业部
			BaseSite baseSite = baseMajorManager.getSiteBySiteCode(siteCode);
			if( baseSite != null){
				return true;
			}
			//查询库房信息
			PsStoreInfo psStoreInfo = baseMajorManager.selectBaseStoreByDmsSiteId(siteCode);
			if(psStoreInfo != null){
				return true;
			}
		}
		return false;
	}

	private List<String> getChecker(Boolean returnGroupFlag){
		SysConfig configContent = sysConfigService.findConfigContentByConfigName(Constants.REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG_NEW);
		if(configContent == null){
			throw new JyBizException("获取返调度省区审核配置为空!");
		}
		ReassignWaybillProvinceAreaApprovalConfigDto configDto = JSON.parseObject(configContent.getConfigContent(), ReassignWaybillProvinceAreaApprovalConfigDto.class);
		Integer type;
		if (returnGroupFlag) {
			type = JUDGMENT_REASSIGN.getType();
		}else {
			type = UNABLE_TO_DELIVER.getType();
		}
		Map<String, List<String>> configList = configDto.getConfigList();
		if(!CollectionUtils.isEmpty(configList)){
			return configList.get(type + "");
		}
		return null;
	}

	@Override
	public void dealReassignWaybillApprove(ReassignWaybillApprovalRecordMQ mq) {

		ReassignWaybillApprovalRecord record = reassignWaybillApprovalRecordDao.selectByBarCodeApprovalNoPass(mq.getBarCode());
		if(record == null){
			log.warn("未查到当前运单的申请记录-{}",mq.getBarCode());
			return ;
		}
		// 提交审批
		Map<String, Object> oaMap = buildOA(mq); // OA数据
		Map<String, Object> businessMap = buildBusiness(mq); // 业务数据
		Map<String, Object> flowMap = buildFlow(mq); // 流程数据

		Integer flowVersion = null;
		SysConfig conf = sysConfigService.findConfigContentByConfigName(REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG_FLOW_VERSION_NEW);
		if (conf != null && conf.getConfigContent() != null) {
			flowVersion = Integer.valueOf(conf.getConfigContent());
		}

		String approveOrderCode = flowServiceManager.startFlow(
				oaMap, businessMap, flowMap, FlowConstants.FLOW_CODE_REASSIGN_WAYBILL_NEW, mq.getApplicationUserErp(), mq.getBarCode(), flowVersion);
		if (log.isInfoEnabled()) {
			log.info("提交审批完成，审批工单号:{}", approveOrderCode);
		}
		if(StringUtils.isNotBlank(approveOrderCode)){
			ReassignWaybillApprovalRecord updaDateRecord = new ReassignWaybillApprovalRecord();
			updaDateRecord.setProvinceAgencyCode(mq.getProvinceAgencyCode());
			updaDateRecord.setProvinceAgencyName(mq.getProvinceAgencyName());
			updaDateRecord.setAreaHubCode(mq.getAreaHubCode());
			updaDateRecord.setAreaHubName(mq.getAreaHubName());
			updaDateRecord.setBarCode(mq.getBarCode());
			if (log.isInfoEnabled()) {
				log.info("提交审批,更新返调度审批数据:{}", JSON.toJSONString(updaDateRecord));
			}
			reassignWaybillApprovalRecordDao.updateByBarCodeApprovalNoPass(updaDateRecord);
		}
		// 将当前单号的审批工单号放入缓存  等到取消审批时使用
		redisClientOfJy.setEx(mq.getBarCode(),approveOrderCode,7,TimeUnit.DAYS);
	}


	private Map<String, Object> buildFlow(ReassignWaybillApprovalRecordMQ mq) {
		Map<String, Object> flowControlMap = Maps.newHashMap();
//		BaseSite baseSite = baseMajorManager.getSiteBySiteCode(mq.getSiteCode());
//		if(baseSite == null){
//			throw new  JyBizException("获取"+mq.getSiteCode()+"基础站点信息失败");
//		}
//		String provinceAgencyCode = baseSite.getProvinceAgencyCode();
//		String areaCode = baseSite.getAreaCode();
//		mq.setProvinceAgencyCode(provinceAgencyCode);
//		mq.setProvinceAgencyName(baseSite.getProvinceAgencyName());
//		mq.setAreaHubCode(areaCode);
//		mq.setAreaHubName(baseSite.getAreaName());

		List<String> checker = getChecker(mq.getReturnGroupFlag());
		if (CollectionUtils.isEmpty(checker)) {
			log.error("处理当前单号-{}-获取返调度审核配置为空!", mq.getBarCode());
			throw new JyBizException("获取返调度审核配置为空!");
		}

		Integer approveCount = checker.size();
		flowControlMap.put(FlowConstants.FLOW_DATA_MAP_REASSIGN_WAYBILL_APPROVE_COUNT, approveCount);

		Integer index = 0;
		for (String erp : checker) {
			index++;
			flowControlMap.put(String.format(FlowConstants.FLOW_DATA_MAP_FIRST_REASSIGN_WAYBILL_APPROVE_ERP_PREFIX, index), erp);
		}
		if(log.isInfoEnabled()){
			log.info("返调度审批 buildFlow -{}",JSON.toJSONString(flowControlMap));
		}
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
		if(log.isInfoEnabled()){
			log.info("返调度审批 buildBusiness -{}",JSON.toJSONString(businessMap));
		}
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
		mainColList.add("单号:" + mq.getBarCode());
		mainColList.add("原预分拣站点:" + mq.getReceiveSiteName());
		mainColList.add("反调度站点:" + mq.getChangeSiteName());
		mainColList.add("反调度原因:" + ReassignWaybillReasonTypeEnum.getNameByCode(mq.getChangeSiteReasonType()));
		mainColList.add("提报人:" + mq.getApplicationUserErp());
		mainColList.add("提报场地:" + mq.getSiteName());
		mainColList.add("提报时间:" + mq.getSubmitTime());
		if (!CollectionUtils.isEmpty(mq.getPhotoUrlList())) {
			for (String url : mq.getPhotoUrlList()) {
				mainColList.add("包裹照片:" + url);
			}
		}

		oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST, mainColList);
		if(log.isInfoEnabled()){
			log.info("返调度审批 buildOA -{}",JSON.toJSONString(oaMap));
		}
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
        //逻辑删除之前正在审批中的返调度申请记录
		int update = reassignWaybillApprovalRecordDao.updateReassignWaybilApprovalNotPassByBarCode(params);
		if(log.isInfoEnabled()){
			log.info("单号-{}，更新结果：{}",req.getBarCode(),update);
		}
		//根据单号获取上次申请的审批工单号 取消OA申请
		String approveOrderCode = redisClientOfJy.get(req.getBarCode());
		if(log.isInfoEnabled()){
			log.info("根据单号-{}-获取审批工单号-{}",req.getBarCode(),approveOrderCode);
		}
		if(StringUtils.isNotBlank(approveOrderCode) ){
			flowServiceManager.cancelRequestOrder(approveOrderCode,req.getOperateUserErp());
			//删除缓存中单号对应的审批工单号
			redisClientOfJy.del(req.getBarCode());
		}
	}

	/**
     * 初始化重新分配运单审批记录
     * @param req 重新分配运单请求
     * @return record 重新分配运单审批记录
     */
	private void dealNewReassignWaybillApprovalRecord(ReassignWaybillReq req, boolean needApproval){


		ReassignWaybillApprovalRecord record = new ReassignWaybillApprovalRecord();
		record.setSiteCode(req.getOperateSiteCode());
		record.setSiteName(req.getOperateSiteName());
		record.setApplicationUserErp(req.getOperateUserErp());
		record.setBarCode(req.getBarCode());
		record.setSubmitTime(new Date());
		record.setChangeSiteReasonType(req.getReasonType());
		record.setReceiveSiteCode(req.getOldSiteCode());
		record.setReceiveSiteName(req.getOldSiteName());
		record.setChangeSiteCode(req.getSiteOfSchedulingOnSiteCode());
		record.setChangeSiteName(req.getSiteOfSchedulingOnSiteName());
		record.setCreateUserErp(req.getOperateUserErp());
		if(needApproval){
			record.setCheckEndFlag(ReassignWaybillCheckEndFlagEnum.NO_END.getCode());
		}else {
			record.setCheckEndFlag(ReassignWaybillCheckEndFlagEnum.END.getCode());
		}
		record.setCreateTime(new Date());
		record.setProvinceAgencyCode(req.getProvinceAgencyCode());
		record.setProvinceAgencyName(req.getProvinceAgencyName());
		record.setAreaHubCode(req.getAreaHubCode());
		record.setAreaHubName(req.getAreaHubName());
		if (!CollectionUtils.isEmpty(req.getPhotoUrlList())) {
			record.setImageUrls(String.join(";",req.getPhotoUrlList()));
		}
		if(log.isInfoEnabled()){
			log.info("initReassignWaybillApprovalRecord- {}",JSON.toJSONString(record));
		}
		Boolean add = reassignWaybillApprovalRecordDao.add(record);
		if(!add){
			throw new JyBizException("初始化添加运单返调度记录数据失败！");
		}
		if(!needApproval){
			log.warn("当前单号-{}不需要审批",req.getBarCode());
			return ;
		}
		ReassignWaybillApprovalRecordMQ recordMQ = new ReassignWaybillApprovalRecordMQ();
		BeanUtils.copyProperties(record,recordMQ);
		recordMQ.setSubmitTime(DateHelper.formatDateTime(record.getSubmitTime()));
		recordMQ.setReturnGroupFlag(req.getReturnGroupFlag());
		recordMQ.setPhotoUrlList(req.getPhotoUrlList());
		if(log.isInfoEnabled()){
			log.info("返调度审批异步消息发送!-{}",JSON.toJSONString(recordMQ) );
		}
		reassignWaybillApprovalProducer.sendOnFailPersistent(req.getBarCode(), JsonHelper.toJson(recordMQ));
		return ;
	}

	/**
	 * 返调度
	 * @param req
	 */
	private JdResult<Boolean>  returnPack(ReassignWaybillReq req){
		//获取打印信息
		String jsonCommand = buildJsonCommand(req);
		if(log.isInfoEnabled()){
			log.info("jsonCommand -{}",jsonCommand);
		}
		String printInfo = commandService.execute(jsonCommand);
		if(log.isInfoEnabled()){
			log.info("printInfo -{}",printInfo);
		}

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
		if(log.isInfoEnabled()){
			log.info("jsonObject -{}",printInfo);
		}
		Object data = jsonObject.get("data");
		if(data == null){
			return null;
		}
		JSONObject dataObject = JSON.parseObject(data.toString());
		String printAddress = dataObject.get("printAddress").toString();
		String printTime = dataObject.get("printTime").toString();
		JSONArray packList = dataObject.getJSONArray("packList");
		if(StringUtils.isBlank(printAddress)
			|| StringUtils.isBlank(printTime)
			|| CollectionUtils.isEmpty(packList)){
			return null;
		}
		List<String> packageCodeList = new ArrayList<>();
		for (int i = 0; i < packList.size(); i++) {
			JSONObject packListJSONObject = packList.getJSONObject(i);
			String packageCode = packListJSONObject.get("packageCode").toString();
			packageCodeList.add(packageCode);
		}
		request.setPackageBarcode(req.getBarCode());
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
		request.setInterfaceType(WaybillPrintOperateTypeEnum.RESCHEDULE_PRINT.getType());
		request.setScheduleAfterType(ScheduleAfterTypeEnum.PACKAGE_CODE_LIST.getCode());
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
		paramMap.put("barCode",WaybillUtil.getWaybillCode(req.getBarCode()));
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
		request.setReasonType(req.getReasonType());
		request.setReturnGroupFlag(req.getReturnGroupFlag());
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
				|| query.getPageNumber() == null
				|| query.getStartSubmitTime() == null
				|| query.getEndSubmitTime() == null){
			pageDtoJdResult.toFail("入参不能为空!");
			return pageDtoJdResult;
		}
		if(StringUtils.isNotBlank(query.getBarCode())){
			query.setBarCode(query.getBarCode().trim());
		}
		query.setCheckEndFlag(ReassignWaybillCheckEndFlagEnum.END.getCode());
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
				recordResponse.setSubmitTime(DateHelper.formatDateTime(record.getSubmitTime()));
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
		query.setCheckEndFlag(ReassignWaybillCheckEndFlagEnum.END.getCode());
		JdResult<Integer> result = new JdResult<>();
		result.toSuccess();
		result.setData(reassignWaybillApprovalRecordDao.queryTotalByCondition(query));
		return result;
	}

	@Override
	public void dealApproveResult(HistoryApprove historyApprove) {
		// 审批工单号
		String processInstanceNo = historyApprove.getProcessInstanceNo();
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
        //判断当前审批节点是否结束
		//流程完结
		boolean flowEndFlag;
		// 审批流程最终结果:true-审批通过,false-审批驳回
		boolean approveFinalResult;
		switch (ReassignWaybillApproveStageEnum.convertApproveEnum(nodeName)) {
			case FIRST:
				log.info("返调度审批工单号:{}的一级审批结果:{}", barCode, historyApprove.getState());
				approveFinalResult =  Objects.equals(approveCount, ReassignWaybillApproveStageEnum.FIRST.getCount())
						&& Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
				flowEndFlag = Objects.equals(approveCount, ReassignWaybillApproveStageEnum.FIRST.getCount());
				break;
			case SECOND:
				log.info("返调度审批工单号:{}的二级审批结果:{}", barCode, historyApprove.getState());
				approveFinalResult =  Objects.equals(approveCount, ReassignWaybillApproveStageEnum.SECOND.getCount())
						&& Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
				flowEndFlag = Objects.equals(approveCount, ReassignWaybillApproveStageEnum.SECOND.getCount());
				break;
			case THIRD:
				log.info("返调度审批工单号:{}的三级审批结果:{}", barCode, historyApprove.getState());
				approveFinalResult =  Objects.equals(approveCount, ReassignWaybillApproveStageEnum.THIRD.getCount())
						&& Objects.equals(historyApprove.getState(), ApprovalResult.AGREE.getValue());
				flowEndFlag = Objects.equals(approveCount, ReassignWaybillApproveStageEnum.THIRD.getCount());
				break;
			default:
				log.warn("未知节点编码:{}", barCode);
				return;
		}
		// 最终的审核结果为通过才执行
		if(approveFinalResult){
			//执行返调度
			ReassignWaybillApprovalRecord approvalRecord = reassignWaybillApprovalRecordDao.selectByBarCodeApprovalNoPass(barCode);
			if(log.isInfoEnabled()){
				log.info("返调度审批记录-{}",approvalRecord);
			}
			if(approvalRecord == null){
				log.warn("未查询到当前单号-{}提交的返调度审批记录",barCode);
				return;
			}
			ReassignWaybillReq req = coverToReassignWaybillReq(approvalRecord);
			if(req != null){
				returnPack(req);
			}
		}

		if (flowEndFlag) {
			ReassignWaybillApprovalRecord record = new ReassignWaybillApprovalRecord();
			record.setUpdateTime(new Date());
			record.setBarCode(barCode);
			record.setCheckEndFlag(ReassignWaybillCheckEndFlagEnum.END.getCode());
			reassignWaybillApprovalRecordDao.updateByBarCodeApprovalNoPass(record);
			//删除缓存中单号对应的审批工单号
			redisClientOfJy.del(barCode);
		}
	}

	private ReassignWaybillReq coverToReassignWaybillReq(ReassignWaybillApprovalRecord approvalRecord){
		ReassignWaybillReq req = new ReassignWaybillReq();
		BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(approvalRecord.getApplicationUserErp());
		if(baseStaff == null){
			log.error("未查询到 {} 的基础信息",approvalRecord.getApplicationUserErp());
			return null;
		}
		req.setOperateUserErp(approvalRecord.getApplicationUserErp());
		req.setOperateUserCode(baseStaff.getStaffNo());
		req.setOperateUserName(baseStaff.getStaffName());
		req.setOperateSiteCode(approvalRecord.getSiteCode());
		req.setOperateSiteName(approvalRecord.getSiteName());
		req.setBarCode(approvalRecord.getBarCode());
		req.setOldSiteCode(approvalRecord.getReceiveSiteCode());
		req.setOldSiteName(approvalRecord.getReceiveSiteName());
		req.setSiteOfSchedulingOnSiteCode(approvalRecord.getChangeSiteCode());
		req.setSiteOfSchedulingOnSiteName(approvalRecord.getChangeSiteName());
		return  req;
	}

	private ImmutablePair<Integer, String> queryApproveData(String processInstanceNo) {
		ApproveRequestOrder approveRequestOrder = flowServiceManager.getRequestOrder(processInstanceNo);
		if(approveRequestOrder == null || approveRequestOrder.getArgs() == null){
			log.warn("根据审批工单号{}未查询到返调度的审批流程!", processInstanceNo);
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
