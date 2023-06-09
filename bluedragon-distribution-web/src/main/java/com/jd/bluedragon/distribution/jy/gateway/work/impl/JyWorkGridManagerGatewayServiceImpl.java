package com.jd.bluedragon.distribution.jy.gateway.work.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCountData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerPageData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import com.jd.bluedragon.common.dto.work.ScanTaskPositionRequest;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseQuery;
import com.jd.bluedragon.distribution.jy.gateway.work.JyWorkGridManagerGatewayService;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
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
public class JyWorkGridManagerGatewayServiceImpl implements JyWorkGridManagerGatewayService {

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseService")
	private JyWorkGridManagerCaseService jyWorkGridManagerCaseService;
	
	@Autowired
	@Qualifier("positionManager")
	private PositionManager positionManager;
	

	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.queryDataList",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<JyWorkGridManagerPageData> queryDataList(JyWorkGridManagerQueryRequest query) {
		JdCResponse<JyWorkGridManagerPageData> result = new JdCResponse<JyWorkGridManagerPageData>();
		result.toSucceed("查询成功！");
		JyWorkGridManagerPageData pageData = new JyWorkGridManagerPageData();
		result.setData(pageData);
		Map<Integer,JyWorkGridManagerCountData> statusCount = new HashMap<>();
		List<JyWorkGridManagerData> dataList = new ArrayList<>();
		pageData.setDataList(dataList);
		pageData.setStatusCount(statusCount);
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
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("保存成功！");
		return result;
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.submitData",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("提交成功！");
		
		return result;
	}
	@JProfiler(jKey = "dmsWeb.server.jyWorkGridManagerGatewayService.scanTaskPosition",
	jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdCResponse<Boolean> scanTaskPosition(ScanTaskPositionRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.setData(Boolean.FALSE);
		result.toSucceed("扫描成功！");
		if(StringUtils.isBlank(request.getScanPositionCode())) {
			result.toFail("扫描的岗位码不能为空！");
			return result;
		}
		if(StringUtils.isBlank(request.getTaskRefGridKey())) {
			result.toFail("任务岗位码不能为空！");
			return result;
		}
		Result<PositionDetailRecord> positionRecord = positionManager.queryOneByPositionCode(request.getScanPositionCode());
		if(positionRecord == null
				|| positionRecord.getData() == null) {
			result.toFail("扫描的岗位码无效！");
			return result;
		}
		if(!request.getTaskRefGridKey().equals(positionRecord.getData().getRefGridKey())) {
			result.toFail("验证失败，请扫描巡检任务对应的岗位码！");
			return result;
		}else {
			result.setData(Boolean.TRUE);
		}
		return result;
	}

}
