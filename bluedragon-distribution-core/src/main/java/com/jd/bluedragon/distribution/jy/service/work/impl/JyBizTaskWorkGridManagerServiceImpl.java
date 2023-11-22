package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.jy.dto.work.*;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.user.JyUser;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
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

import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.distribution.jy.dao.work.JyBizTaskWorkGridManagerDao;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.jsf.gd.util.StringUtils;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.common.utils.Result;
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

	//飞检巡场任务 sysconfig 配置
	private static final String MANAGER_PATROL_SYS_CONF_KEY = "manager.patrol.task.grid.config";
	//飞检巡场任务 登录岗位码所在的作业区
	private static final String MANAGER_PATROL_AREA_CODE_SYS_CONF_KEY = "manager.patrol.login.area.code";



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
	private JyUserManager jyUserManager;
	@Autowired
	private JyWorkGridManagerBusinessService jyWorkGridManagerBusinessService;

	@Override
	public JyWorkGridManagerData queryTaskDataByBizId(String bizId) {
		JyBizTaskWorkGridManager jyTaskData = jyBizTaskWorkGridManagerDao.queryByBizId(bizId);
		if(jyTaskData == null) {
			return null;
		}
		return toJyWorkGridManagerData(jyTaskData);
	}
	private JyWorkGridManagerData toJyWorkGridManagerData(JyBizTaskWorkGridManager jyTaskData) {
		JyWorkGridManagerData taskData  = new JyWorkGridManagerData();
		BeanUtils.copyProperties(jyTaskData, taskData);
		//扩展信息不为空
		if(StringUtils.isNotBlank(jyTaskData.getExtendInfo())){
			//指标改善任务的扩展信息
			if(WorkTaskTypeEnum.IMPROVE.getCode().equals(jyTaskData.getTaskType())){
				BusinessQuotaInfoData data  = JsonHelper.fromJsonMs(jyTaskData.getExtendInfo(), BusinessQuotaInfoData.class);
				taskData.setBusinessQuotaInfoData(data);
			}
			//指标改善任务的扩展信息
			if(WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(jyTaskData.getTaskType())){
				ViolenceSortInfoData data  = JsonHelper.fromJsonMs(jyTaskData.getExtendInfo(), ViolenceSortInfoData.class);
				taskData.setViolenceSortInfoData(data);
			}
			
		}
		//飞检 和 非待处理状态的  不能转派
		WorkGridManagerTaskBizType bizTypeEnum = WorkGridManagerTaskBizType.getEnum(jyTaskData.getTaskBizType());
		if (WorkGridManagerTaskBizType.MANAGER_PATROL.equals(bizTypeEnum)
			|| !WorkTaskStatusEnum.TODO.getCode().equals(jyTaskData.getStatus())
			|| jyTaskData.getTransfered().intValue() == 1){
			taskData.setCanTransfer(false);
		}else {
			taskData.setCanTransfer(true);
		}
		if(bizTypeEnum != null){
			taskData.setBizTypeLabel(bizTypeEnum.getShortLable());
		}
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
		//添加转派统计逻辑 状态为7的数量加到待处理中
		additionalTransferCount(query, dataList);
		return dataList;
	}

	private void additionalTransferCount(JyWorkGridManagerQueryRequest query, List<JyWorkGridManagerCountData> dataList) {
		Integer transferCount = jyBizTaskWorkGridManagerDao.queryTransferCountListForPda(query);
		JyWorkGridManagerCountData transferCountData  = new JyWorkGridManagerCountData();
		transferCountData.setDataNum(transferCount);
		transferCountData.setStatus(WorkTaskQueryStatusEnum.TRANSFERED.getCode());
		transferCountData.setStatusName(WorkTaskQueryStatusEnum.TRANSFERED.getName());
		dataList.add(transferCountData);
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
	 * 生成飞检巡场任务
	 * @param erp
	 * @param positionCode 登录扫描的岗位码
	 * @param userName
	 * @param userSiteCode 用户绑定的青龙基础资料场地
	 */
	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.Worker.JyBizTaskWorkGridManagerService.generateManageInspectionTask",
			mState = {JProEnum.TP, JProEnum.FunctionError})
	public void generateManageInspectionTask(String erp, String positionCode, String userName, Integer userSiteCode){
		SysConfig loginAreaCodeConfig = sysConfigService.findConfigContentByConfigName(MANAGER_PATROL_AREA_CODE_SYS_CONF_KEY);
		if(loginAreaCodeConfig == null || org.apache.commons.lang3.StringUtils.isBlank(loginAreaCodeConfig.getConfigContent())){
			logger.warn("生成飞检巡场任务,未配置登录扫描岗位码所属的作业区，erp:{}", erp);
			return;
		}
		SysConfig managePatrolTaskToAreaConfig = sysConfigService.findConfigContentByConfigName(MANAGER_PATROL_SYS_CONF_KEY);
		if(managePatrolTaskToAreaConfig == null || org.apache.commons.lang3.StringUtils.isBlank(managePatrolTaskToAreaConfig.getConfigContent())){
			logger.warn("生成飞检巡场任务,未配置任务对应作业区网格数量，erp:{}", erp);
			return;
		}

		Result<PositionDetailRecord> recordResult = positionManager.queryOneByPositionCode(positionCode);
		if(recordResult == null || recordResult.getData() == null){
			logger.info("生成飞检巡场任务，未查到岗位信息，erp:{},positionCode:{}", erp, positionCode);
			return;
		}
		PositionDetailRecord detailRecord = recordResult.getData();
		String areaCode = detailRecord.getAreaCode();
		if(org.apache.commons.lang3.StringUtils.isBlank(areaCode)){
			logger.info("生成飞检巡场任务，未查到岗位对应的作业区信息，erp:{},positionCode:{}", erp, positionCode);
			return;
		}

		if(!ArrayUtils.contains(loginAreaCodeConfig.getConfigContent().split(","), areaCode)){
			logger.info("生成飞检巡场任务，扫描的岗位码所属作业区:{},非配置触发任务的作业区:{}，erp:{},positionCode:{}",
					areaCode, loginAreaCodeConfig.getConfigContent(), erp, positionCode);
			return;
		}

		Result<JyUser> jyUserResult = jyUserManager.queryUserInfo(erp);
		if(jyUserResult.getData() == null){
			logger.info("生成飞检巡场任务，未查到登录人的岗位信息，erp:{},岗位码positionCode:{}", erp, positionCode);
			return;
		}
		//中控岗 精益改善岗 机构负责人岗
		String userPositionName = jyUserResult.getData().getPositionName();
		String userPositionCode = jyUserResult.getData().getPositionCode();

		//管理任务
		Result<List<WorkGridManagerTask>> taskResult = workGridManagerTaskJsfManager.queryByBizType(WorkGridManagerTaskBizType.MANAGER_PATROL.getCode());
		if(taskResult == null || CollectionUtils.isEmpty(taskResult.getData())){
			logger.info("生成飞检巡场任务，根据类型未查询管理任务定义，erp:{},positionCode:{},areaCode:{}", erp, positionCode, areaCode);
			return;
		}
		List<String> taskCodeList = taskResult.getData().stream().map(WorkGridManagerTask::getTaskCode).collect(Collectors.toList());
		//检查是否已生成本erp的今天的管理任务
		Integer taskCount = jyBizTaskWorkGridManagerDao.selectHandlerTodayTaskCountByTaskBizType(detailRecord.getSiteCode(),
				DateHelper.getZeroFromDay(new Date(), 0), erp, taskCodeList);
		if(taskCount > 0){
			logger.info("生成飞检巡场任务，今天已生成管理任务，不再重复生成, erp:{}, siteCode:{}", erp, detailRecord.getSiteCode());
			return;
		}
		Integer siteCode = detailRecord.getSiteCode();
		BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
		if(siteInfo == null) {
			logger.warn("生成飞检巡场任务，场地【{}】在青龙基础资料不存在！erp:{}",siteCode, erp);
			return;
		}
		List<ManagePatrolAreaConfig> configs = JsonHelper.jsonToList(managePatrolTaskToAreaConfig.getConfigContent(), ManagePatrolAreaConfig.class);
		if(CollectionUtils.isEmpty(configs)){
			logger.warn("生成飞检巡场任务,配置的任务对应作业区网格数量反序列对象失败，erp:{}，content:{}", erp, managePatrolTaskToAreaConfig.getConfigContent());
			return;
		}
		Map<WorkGridManagerTask, List<WorkGrid>> taskToWorkGridList = getTaskToWorkGridList(configs, taskResult.getData(),
				erp, siteCode);
		if(taskToWorkGridList == null){
			return;
		}
		Date curDate = new Date();
		Date preFinishTime = DateUtil.addDay(curDate, 1);
		List<JyBizTaskWorkGridManager> bizTaskWorkGridManagers = getTaskList(taskToWorkGridList, siteInfo,
				userPositionCode, userPositionName, erp, userName, curDate, preFinishTime);
		//保存已分配的任务
		batchInsertDistributionTask(bizTaskWorkGridManagers);
		List<String> bizIdList = bizTaskWorkGridManagers.stream().map(JyBizTaskWorkGridManager::getBizId).collect(Collectors.toList());
		//保持超时任务
		saveAutoCloseTask(preFinishTime,siteCode, bizIdList);
	}

	@Override
	public JdCResponse<Boolean> transferCandidate(JyWorkGridManagerTransferData request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		if (StringUtils.isEmpty(request.getBizId())){
			result.toFail("业务id为空");
			return result;
		}
		if (StringUtils.isEmpty(request.getErp())){
			result.toFail("管理任务转派责任人ERP为空");
			return result;
		}
		BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(request.getErp());
		JyBizTaskWorkGridManager jyBizTaskWorkGridManager = jyBizTaskWorkGridManagerDao.queryByBizId(request.getBizId());
		if (jyBizTaskWorkGridManager!=null){
			JyBizTaskWorkGridManager manager = new JyBizTaskWorkGridManager();
			manager.setId(jyBizTaskWorkGridManager.getId());
			manager.setTransfered(1);
			manager.setTransferTime(new Date());
			manager.setOrignHandlerErp(jyBizTaskWorkGridManager.getHandlerErp());
			manager.setOrignHandlerUserName(jyBizTaskWorkGridManager.getHandlerUserName());
			manager.setOrignHandlerUserPositionCode(jyBizTaskWorkGridManager.getHandlerUserPositionCode());
			manager.setOrignHandlerUserPositionName(jyBizTaskWorkGridManager.getHandlerUserPositionName());
			manager.setHandlerErp(request.getErp());
			manager.setHandlerUserName(baseStaff.getStaffName());
			manager.setUpdateTime(new Date());
			manager.setUpdateUser(jyBizTaskWorkGridManager.getHandlerErp());
			manager.setUpdateUserName(jyBizTaskWorkGridManager.getHandlerUserName());
			Result<JyUser> jyUserResult = jyUserManager.queryUserInfo(request.getErp());
			if(jyUserResult.getData() != null){
				manager.setHandlerUserPositionCode(jyUserResult.getData().getPositionCode());
				manager.setHandlerUserPositionName(jyUserResult.getData().getPositionName());
			}
			jyBizTaskWorkGridManagerDao.transfer(manager);
			JyUserDto user = new JyUserDto();
			user.setUserErp(manager.getHandlerErp());
			jyWorkGridManagerBusinessService.sendTimeLineNotice(WorkGridManagerTaskBizType.getEnum(jyBizTaskWorkGridManager.getTaskBizType()),user);
		}

		result.toSucceed();
		result.setData(true);
		return result;
	}


	private List<JyBizTaskWorkGridManager> getTaskList(Map<WorkGridManagerTask, List<WorkGrid>> taskToWorkGridList,
													   BaseSiteInfoDto siteInfo, String handlerPositionCode,
													   String handlerPositionName, String erp, String userName, Date curDate,
													   Date preFinishTime){
		List<JyBizTaskWorkGridManager> jyTaskInitList = new ArrayList<>();
		for(Map.Entry<WorkGridManagerTask, List<WorkGrid>> entry : taskToWorkGridList.entrySet()){
			WorkGridManagerTask taskInfo = entry.getKey();
			for(WorkGrid workGrid : entry.getValue()){
				jyTaskInitList.add(initJyBizTaskWorkGridManager(siteInfo, taskInfo, handlerPositionCode,
						handlerPositionName, workGrid,curDate, erp, userName, preFinishTime));
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
		while(!CollectionUtils.isEmpty(gridList) && pageNum <= 20) {
			sumGridList.addAll(gridList);
			pageNum ++;
			workGridQuery.setPageNumber(pageNum);
			gridList = workGridManager.queryListForManagerSiteScan(workGridQuery);
		}
		if(CollectionUtils.isEmpty(sumGridList)){
			logger.info("生成飞检巡场任务,本场地无符合生成任务的网格，erp:{},siteCode:{}", erp, siteCode);
			return null;
		}

		Map<String, List<WorkGrid>> areaCodeToGridList = sumGridList.stream().collect(Collectors.groupingBy(WorkGrid::getAreaCode));
		Map<String, WorkGridManagerTask> taskCodeToTask = tasks.stream()
				.collect(Collectors.toMap(WorkGridManagerTask::getTaskCode, WorkGridManagerTask -> WorkGridManagerTask));
		Map<WorkGridManagerTask, List<WorkGrid>> map = new HashMap<>();
		for(ManagePatrolAreaConfig config : configs){
			WorkGridManagerTask task = taskCodeToTask.get(config.getTaskCode());
			if(task == null){
				logger.info("生成飞检巡场任务,此任务已删除或无效，erp:{},taskCode:{}", erp, config.getTaskCode());
				continue;
			}
			//该作业区的网格
			List<WorkGrid> workGrids = areaCodeToGridList.get(config.getAreaCode());

			if(CollectionUtils.isEmpty(workGrids)){
				logger.info("生成飞检巡场任务,本场地无此作业区网格，erp:{},siteCode:{}，areaCode:{}", erp, siteCode, config.getAreaCode());
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
																  String handlerPositionName,
																  WorkGrid grid, Date curDate,
																  String erp, String userName,
																  Date preFinishTime) {
		TaskWorkGridManagerSiteScanData taskWorkGridManagerScan = new TaskWorkGridManagerSiteScanData();
		taskWorkGridManagerScan.setTaskConfigCode("");
		taskWorkGridManagerScan.setTaskBatchCode("");
		WorkGridManagerTaskConfigVo configData = new WorkGridManagerTaskConfigVo();
		configData.setHandlerUserPositionCode(handlerPositionCode);
		configData.setHandlerUserPositionName(handlerPositionName);
		JyBizTaskWorkGridManager jyTask = jyWorkGridManagerBusinessService.initJyBizTaskWorkGridManager(siteInfo, taskWorkGridManagerScan,taskInfo,
				configData, grid, curDate);

		//设置任务配置信息
		jyTask.setHandlerPositionCode(handlerPositionCode);
		jyTask.setTaskBizType(WorkGridManagerTaskBizType.MANAGER_PATROL.getCode());
		jyTask.setProcessBeginTime(curDate);
		jyTask.setHandlerErp(erp);
		jyTask.setHandlerUserName(userName);
		jyTask.setPreFinishTime(preFinishTime);
		jyTask.setStatus(WorkTaskStatusEnum.TODO.getCode());
		jyTask.setTaskDate(curDate);
		return jyTask;
	}

	private void saveAutoCloseTask(Date preFinishTime, Integer siteCode, List<String> bizIdList){
		if(CollectionUtils.isEmpty(bizIdList)){
			return;
		}
		TaskWorkGridManagerAutoCloseData autoCloseTaskData = new TaskWorkGridManagerAutoCloseData();
		autoCloseTaskData.setTaskConfigCode("");
		autoCloseTaskData.setSiteCode(siteCode);
		autoCloseTaskData.setTaskBatchCode("");
		autoCloseTaskData.setExecuteTime(preFinishTime);
		autoCloseTaskData.setBizIdList(bizIdList);
		jyWorkGridManagerBusinessService.addWorkGridManagerAutoCloseTask(autoCloseTaskData);
	}
	@Override
	public String selectLastHandlerErp(String taskCode){
		return jyBizTaskWorkGridManagerDao.selectLastHandlerErp(taskCode);
	}
}
