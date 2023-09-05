package com.jd.bluedragon.distribution.station.jsf.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.FlowConstants;
import com.jd.bluedragon.common.dto.group.GroupMemberData;
import com.jd.bluedragon.common.dto.station.UserSignRequest;
import com.jd.bluedragon.core.base.FlowServiceManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.station.api.UserSignRecordFlowJsfService;
import com.jd.bluedragon.distribution.station.domain.UserSignFlowRequest;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.enums.SignFlowStatusEnum;
import com.jd.bluedragon.distribution.station.enums.SignFlowTypeEnum;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordFlowService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordHistoryService;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;

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
	/**
	 * 最大签到时间
	 */
	@Value("${beans.userSignRecordFlowJsfService.maxSignRangeHours:18}")
	private int maxSignRangeHours;
	
	@Autowired
	FlowServiceManager flowServiceManager;
	
	@Autowired
	private WorkStationGridManager workStationGridManager;
	
	@Autowired
	private PositionManager positionManager;	
	
	@Autowired
	@Qualifier("jyGroupMemberService")
	private JyGroupMemberService jyGroupMemberService;
	
	@Autowired
	@Qualifier("jyGroupService")
	private JyGroupService jyGroupService;
	/**
	 * 流程状态-不显示历史数据
	 */
	private static List<Integer> hiddenHistoryStatus = new ArrayList<Integer>();
	/**
	 * 流程状态-未完成状态
	 */
	private static List<Integer> unCompletedStatus = new ArrayList<Integer>();
	
	static {
		hiddenHistoryStatus.add(SignFlowStatusEnum.PENDING_APPROVAL.getCode());
		hiddenHistoryStatus.add(SignFlowStatusEnum.ADD_COMPLETE.getCode());
		hiddenHistoryStatus.add(SignFlowStatusEnum.MODIFY_COMPLETE.getCode());
		hiddenHistoryStatus.add(SignFlowStatusEnum.DELETE_COMPLETE.getCode());
		
		unCompletedStatus.add(SignFlowStatusEnum.PENDING_APPROVAL.getCode());
	}
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
		        FlowConstants.FLOW_CODE_SIGN_MODIFY, addRequest.getOperateUserCode(), flowKey);
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
		Date currentTime = new Date();
		Integer flowType = addRequest.getFlowType();
		UserSignRecordFlow signData = null;
		Date signInTimeNew = null;
		Date signOutTimeNew = null;
		Date signDateNew = null;
		if(SignFlowTypeEnum.DELETE.getCode().equals(flowType)
				|| SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			if(addRequest.getRecordId() == null) {
				result.toFail("请选择要操作的数据！");
				return result;
			}
			signData = userSignRecordHistoryService.queryById(addRequest.getRecordId());
			if(signData == null
					|| Constants.YN_NO.equals(signData.getYn())) {
				result.toFail("无效的签到数据！");
				return result;
			}
		}
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)
				|| SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
			signInTimeNew = DateHelper.parseDateTime(addRequest.getSignInTimeNewStr());
			signOutTimeNew = DateHelper.parseDateTime(addRequest.getSignOutTimeNewStr());
			if(signInTimeNew == null || signOutTimeNew == null) {
				result.toFail("签到、签退时间不能为空！");
				return result;
			}
			if(!signOutTimeNew.after(signInTimeNew)) {
				result.toFail("签退时间需要大于签到时间！");
				return result;
			}
			if(signOutTimeNew.after(currentTime)) {
				result.toFail("签退时间不能大于当前时间！");
				return result;
			}
			if(DateHelper.betweenHours(signInTimeNew, signOutTimeNew) > maxSignRangeHours) {
				result.toFail("签到时长不能大于"+maxSignRangeHours+"小时！");
				return result;
			}
			signDateNew = DateHelper.parseDate(DateHelper.formatDate(signInTimeNew));
			
			//修改-签到时间范围限制
			if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)) {
				Date signInStart = DateHelper.addHours(signData.getSignInTime(), -DateHelper.ONE_DAY_HOURS);
				Date signInEnd = DateHelper.addHours(signData.getSignInTime(), DateHelper.ONE_DAY_HOURS);
				//签到时间不能大于当前时间
				if(signInEnd.after(currentTime)) {
					signInEnd = currentTime;
				}
				if(signInTimeNew.before(signInStart)|| signInTimeNew.after(signInEnd)) {
					result.toFail("签到时间修改只能是【"+DateHelper.formatDateTime(signInStart) +"~" +DateHelper.formatDateTime(signInEnd)+"】！");
					return result;
				}
			}
			
			//查询history签到时长是否重合
			UserSignRecordFlowQuery checkQuery = new UserSignRecordFlowQuery();
			checkQuery.setUserCode(addRequest.getUserCode());
			checkQuery.setSignInTime(signInTimeNew);
			checkQuery.setSignOutTime(signOutTimeNew);
			checkQuery.setRefRecordId(addRequest.getRecordId());
			boolean checkTimeFlag = userSignRecordHistoryService.checkSignTimeForFlow(checkQuery);
			if(!checkTimeFlag) {
				result.toFail("提交失败，修改后的在岗时间段与已有时间段重叠，不能同时出勤在两段时间！");
				return result;
			}
		}
		if(SignFlowTypeEnum.ADD.getCode().equals(flowType)) {
			UserSignRequest signInRequest = new UserSignRequest();
			signInRequest.setJobCode(addRequest.getJobCode());
			signInRequest.setUserCode(addRequest.getUserCode());
			signInRequest.setPositionCode(addRequest.getPositionCode());
			signInRequest.setOperateUserCode(addRequest.getOperateUserCode());
			signInRequest.setOperateUserName(addRequest.getOperateUserName());
			Result<UserSignRecord> newSignResult = this.userSignRecordService.checkAndCreateSignInDataForFlowAdd(signInRequest);
			if(newSignResult != null && newSignResult.getData() != null) {
				signData = toUserSignRecordFlow(newSignResult.getData());
				signData.setSignInTime(signInTimeNew);
				signData.setSignOutTime(signOutTimeNew);
				signData.setSignDate(DateHelper.parseDate(DateHelper.formatDate(signInTimeNew)));
			}else {
				String msg = "签到失败！";
				if(newSignResult != null) {
					msg = newSignResult.getMessage(); 
				}
				result.toFail(msg);
				return result;
			}
			log.info("checkParamForAddSignFlow flowType:{},addRequest.getUserCode():",flowType, addRequest.getUserCode());
			signData.setIdCard(BusinessUtil.encryptIdCardDoubleStar(addRequest.getUserCode()));
		}else if(SignFlowTypeEnum.MODIFY.getCode().equals(flowType)){
			signData.setSignInTimeNew(signInTimeNew);
			signData.setSignOutTimeNew(signOutTimeNew);
			signData.setSignDateNew(signDateNew);
		}
		
		if(!userSignRecordFlowService.checkSignInTime(signData.getSignInTime(),signInTimeNew)) {
			result.toFail("提交失败，修改时间为上一个计提周期，无法修改！");
			return result;
		}
		
		//存在未审批完成的流程，不允许发起新流程
		UserSignRecordFlowQuery checkFlowQuery = new UserSignRecordFlowQuery();
		checkFlowQuery.setUserCode(signData.getUserCode());
		checkFlowQuery.setFlowStatusList(unCompletedStatus);
		
		boolean checkFlowFlag = userSignRecordFlowService.checkUnCompletedFlow(checkFlowQuery);
		if(!checkFlowFlag) {
			result.toFail("该ERP/身份证号存在未审批的签到流程，审批完成后再提交！");
			return result;
		}
		signData.setFlowStatus(SignFlowStatusEnum.PENDING_APPROVAL.getCode());
		signData.setFlowType(flowType);
		signData.setFlowCreateTime(new Date());
		signData.setFlowCreateUser(addRequest.getOperateUserCode());
		loadGridData(signData,new HashMap<>());
		addRequest.setUserSignRecordFlow(signData);
		return result;
	}
	private UserSignRecordFlow toUserSignRecordFlow(UserSignRecord signData) {
		if(signData == null) {
			return null;
		}
		UserSignRecordFlow flowData = new UserSignRecordFlow();
		BeanUtils.copyProperties(signData, flowData);
		flowData.setId(null);
		flowData.setRefRecordId(signData.getId());
		return flowData;
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
			Set<Long> hiddenHistoryIds = new HashSet<>();
			if(hasFlowData) {
				totalCount = flowCount;
				List<UserSignRecordFlow> flowList = userSignRecordFlowService.queryFlowList(query);
				if(!CollectionUtils.isEmpty(flowList)) {
					for(UserSignRecordFlow flowData: flowList) {
						dataList.add(flowData);
						if(flowData.getRefRecordId() != null
								&& hiddenHistoryStatus.contains(flowData.getFlowStatus())) {
							hiddenHistoryIds.add(flowData.getRefRecordId());
						}
					}
				}
			}
			if(hasHistoryData) {
				if(historyCount > totalCount) {
					totalCount = historyCount;
				}
				List<UserSignRecordFlow> historyList = userSignRecordHistoryService.querySignList(query);
				if(!CollectionUtils.isEmpty(historyList)) {
					for(UserSignRecordFlow flowData: historyList) {
						//存在流程的数据，只展示流程数据
						if(hiddenHistoryIds.contains(flowData.getRefRecordId())) {
							continue;
						}
						dataList.add(flowData);
					}					
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
			Map<String,UserSignRecordFlow> gridPositionCacheMap = new HashMap<>();
			for(UserSignRecordFlow flowData: dataList) {
				loadGridData(flowData,gridPositionCacheMap);
				fillOtherData(flowData);
			}
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
	 * 签到记录-加载网格相关数据
	 * @param signData
	 */
	private void fillOtherData(UserSignRecordFlow signData) {
		if(signData == null) {
			return;
		}
		signData.setFlowTypeName(SignFlowTypeEnum.getNameByCode(signData.getFlowType()));
		signData.setFlowStatusName(SignFlowStatusEnum.getNameByCode(signData.getFlowStatus()));
		//身份证隐藏
		signData.setUserCodeHidden(BusinessUtil.encryptIdCardDoubleStar(signData.getUserCode()));
		signData.setUserNameHidden(BusinessUtil.encryptIdCard(signData.getUserName()));
	}
			
	/**
	 * 签到记录-加载网格相关数据
	 * @param signData
	 */
	private void loadGridData(UserSignRecordFlow signData,Map<String,UserSignRecordFlow> gridPositionCacheMap) {
		if(signData == null || signData.getRefGridKey() == null) {
			return;
		}
		//先从缓存读取
		if(gridPositionCacheMap.containsKey(signData.getRefGridKey())) {
			loadGridDataByCacheData(signData,gridPositionCacheMap.get(signData.getRefGridKey()));
			return;
		}
		if(signData.getGridCode() == null || signData.getGridNo() == null) {
			com.jdl.basic.api.domain.workStation.WorkStationGridQuery  workStationGridCheckQuery = new com.jdl.basic.api.domain.workStation.WorkStationGridQuery ();
			workStationGridCheckQuery.setBusinessKey(signData.getRefGridKey());
			com.jdl.basic.common.utils.Result<WorkStationGrid> gridData = workStationGridManager.queryByGridKey(workStationGridCheckQuery);
			if(gridData != null && gridData.getData() != null) {
				signData.setGridCode(gridData.getData().getGridCode());
				signData.setGridName(gridData.getData().getGridName());
				signData.setGridNo(gridData.getData().getGridNo());
				signData.setAreaCode(gridData.getData().getAreaCode());
				signData.setAreaName(gridData.getData().getAreaName());
				signData.setWorkCode(gridData.getData().getWorkCode());
				signData.setWorkName(gridData.getData().getWorkName());
				signData.setFloor(gridData.getData().getFloor());
			}
		}
		if(signData.getPositionCode() == null) {
			//查询组员信息
			com.jdl.basic.common.utils.Result<PositionData>  positionResult= this.positionManager.queryPositionByGridKey(signData.getRefGridKey());
			if(positionResult != null
					&& positionResult.getData() != null) {
				signData.setPositionCode(positionResult.getData().getPositionCode());
			}
		}
		gridPositionCacheMap.put(signData.getRefGridKey(), signData);
	}
	private void loadGridDataByCacheData(UserSignRecordFlow signData,UserSignRecordFlow cacheData) {
		signData.setGridCode(cacheData.getGridCode());
		signData.setGridName(cacheData.getGridName());
		signData.setGridNo(cacheData.getGridNo());
		signData.setAreaCode(cacheData.getAreaCode());
		signData.setAreaName(cacheData.getAreaName());
		signData.setWorkCode(cacheData.getWorkCode());
		signData.setWorkName(cacheData.getWorkName());
		signData.setFloor(cacheData.getFloor());
		signData.setPositionCode(cacheData.getPositionCode());
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
		String userCode = query.getUserCode();
		if (StringUtils.isNotBlank(query.getUserCode()) && query.getUserCode().contains("***")) {
			query.setUserCode(null);
			query.setIdCard(userCode);
		}
		return result;
	 }
}
