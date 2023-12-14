package com.jd.bluedragon.distribution.jy.gateway.work.impl;


import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.work.WorkGridCandidate;
import com.jdl.basic.api.service.work.WorkGridCandidateJsfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseQuery;
import com.jd.bluedragon.distribution.jy.gateway.work.JyWorkGridManagerGatewayService;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ldop.utils.CollectionUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;

/**
 * 任务线上化网关服务实现
 *
 * @author wuyoude
 * @date 2023年05月30日 14:30:43
 *
 */
@Service("jyWorkGridManagerGatewayService")
@Slf4j
public class JyWorkGridManagerGatewayServiceImpl implements JyWorkGridManagerGatewayService {
	private static final String refresh_second_config_key = "refresh.second.config.key";
	private static final String query_data_list_mock_key ="query_data_list_mock_key";
	private static final String query_data_bybizId_mock_key ="query_data_bybizId_mock_key";
	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;

	@Autowired
	@Qualifier("jyWorkGridManagerCaseService")
	private JyWorkGridManagerCaseService jyWorkGridManagerCaseService;

	@Autowired
	@Qualifier("jyWorkGridManagerBusinessService")
	private JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;
	

	@Autowired
	private PositionManager positionManager;

	@Autowired
	SysConfigService sysConfigService;

	@Autowired
	WorkGridCandidateJsfService workGridCandidateJsfService;
	@Autowired
	BaseMajorManager baseMajorManager;


	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.queryDataList",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<JyWorkGridManagerPageData> queryDataList(JyWorkGridManagerQueryRequest query) {
		JdCResponse<JyWorkGridManagerPageData> result = new JdCResponse<JyWorkGridManagerPageData>();
		result.toSucceed("查询成功！");
		JyWorkGridManagerPageData pageData = new JyWorkGridManagerPageData();
		//列表定时刷新时间
		Integer refreshSecond = getRefreshSecond();
		if(refreshSecond != null){
			pageData.setRefreshSecond(refreshSecond);
		}
		result.setData(pageData);
		if(StringUtils.isNotBlank(query.getPositionCode())) {
			Result<PositionDetailRecord> positionRecord = positionManager.queryOneByPositionCode(query.getPositionCode());
			if(positionRecord == null
					|| positionRecord.getData() == null) {
				result.toFail("扫描的网格码无效！");
				return result;
			}
			query.setTaskRefGridKey(positionRecord.getData().getRefWorkGridKey());
		}
		Map<Integer,JyWorkGridManagerCountData> statusCount = new HashMap<>();
		Map<String,JyWorkGridManagerCountData> statusNum = new HashMap<>();
		List<JyWorkGridManagerData> dataList = new ArrayList<>();
		pageData.setDataList(dataList);
		pageData.setStatusCount(statusCount);
		pageData.setStatusNum(statusNum);
		//todo 待下线，Integer为key的map 序列化json的时候有问题
		for(WorkTaskStatusEnum statusEnum : WorkTaskStatusEnum.values()) {
			JyWorkGridManagerCountData countData = new JyWorkGridManagerCountData();
			countData.setDataNum(0);
			countData.setStatus(statusEnum.getCode());
			countData.setStatusName(statusEnum.getName());
			statusCount.put(statusEnum.getCode(), countData);
		}
		for(WorkTaskQueryStatusEnum statusEnum : WorkTaskQueryStatusEnum.values()) {
			JyWorkGridManagerCountData countData = new JyWorkGridManagerCountData();
			countData.setDataNum(0);
			countData.setStatus(statusEnum.getCode());
			countData.setStatusName(statusEnum.getName());
			statusNum.put(String.valueOf(statusEnum.getCode()), countData);
		}

		List<JyWorkGridManagerCountData> countList = jyBizTaskWorkGridManagerService.queryDataCountListForPda(query);
		if(CollectionUtils.isEmpty(countList)) {
			return result;
		}
		//设置数量
		for(JyWorkGridManagerCountData countData: countList){
			if(countData.getDataNum() != null
					&& statusCount.containsKey(countData.getStatus())) {
				statusCount.get(countData.getStatus()).setDataNum(countData.getDataNum());
			}
			if(countData.getDataNum() != null
					&& statusNum.containsKey(String.valueOf(countData.getStatus()))) {
				statusNum.get(String.valueOf(countData.getStatus())).setDataNum(countData.getDataNum());
			}
		}

		//判断相应状态的数量是否大于0
		if(query.getStatus() != null
				&& statusNum.containsKey(String.valueOf(query.getStatus()))
				&& statusNum.get(String.valueOf(query.getStatus())).getDataNum() > 0) {
			List<JyWorkGridManagerData> queryList = jyBizTaskWorkGridManagerService.queryDataListForPda(query);
			if(!CollectionUtils.isEmpty(queryList)) {
				pageData.setDataList(queryList);
			}
		}
		return result;
	}

	private Integer getRefreshSecond(){
		SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(refresh_second_config_key);
		if(sysConfig == null || !org.apache.commons.lang3.StringUtils.isNumeric(sysConfig.getConfigContent())){
			return null;
		}
		return Integer.parseInt(sysConfig.getConfigContent());
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.queryDataByBizId",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<JyWorkGridManagerData> queryDataByBizId(String bizId) {
		JdCResponse<JyWorkGridManagerData> result = new JdCResponse<JyWorkGridManagerData>();
		result.toSucceed("查询成功！");
		JyWorkGridManagerData taskData  = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
		if(taskData == null) {
			result.toFail("任务数据不存在！");
			return result;
		}
		JyWorkGridManagerCaseQuery taskCaseQuery = new JyWorkGridManagerCaseQuery();
		taskCaseQuery.setBizId(taskData.getBizId());
		taskCaseQuery.setSiteCode(taskData.getSiteCode());
		taskCaseQuery.setTaskType(taskData.getTaskType());
		taskCaseQuery.setTaskCode(taskData.getTaskCode());
		List<JyWorkGridManagerCaseData> caseList = jyWorkGridManagerCaseService.loadCaseListForTaskData(taskCaseQuery);
		taskData.setCaseList(caseList);
		result.setData(taskData);
		return result;
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.saveData",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<Boolean> saveData(JyWorkGridManagerTaskEditRequest request) {
		return jyWorkGridManagerBusinessService.saveData(request);
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.submitData",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request) {
		return jyWorkGridManagerBusinessService.submitData(request);
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.scanTaskPosition",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<Boolean> scanTaskPosition(ScanTaskPositionRequest request) {
		return jyWorkGridManagerBusinessService.scanTaskPosition(request);
	}

	@Override
	public JdCResponse<Boolean> transferCandidate(JyWorkGridManagerTransferData request) {
		return jyBizTaskWorkGridManagerService.transferCandidate(request);
	}

	@Override
	@Deprecated
	public JdCResponse<List<String>> queryCandidateList(JyWorkGridManagerQueryRequest query) {
		JdCResponse<List<String>> result = new JdCResponse<List<String>>();
		if (StringUtils.isEmpty(query.getPositionCode())){
			result.toFail("扫描的网格码为空！");
			return result;
		}
		Result<PositionDetailRecord> positionRecord = positionManager.queryOneByPositionCode(query.getPositionCode());
		if(positionRecord == null
				|| positionRecord.getData() == null) {
			result.toFail("扫描的网格码无效！");
			return result;
		}

		List<WorkGridCandidate> workGridCandidates = workGridCandidateJsfService.queryCandidateList(positionRecord.getData().getSiteCode());
		List<String> erpList = Lists.newArrayList();
		if (!CollectionUtils.isEmpty(workGridCandidates)){
			workGridCandidates.stream().forEach(item->{
				erpList.add(item.getErp());
			});
		}
		result.toSucceed();
		result.setData(erpList);
		return result;
	}

	/**
	 * 根据
	 * @param query
	 * @return
	 */
	@Override
	public JdCResponse<List<WorkGridCandidateData>> queryCandidates(JyWorkGridManagerQueryRequest query) {
		JdCResponse<List<WorkGridCandidateData>> result = new JdCResponse<List<WorkGridCandidateData>>();
		if (StringUtils.isEmpty(query.getPositionCode())){
			result.toFail("扫描的网格码为空！");
			return result;
		}
		Result<PositionDetailRecord> positionRecord = positionManager.queryOneByPositionCode(query.getPositionCode());
		if(positionRecord == null
				|| positionRecord.getData() == null) {
			result.toFail("扫描的网格码无效！");
			return result;
		}
		if(StringUtils.isBlank(query.getBizId())){
			result.toFail("设备编码bizId不能为空！");
			return result;
		}
		JyWorkGridManagerData jyWorkGridManagerData = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(query.getBizId());
		if(jyWorkGridManagerData == null){
			result.toFail("设备编码bizId不能为空！");
			return result;
		}
		
		List<WorkGridCandidate> workGridCandidates = workGridCandidateJsfService.queryCandidateList(positionRecord.getData().getSiteCode());
		List<WorkGridCandidateData> datas = Lists.newArrayList();
		if (CollectionUtils.isEmpty(workGridCandidates)){
			result.toSucceed();
			result.setData(datas);
			return result;
		}
		List<JyUserDto> jyUserDtos = workGridCandidates.stream().map(c ->{
			JyUserDto jyUserDto = new JyUserDto();
			jyUserDto.setUserErp(c.getErp());
			return jyUserDto;
		}).collect(Collectors.toList());
		//根据三定排班过滤
		jyUserDtos = jyWorkGridManagerBusinessService.filterJyUserDtoInSchedule(jyWorkGridManagerData.getTaskCode(), jyWorkGridManagerData.getProcessBeginTime(), 
				jyWorkGridManagerData.getPreFinishTime(), jyUserDtos);
		Map<String , String> erpMap = new HashMap<>();
		if(!CollectionUtils.isEmpty(jyUserDtos)){
			erpMap = jyUserDtos.stream().collect(Collectors.toMap(JyUserDto::getUserErp , JyUserDto::getUserErp));
		}
		for (WorkGridCandidate candidate : workGridCandidates){
			WorkGridCandidateData data = new WorkGridCandidateData();
			data.setErp(candidate.getErp());
			if(erpMap.get(data.getErp()) != null){
				data.setStatus(WorkGridCandidateStatusEnum.NORMAL.getCode());
			}else {
				data.setStatus(WorkGridCandidateStatusEnum.ABNORMAL.getCode());
				log.info("任务转派人员根据三排排班过滤，该erp在任务期间未排班，erp:{},bizId:{}", data.getErp(), query.getBizId());
			}
			BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(data.getErp());
			//erp信息为空 或
			if(baseStaffSiteOrgDto != null){
				data.setName(baseStaffSiteOrgDto.getStaffName());
				//未在职
				if(baseStaffSiteOrgDto.getIsResign() == null || baseStaffSiteOrgDto.getIsResign() != 1){
					data.setStatus(WorkGridCandidateStatusEnum.ABNORMAL.getCode());
					log.info("任务转派人员根据三排排班过滤，该erp未在职，erp:{},bizId:{}", data.getErp(), query.getBizId());
				}
			}else {
				data.setName("");
			} 
			datas.add(data);
		}
		//根据状态排序，正常状态的排在前面
		datas = datas.stream().sorted(Comparator.comparing(WorkGridCandidateData::getStatus)).collect(Collectors.toList());
		result.toSucceed();
		result.setData(datas);
		return result;
	}

	@Override
	public JdCResponse<Boolean> updateTask4Uat(Map<String,Object> data) {
		JdCResponse<Boolean> response = new JdCResponse<>();
		if(data.get("id") == null){
			response.toFail("id不能为空"); 
			return response;
		}
		if(data.get("handlerErp") == null && data.get("yn") == null && data.get("taskBizType") == null){
			response.toFail("handlerErp,yn,taskBizType不能同时为空");
			return response;
		}
		int count = jyBizTaskWorkGridManagerService.updateTask4Uat(data);
		response.setData(count > 0);
		return response;
	}

}
