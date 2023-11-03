package com.jd.bluedragon.distribution.jy.gateway.work.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jdl.basic.api.domain.work.WorkGridCandidate;
import com.jdl.basic.api.service.work.WorkGridCandidateJsfService;
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
import com.jdl.basic.api.domain.position.PositionData;
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
public class JyWorkGridManagerGatewayServiceImpl implements JyWorkGridManagerGatewayService {
	private static final String refresh_second_config_key = "refresh.second.config.key";
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
		for(WorkTaskStatusEnum statusEnum : WorkTaskStatusEnum.values()) {
			JyWorkGridManagerCountData countData = new JyWorkGridManagerCountData();
			countData.setDataNum(0);
			countData.setStatus(statusEnum.getCode());
			countData.setStatusName(statusEnum.getName());
			statusCount.put(statusEnum.getCode(), countData);
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
		}
		//判断相应状态的数量是否大于0
		if(query.getStatus() != null
				&& statusCount.containsKey(query.getStatus())
				&& statusCount.get(query.getStatus()).getDataNum() > 0) {
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
		result.setData(erpList);
		return result;
	}

}
