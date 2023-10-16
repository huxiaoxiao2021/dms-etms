package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.jy.dto.work.*;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.service.work.WorkGridManagerTaskJsfService;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCountData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.distribution.jy.dao.work.JyBizTaskWorkGridManagerDao;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.jsf.gd.util.StringUtils;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.common.utils.Result;
import org.terracotta.statistics.jsr166e.ThreadLocalRandom;

/**
 * @ClassName: JyBizTaskWorkGridManagerServiceImpl
 * @Description: 巡检任务表--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyBizTaskWorkGridManagerService")
public class JyBizTaskWorkGridManagerServiceImpl implements JyBizTaskWorkGridManagerService {

	private static final Logger logger = LoggerFactory.getLogger(JyBizTaskWorkGridManagerServiceImpl.class);
	//todo sysconfig 配置
	private List<String> OFFICE_AREA_CODE_LIST = Arrays.asList("FJBGQ");

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerDao")
	private JyBizTaskWorkGridManagerDao jyBizTaskWorkGridManagerDao;
	
	@Autowired
	@Qualifier("workGridManagerTaskJsfManager")
	private WorkGridManagerTaskJsfManager workGridManagerTaskJsfManager;
	@Autowired
	private PositionManager positionManager;
	
	

	@Override
	public JyWorkGridManagerData queryTaskDataByBizId(String bizId) {
		JyBizTaskWorkGridManager jyTaskData = jyBizTaskWorkGridManagerDao.queryByBizId(bizId);
		if(jyTaskData == null) {
			return null;
		}
		JyWorkGridManagerData taskData  = toJyWorkGridManagerData(jyTaskData);
		Result<WorkGridManagerTask> taskInfoResult = workGridManagerTaskJsfManager.queryByTaskCode(jyTaskData.getTaskCode());
		if(taskInfoResult != null 
				&& taskInfoResult.getData() != null) {
			//字段生成任务时，冗余到任务数据中
		}
		return taskData;
	}
	private JyWorkGridManagerData toJyWorkGridManagerData(JyBizTaskWorkGridManager jyTaskData) {
		JyWorkGridManagerData taskData  = new JyWorkGridManagerData();
		BeanUtils.copyProperties(jyTaskData, taskData);
		return taskData;
	}
	@Override
	public List<JyWorkGridManagerCountData> queryDataCountListForPda(JyWorkGridManagerQueryRequest query) {
		List<JyWorkGridManagerCountData> dataList = new ArrayList<>();
		if(!checkAndInitQuery(query)) {
			return dataList;
		}
		List<JyBizTaskWorkGridManagerCount> jyDataList  = jyBizTaskWorkGridManagerDao.queryDataCountListForPda(query);
		if(CollectionUtils.isEmpty(jyDataList)) {
			return dataList;
		}
		for(JyBizTaskWorkGridManagerCount jyData: jyDataList) {
			dataList.add(toJyWorkGridManagerCountData(jyData));
		}
		return dataList;
	}
	private JyWorkGridManagerCountData toJyWorkGridManagerCountData(JyBizTaskWorkGridManagerCount jyData) {
		JyWorkGridManagerCountData taskCountData  = new JyWorkGridManagerCountData();
		BeanUtils.copyProperties(jyData, taskCountData);
		return taskCountData;
	}
	@Override
	public List<JyWorkGridManagerData> queryDataListForPda(JyWorkGridManagerQueryRequest query) {
		List<JyWorkGridManagerData> dataList = new ArrayList<>();
		if(!checkAndInitQuery(query)) {
			return dataList;
		}
		List<JyBizTaskWorkGridManager> jyDataList  = jyBizTaskWorkGridManagerDao.queryDataListForPda(query);
		if(CollectionUtils.isEmpty(jyDataList)) {
			return dataList;
		}
		for(JyBizTaskWorkGridManager jyTaskData: jyDataList) {
			dataList.add(toJyWorkGridManagerData(jyTaskData));
		}
		return dataList;
	}
	private boolean checkAndInitQuery(JyWorkGridManagerQueryRequest query) {
    	if(query == null
    			|| StringUtils.isBlank(query.getOperateUserCode())) {
    		return false;
    	}
		if(query.getPageSize() == null || query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		};
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() != null && query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		};
		query.setProcessBeginTime(new Date());
		return true;
	}
	@Override
	public int finishTask(JyBizTaskWorkGridManager updateTaskData) {
		return jyBizTaskWorkGridManagerDao.finishTask(updateTaskData);
	}
	@Override
	public int addTask(JyBizTaskWorkGridManager jyTask) {
		return jyBizTaskWorkGridManagerDao.addTask(jyTask);
	}
	@Override
	public Integer queryDataCountForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return jyBizTaskWorkGridManagerDao.queryDataCountForDistribution(query);
	}
	@Override
	public List<JyBizTaskWorkGridManager> queryDataListForDistribution(JyBizTaskWorkGridManagerQuery query) {
		return jyBizTaskWorkGridManagerDao.queryDataListForDistribution(query);
	}
	@Override
	public int distributionTask(JyBizTaskWorkGridManagerBatchUpdate distributionData) {
		if(distributionData == null
				|| distributionData.getData() == null
				|| CollectionUtils.isEmpty(distributionData.getBizIdList())) {
			return 0;
		}
		return jyBizTaskWorkGridManagerDao.distributionTask(distributionData);
	}
	@Override
	public int autoCloseTask(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		if(closeData == null
				|| closeData.getData() == null
				|| CollectionUtils.isEmpty(closeData.getBizIdList())) {
			return 0;
		}		
		return jyBizTaskWorkGridManagerDao.autoCloseTask(closeData);
	}
	@Override
	public int closeTaskForEndBatch(JyBizTaskWorkGridManagerBatchUpdate closeData) {
		if(closeData == null
				|| closeData.getData() == null) {
			return 0;
		}		
		return jyBizTaskWorkGridManagerDao.closeTaskForEndBatch(closeData);
	}
	@Override
	public int batchAddTask(List<JyBizTaskWorkGridManager> taskList) {
		return jyBizTaskWorkGridManagerDao.batchAddTask(taskList);
	}
	@Override
	public int autoCancelTaskForGridDelete(JyBizTaskWorkGridManagerBatchUpdate cancelData) {
		if(cancelData == null
				|| cancelData.getData() == null
				|| StringUtil.isBlank(cancelData.getData().getTaskRefGridKey())) {
			return 0;
		}
		return jyBizTaskWorkGridManagerDao.autoCancelTaskForGridDelete(cancelData);
	}

	/**
	 * 生成管理巡视任务
	 * @param erp
	 * @param positionCode
	 */
	@Override
	public void generateManageInspectionTask(String erp, String positionCode){
		Result<PositionDetailRecord> recordResult = positionManager.queryOneByPositionCode(positionCode);
		if(recordResult == null || recordResult.getData() == null){
			logger.info("生成管理巡视任务，未查到岗位信息，erp:{},positionCode:{}", erp, positionCode);
			return;
		}
		PositionDetailRecord detailRecord = recordResult.getData();
		String areaCode = detailRecord.getAreaCode();
		if(org.apache.commons.lang3.StringUtils.isBlank(areaCode)){
			logger.info("生成管理巡视任务，未查到岗位对应的作业区信息，erp:{},positionCode:{}", erp, positionCode);
			return;
		}
		//todo 过滤 【一线机构管理岗】、【精益改善岗】、【中控岗】
		
		if(!OFFICE_AREA_CODE_LIST.contains(areaCode)){
			logger.info("生成管理巡视任务，登录扫描的非办公区岗位码，erp:{},positionCode:{},areaCode:{}", erp, positionCode,
					areaCode);
			return;
		}
		//管理任务
		Result<List<WorkGridManagerTask>> taskResult = workGridManagerTaskJsfManager.queryByBizType(WorkGridManagerTaskBizType.MANAGER_PATROL.getCode());
		if(taskResult == null || CollectionUtils.isEmpty(taskResult.getData())){
			logger.info("生成管理巡视任务，根据类型未查询管理任务定义，erp:{},positionCode:{},areaCode:{}");
			return;
		}
		List<String> taskCodeList = taskResult.getData().stream().map(WorkGridManagerTask::getTaskCode).collect(Collectors.toList());
		//检查是否已生成本erp的今天的管理任务
		Integer taskCount = jyBizTaskWorkGridManagerDao.selectHandlerTodayTaskCountByTaskBizType(detailRecord.getSiteCode(),
				DateHelper.getZeroFromDay(new Date(), 0), erp, taskCodeList);
		if(taskCount > 0){
			logger.info("生成管理巡视任务，今天已生成管理任务，不再重复生成");
			return;
		}
		
		
		
	}

	private JyBizTaskWorkGridManager initJyBizTaskWorkGridManager(BaseSiteInfoDto siteInfo, String taskConfigCode, String taskBatchCode,
																  WorkGridManagerTask taskInfo, String handlerPositionCode,String handlerPositionName
																  ,WorkGrid grid, Date curDate) {
		JyBizTaskWorkGridManager jyTask = new JyBizTaskWorkGridManager();
		jyTask.setBizId(UUID.randomUUID().toString());
		//设置任务配置信息
		jyTask.setTaskConfigCode(taskConfigCode);
		jyTask.setTaskBatchCode(taskBatchCode);
		jyTask.setHandlerUserPositionCode(handlerPositionCode);
		jyTask.setHandlerUserPositionName(handlerPositionName);
		jyTask.setCreateTime(curDate);
		jyTask.setStatus(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());

		//设置网格信息
		jyTask.setTaskRefGridKey(grid.getBusinessKey());
		jyTask.setAreaCode(grid.getAreaCode());
		jyTask.setAreaName(grid.getAreaName());
		jyTask.setGridName(grid.getGridName());
		jyTask.setSiteCode(grid.getSiteCode());
		//设置省区相关字段
		jyTask.setSiteName(siteInfo.getSiteName());
		jyTask.setAreaHubCode(StringHelper.getStringValue(siteInfo.getAreaCode()));
		jyTask.setAreaHubName(StringHelper.getStringValue(siteInfo.getAreaName()));
		jyTask.setProvinceAgencyCode(StringHelper.getStringValue(siteInfo.getProvinceAgencyCode()));
		jyTask.setProvinceAgencyName(StringHelper.getStringValue(siteInfo.getProvinceAgencyName()));
		//设置任务信息
		jyTask.setTaskType(taskInfo.getTaskType());
		jyTask.setNeedScanGrid(taskInfo.getNeedScanGrid());
		jyTask.setTaskCode(taskInfo.getTaskCode());
		jyTask.setTaskName(taskInfo.getTaskName());
		jyTask.setTaskDescription(taskInfo.getTaskDescription());
		jyTask.setOrderNum(ThreadLocalRandom.current().nextInt(1000));
		return jyTask;
	}
}
