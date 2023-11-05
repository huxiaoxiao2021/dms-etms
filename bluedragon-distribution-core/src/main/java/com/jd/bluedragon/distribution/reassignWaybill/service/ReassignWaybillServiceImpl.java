package com.jd.bluedragon.distribution.reassignWaybill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyApproveStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ExpressDispatchServiceManager;
import com.jd.bluedragon.core.jmq.domain.SiteChangeMqDto;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.security.log.SecurityLogWriter;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.enums.ScheduleAfterTypeEnum;
import com.jd.bluedragon.distribution.api.enums.ScheduleReasonTypeEnum;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.api.response.OrderResponse;
import com.jd.bluedragon.distribution.api.response.ReassignOrder;
import com.jd.bluedragon.distribution.api.response.ReassignWaybillApprovalRecordResponse;
import com.jd.bluedragon.distribution.api.response.StationMatchResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.SiteService;
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
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.log.BusinessLogConstans;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.dms.logger.external.LogEngine;
import com.alibaba.fastjson.JSONObject;
import com.jd.lsb.flow.domain.ApprovalResult;
import com.jd.lsb.flow.domain.HistoryApprove;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
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
	public InvokeResult<List<BaseStaffSiteOrgDto>> getSiteByCodeOrName(String siteCodeOrName) {
		InvokeResult<List<BaseStaffSiteOrgDto>> result = new InvokeResult<>();
		if (StringHelper.isEmpty(siteCodeOrName)) {
			return result;
		}
		List<BaseStaffSiteOrgDto> resList = new ArrayList<>();
		if (NumberHelper.isNumber(siteCodeOrName)) {
			/* 站点code精确匹配 */
			resList.add(baseMajorManager.queryDmsBaseSiteByCodeDmsver(siteCodeOrName));
		} else {
			/* 站点名称模糊匹配 */
			resList.addAll(siteService.fuzzyGetSiteBySiteName(siteCodeOrName));
		}
		result.setData(resList);
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
		ScheduleReasonTypeEnum reasonTypeEnum = ScheduleReasonTypeEnum.getEnum(req.getReasonType());
		switch (reasonTypeEnum){
			case UNABLE_TO_DELIVER :
			case CONTROL_CONTRABAND :
				//审核状态中的返调度运单是否存在
				//是不是一个单子此时只能在一个场地操作返调度 ，数据库只存在一个有效的申请
				// 能不能一个场地只能操作一次？ 多个记录的话 如果按运单维度  都会更新
				ReassignWaybillApprovalRecord approvalRecord = reassignWaybillApprovalRecordDao.getApprovalNotPassByBarCode(req.getBarCode());
				if(approvalRecord != null){
					ReassignWaybillApprovalRecord updateApprovalRecord = new ReassignWaybillApprovalRecord();
					updateApprovalRecord.setBarCode(req.getBarCode());
					updateApprovalRecord.setUpdateUserErp(req.getOperateUserErp());
					updateApprovalRecord.setUpdateTime(new Date());
					updateApprovalRecord.setYn(0);
					reassignWaybillApprovalRecordDao.update(approvalRecord);
				}

				//初始化返调度申请信息
				dealReassignWaybillApprovalRecord(req);
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

		reassignWaybillApprovalRecordDao.selectByBarCode(mq.getBarCode());


	}


	/**
     * 初始化重新分配运单审批记录
     * @param req 重新分配运单请求
     * @return record 重新分配运单审批记录
     */
	private void dealReassignWaybillApprovalRecord(ReassignWaybillReq req){
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
		log.info("buildJsonCommand -{}",JSON.toJSONString(jsonCommand));
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
		log.info("buildWaybillForPreSortOnSiteRequest -{}",JSON.toJSONString(request));
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
		if(!ScheduleReasonTypeEnum.exist(req.getReasonType())){
			result.toFail("返调度原因类型不存在！");
			return ;
		}
	}

	@Override
	public JdResult<PageDto<ReassignWaybillApprovalRecordResponse>> getReassignWaybillRecordListByPage() {
		return null;
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
