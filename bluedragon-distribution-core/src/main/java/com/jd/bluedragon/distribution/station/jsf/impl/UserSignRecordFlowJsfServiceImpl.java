package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.station.api.UserSignRecordFlowJsfService;
import com.jd.bluedragon.distribution.station.domain.UserSignFlowRequest;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignFlowTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordHistoryService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.lsb.flow.exception.FlowException;
import com.jd.lsb.flow.service.FlowManageService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * 人员签到表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
@Slf4j
@Service("userSignRecordFlowJsfService")
public class UserSignRecordFlowJsfServiceImpl implements UserSignRecordFlowJsfService {

	@Autowired
	@Qualifier("userSignRecordService")
	private UserSignRecordService userSignRecordService;
	
	@Autowired
	@Qualifier("userSignRecordHistoryService")
	private UserSignRecordHistoryService userSignRecordHistoryService;

	@Autowired
	@Qualifier("userSignRecordFlowService")
	private UserSignRecordFlowService userSignRecordFlowService;
	
	@Autowired
	FlowServiceManager flowServiceManager;
	
	@Override
	public Result<Boolean> addSignFlow(UserSignFlowRequest addRequest) {
		Result<Boolean> result = this.checkParamForAddSignFlow(addRequest);
		if(!result.isSuccess()){
		    return result;
		}
		boolean createFlow = startFlow(addRequest);
		if(!createFlow) {
			result.toFail("流程审批发起失败，请重新提交！");
			return result;
		}
		userSignRecordFlowService.addData(addRequest.getUserSignRecordFlow());
		return result;
	}
	private boolean startFlow(UserSignFlowRequest addRequest) {
        // OA数据
        Map<String,Object> oaMap = new HashMap<>();

        String flowKey = FlowConstants.FLOW_CODE_SIGN_MODIFY + Constants.SEPARATOR_VERTICAL_LINE
                + FlowConstants.FLOW_VERSION + Constants.SEPARATOR_VERTICAL_LINE + System.currentTimeMillis();
        
        UserSignRecordFlow signData= addRequest.getUserSignRecordFlow();
        Integer flowType = signData.getFlowType();
        String flowTypeName = SignFlowTypeEnum.getNameByCode(flowType);
        String flowTitle = "签到数据【"+flowTypeName+"】申请单";
        String flowRemark = "签到数据【"+flowTypeName+"】申请";
        
        List<String> mainColList = new ArrayList<>();
        mainColList.add("ERP|身份证号:" + signData.getUserCode());
        mainColList.add("签到网格、工序:" + signData.getGridName()+"、"+signData.getGridName());
        mainColList.add("原签到时间:" + DateHelper.formatDateTime(signData.getSignInTime()));
        mainColList.add("原签退时间:" + DateHelper.formatDateTime(signData.getSignOutTime()));
        if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
            mainColList.add("修改后签到时间:" + DateHelper.formatDateTime(signData.getSignInTimeNew()));
            mainColList.add("修改后签退时间:" + DateHelper.formatDateTime(signData.getSignOutTimeNew()));
        }
        mainColList.add("申请人:" + addRequest.getOperateUserCode());
        
        oaMap.put(FlowConstants.FLOW_OA_JMEMAINCOLLIST,mainColList);
        oaMap.put(FlowConstants.FLOW_OA_JMEREQNAME, flowTitle);
        oaMap.put(FlowConstants.FLOW_OA_JMEREQCOMMENTS, flowRemark);
        // 业务数据
        Map<String,Object> businessMap = new HashMap<>();
        // 设置业务唯一编码
        businessMap.put(FlowConstants.FLOW_BUSINESS_NO_KEY,flowKey);
        // 设置业务其他信息

		// 提交申请单
		String flowWorkNo = flowServiceManager.startFlow(oaMap, businessMap, null,
		        FlowConstants.FLOW_CODE_PRINT_HANDOVER, addRequest.getOperateUserCode(), flowKey);
		signData.setRefFlowBizCode(flowWorkNo);
		if(flowWorkNo != null) {
			log.info("签到流程发起成功！工单号={},业务单号={}",flowWorkNo,flowKey);
			return true;
		}
		return false;
	}
	private Result<Boolean> checkParamForAddSignFlow(UserSignFlowRequest addRequest) {
		Result<Boolean> result = Result.success();
		if(addRequest == null) {
			result.toFail("传入参数不能为空！");
			return result;
		}
		if(addRequest.getOperateSiteCode() == null) {
			result.toFail("操作场地不能为空！");
			return result;
		}
		if(StringUtils.isBlank(addRequest.getOperateUserCode())) {
			result.toFail("操作人Erp不能为空！");
			return result;
		}
		Integer flowType = addRequest.getFlowType();
		UserSignRecordFlow signData = null;
		if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)
				|| SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			if(addRequest.getRecordId() == null) {
				result.toFail("请选择要操作的数据！");
				return result;
			}
			signData = userSignRecordHistoryService.queryById(addRequest.getRecordId());
			if(signData == null) {
				result.toFail("无效的签到数据！");
				return result;
			}
		}
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			signData = new UserSignRecordFlow();
		}
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)
				|| SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			Date signInTimeNew = DateHelper.parseDateTime(addRequest.getSignInTimeNewStr());
			Date signOutTimeNew = DateHelper.parseDateTime(addRequest.getSignOutTimeNewStr());
			if(signInTimeNew == null || signOutTimeNew == null) {
				result.toFail("签到、签退时间不能为空！");
				return result;
			}
			if(signOutTimeNew.before(signInTimeNew)) {
				result.toFail("签到时间需要小于签退时间！");
				return result;
			}
		}		
		signData.setFlowCreateTime(new Date());
		signData.setFlowCreateUser(addRequest.getOperateUserCode());
		addRequest.setUserSignRecordFlow(signData);
		return result;
	}
	
	@Override
	public Result<PageDto<UserSignRecordFlow>> queryFlowPageList(UserSignRecordFlowQuery query) {
		Result<PageDto<UserSignRecordFlow>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<UserSignRecordFlow> pageData = new PageDto<>(query.getPageNumber(), query.getPageSize());
		boolean queryHistory = true;
		//只查询流程数据
		if(query.getFlowType() != null) {
			queryHistory = false;
		}
		Integer totalCount = 0;
		Integer historyCount = 0;
		Integer flowCount = userSignRecordFlowService.queryFlowCount(query);
		if(queryHistory) {
			historyCount = userSignRecordHistoryService.querySignCount(query);
		}
		boolean hasFlowData = (flowCount != null && flowCount > 0);
		boolean hasHistoryData = (historyCount != null && historyCount > 0);
		List<UserSignRecordFlow> dataList = new ArrayList<UserSignRecordFlow>();
		if(hasFlowData || hasHistoryData){
			if(hasFlowData) {
				List<UserSignRecordFlow> flowList = userSignRecordFlowService.queryFlowList(query);
				if(!CollectionUtils.isEmpty(flowList)) {
					dataList.addAll(flowList);
				}
			}
			if(hasHistoryData) {
				List<UserSignRecordFlow> historyList = userSignRecordHistoryService.querySignList(query);
				if(!CollectionUtils.isEmpty(historyList)) {
					dataList.addAll(historyList);
				}
			}
			Collections.sort(dataList, new Comparator<UserSignRecordFlow>(){
				@Override
				public int compare(UserSignRecordFlow o1, UserSignRecordFlow o2) {
					if(o1.getRefRecordId() != null && o2.getRefRecordId() != null) {
						return o1.getRefRecordId().compareTo(o2.getRefRecordId());
					}
					return 0;
				}
			});
		    pageData.setResult(dataList);
		    pageData.setTotalRow(totalCount.intValue());
		}else {
			pageData.setResult(new ArrayList<UserSignRecordFlow>());
			pageData.setTotalRow(0);
		}
		pageData.setResult(dataList);
		pageData.setTotalRow(totalCount);
		result.setData(pageData);
		return result;
	}
	/**
	 * 查询参数校验
	 * @param query
	 * @return
	 */
	public Result<Boolean> checkParamForQueryPageList(UserSignRecordFlowQuery query){
		Result<Boolean> result = Result.success();
		if(query == null) {
			result.toFail("查询参数不能为空！");
			return result;
		}
		if(query.getSiteCode() == null) {
			result.toFail("站点不能为空！");
			return result;
		}
		if(StringUtils.isBlank(query.getUserCode())) {
			result.toFail("ERP|身份证号不能为空！");
			return result;
		}
		Date signDate = null;
		if(StringHelper.isNotEmpty(query.getSignDateStr())) {
			signDate = DateHelper.parseDate(query.getSignDateStr(),DateHelper.DATE_FORMAT_YYYYMMDD);
		}else {
			result.toFail("签到日期不能为空！");
			return result;
		}
		query.setSignDate(signDate);
		if(query.getPageSize() == null
				|| query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		}
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		}
		return result;
	 }
}
