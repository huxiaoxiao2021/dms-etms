package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.jy.dto.work.*;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkGridQuery;
import com.jdl.basic.common.utils.DateUtil;
import org.apache.commons.lang3.ArrayUtils;
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
import com.jdl.basic.api.enums.WorkGridManagerTaskBizType;

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
	//管理巡视任务 sysconfig 配置
	private static final String MANAGER_PATROL_SYS_CONF_KEY = "manager.patrol.task.grid.config";
	//一线管理岗，岗位码 【一线机构管理岗】、【精益改善岗】、【中控岗 sysconfig 配置
	private static final String FRONT_LINE_MANAGEMENT_POSITION_SYS_CONF_KEY = "front.line.management.position.config";
	
	
	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerDao")
	private JyBizTaskWorkGridManagerDao jyBizTaskWorkGridManagerDao;
	
	@Autowired
	@Qualifier("workGridManagerTaskJsfManager")
	private WorkGridManagerTaskJsfManager workGridManagerTaskJsfManager;
	@Autowired
	private PositionManager positionManager;
	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	SysConfigService sysConfigService;
	@Autowired
	private WorkGridManager workGridManager;
	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;


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
	public int batchInsertDistributionTask(List<JyBizTaskWorkGridManager> taskList) {
		return jyBizTaskWorkGridManagerDao.batchInsertDistributionTask(taskList);
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
	public void generateManageInspectionTask(String erp, String positionCode, String userName){
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
		if(!OFFICE_AREA_CODE_LIST.contains(areaCode)){
			logger.info("生成管理巡视任务，登录扫描的非办公区岗位码，erp:{},positionCode:{},areaCode:{}", erp, positionCode,
					areaCode);
			return;
		}
		SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(FRONT_LINE_MANAGEMENT_POSITION_SYS_CONF_KEY);
		if(sysConfig == null || org.apache.commons.lang3.StringUtils.isBlank(sysConfig.getConfigContent())){
			logger.info("生成管理巡视任务，未查到一线管理岗sysConfig配置信息，erp:{},positionCode:{}", erp, positionCode);
			return;
		}
		//一线管理岗 
		if(ArrayUtils.contains(sysConfig.getConfigContent().split(","), positionCode)){
			logger.info("生成管理巡视任务，一线管理岗不用生成管理巡视任务，erp:{},positionCode:{}", erp, positionCode);
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
			logger.info("生成管理巡视任务，今天已生成管理任务，不再重复生成, erp:{}, siteCode:{}", erp, detailRecord.getSiteCode());
			return;
		}
		Integer siteCode = detailRecord.getSiteCode();
		BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
		if(siteInfo == null) {
			logger.warn("生成管理巡视任务，场地【{}】在青龙基础资料不存在！erp:{}",siteCode, erp);
			return;
		}
		List<ManagePatrolAreaConfig> configs = null;
		sysConfig = sysConfigService.findConfigContentByConfigName(MANAGER_PATROL_SYS_CONF_KEY);
		if(sysConfig == null || org.apache.commons.lang3.StringUtils.isBlank(sysConfig.getConfigContent())){
			logger.warn("生成管理巡视任务,未配置任务对应作业区网格数量，erp:{}", erp);
			return;
		}
		configs = JsonHelper.jsonToList(sysConfig.getConfigContent(), ManagePatrolAreaConfig.class);
		if(CollectionUtils.isEmpty(configs)){
			logger.warn("生成管理巡视任务,配置的任务对应作业区网格数量反序列对象失败，erp:{}，content:{}", erp, sysConfig.getConfigContent());
			return;
		}
		Map<WorkGridManagerTask, List<WorkGrid>> taskToWorkGridList = getTaskToWorkGridList(configs, taskResult.getData(),
				erp, siteCode);
		if(taskToWorkGridList == null){
			return;
		}
		List<JyBizTaskWorkGridManager> bizTaskWorkGridManagers = getTaskList(taskToWorkGridList, siteInfo, 
				positionCode, positionCode, erp, userName);

		jyBizTaskWorkGridManagerService.batchInsertDistributionTask(bizTaskWorkGridManagers);
	}

	private List<JyBizTaskWorkGridManager> getTaskList(Map<WorkGridManagerTask, List<WorkGrid>> taskToWorkGridList,
													   BaseSiteInfoDto siteInfo, String handlerPositionCode, 
													   String handlerPositionName, String erp, String userName){
		List<JyBizTaskWorkGridManager> jyTaskInitList = new ArrayList<>();
		Date curDate = new Date();
		for(Map.Entry<WorkGridManagerTask, List<WorkGrid>> entry : taskToWorkGridList.entrySet()){
			WorkGridManagerTask taskInfo = entry.getKey();
			for(WorkGrid workGrid : entry.getValue()){
				jyTaskInitList.add(initJyBizTaskWorkGridManager(siteInfo, taskInfo, handlerPositionCode,
						handlerPositionName, workGrid,curDate, erp, userName));
			}
		}
		return jyTaskInitList;
	}

	/**
	 * 根据任务配置信息和任务信息返回任务对应的网格list
	 * @param tasks
	 * @return
	 */
	private Map<WorkGridManagerTask, List<WorkGrid>> getTaskToWorkGridList(List<ManagePatrolAreaConfig> configs,
																		   List<WorkGridManagerTask> tasks, String erp, 
																		   Integer siteCode){
		
		List<String> areaCodes = configs.stream().map(ManagePatrolAreaConfig::getAreaCode).collect(Collectors.toList());
		//查询网格场地
		int pageNum = 1;
		WorkGridQuery workGridQuery = new WorkGridQuery();
		workGridQuery.setSiteCode(siteCode);
		workGridQuery.setAreaCodeList(areaCodes);
		workGridQuery.setPageSize(100);
		workGridQuery.setPageNumber(pageNum);
		//场地下存在需要推送的网格
		List<WorkGrid> sumGridList = new ArrayList<>();
		List<WorkGrid> gridList = workGridManager.queryListForManagerSiteScan(workGridQuery);
		while(!CollectionUtils.isEmpty(gridList)) {
			sumGridList.addAll(gridList);
			pageNum ++;
			workGridQuery.setPageNumber(pageNum);
			gridList = workGridManager.queryListForManagerSiteScan(workGridQuery);
		}
		if(CollectionUtils.isEmpty(sumGridList)){
			logger.info("生成管理巡视任务,本场地无符合生成任务的网格，erp:{},siteCode:{}", erp, siteCode);
			return null;
		}
		
		Map<String, List<WorkGrid>> areaCodeToGridList = sumGridList.stream().collect(Collectors.groupingBy(WorkGrid::getAreaCode));
		Map<String, WorkGridManagerTask> taskCodeToTask = tasks.stream()
				.collect(Collectors.toMap(WorkGridManagerTask::getTaskCode, WorkGridManagerTask -> WorkGridManagerTask));
		Map<WorkGridManagerTask, List<WorkGrid>> map = new HashMap<>();
		for(ManagePatrolAreaConfig config : configs){
			WorkGridManagerTask task = taskCodeToTask.get(config.getTaskCode());
			if(task == null){
				logger.info("生成管理巡视任务,此任务已删除或无效，erp:{},taskCode:{}", erp, config.getTaskCode());
				continue;
			}
			//该作业区的网格
			List<WorkGrid> workGrids = areaCodeToGridList.get(config.getAreaCode());
			
			if(CollectionUtils.isEmpty(workGrids)){
				logger.info("生成管理巡视任务,本场地无此作业区网格，erp:{},siteCode:{}，areaCode:{}", erp, siteCode, config.getAreaCode());
				continue;
			}
			if(workGrids.size() <= config.getGridQuality()){
				map.put(task, workGrids);
				continue;
			}
			//随机乱序
			Collections.shuffle(workGrids);
			map.put(task, workGrids.subList(0, config.getGridQuality()));
		}
		return map;
	}
	
	private JyBizTaskWorkGridManager initJyBizTaskWorkGridManager(BaseSiteInfoDto siteInfo,
																  WorkGridManagerTask taskInfo,
																  String handlerPositionCode,
																  String handlerPositionName
																  ,WorkGrid grid, Date curDate,
																  String erp, String userName) {
		JyBizTaskWorkGridManager jyTask = new JyBizTaskWorkGridManager();
		jyTask.setBizId(UUID.randomUUID().toString());
		//设置任务配置信息
		//管理巡视任务登录触发不需要任务配置
		jyTask.setTaskConfigCode("");
		jyTask.setTaskBatchCode("");
		jyTask.setHandlerPositionCode(handlerPositionCode);
		jyTask.setHandlerUserPositionCode(handlerPositionCode);
		jyTask.setHandlerUserPositionName(handlerPositionName);
		jyTask.setCreateTime(curDate);
		//处理中
		jyTask.setStatus(WorkTaskStatusEnum.TODO.getCode());

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
		
		//设置任务配置信息
		jyTask.setProcessBeginTime(curDate);
		jyTask.setHandlerErp(erp);
		jyTask.setHandlerUserName(userName);
		jyTask.setPreFinishTime(DateUtil.getLastTimeOfDay(curDate));
		jyTask.setStatus(WorkTaskStatusEnum.TODO.getCode());
		jyTask.setTaskDate(curDate);
		return jyTask;
	}
}
