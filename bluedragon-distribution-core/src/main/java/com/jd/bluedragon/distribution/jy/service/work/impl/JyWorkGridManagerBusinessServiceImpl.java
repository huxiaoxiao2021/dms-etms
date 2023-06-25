package com.jd.bluedragon.distribution.jy.service.work.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerCaseItemData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerData;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerTaskEditRequest;
import com.jd.bluedragon.common.dto.work.ScanTaskPositionRequest;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskConfigJsfManager;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerBatchUpdate;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerQuery;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCase;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerAutoCloseData;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerScanData;
import com.jd.bluedragon.distribution.jy.dto.work.TaskWorkGridManagerSiteScanData;
import com.jd.bluedragon.distribution.jy.enums.EditTypeEnum;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.service.work.JyBizTaskWorkGridManagerService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerBusinessService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseItemService;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.jsf.gd.util.StringUtils;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfig;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.api.enums.FrequencyTypeEnum;
import com.jdl.basic.api.enums.WorkFinishTypeEnum;
import com.jdl.basic.common.utils.Result;

/**
 * @ClassName: JyBizTaskWorkGridManagerServiceImpl
 * @Description: 巡检任务表--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyWorkGridManagerBusinessService")
public class JyWorkGridManagerBusinessServiceImpl implements JyWorkGridManagerBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(JyWorkGridManagerBusinessServiceImpl.class);

	@Autowired
	@Qualifier("jyBizTaskWorkGridManagerService")
	private JyBizTaskWorkGridManagerService jyBizTaskWorkGridManagerService;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseService")
	private JyWorkGridManagerCaseService jyWorkGridManagerCaseService;
	
	@Autowired
	@Qualifier("jyWorkGridManagerCaseItemService")
	private JyWorkGridManagerCaseItemService jyWorkGridManagerCaseItemService;	
	
	@Autowired
	@Qualifier("workGridManagerTaskJsfManager")
	private WorkGridManagerTaskJsfManager workGridManagerTaskJsfManager;
	
	@Autowired
	@Qualifier("workGridManagerTaskConfigJsfManager")
	private WorkGridManagerTaskConfigJsfManager workGridManagerTaskConfigJsfManager;
	
	@Autowired
	private WorkStationGridManager workStationGridManager;
	
	@Autowired
	@Qualifier("jyUserManager")
	private JyUserManager jyUserManager;
	
	
	@Autowired
	@Qualifier("jyAttachmentDetailService")
	private JyAttachmentDetailService jyAttachmentDetailService;
	
    @Autowired
    protected TaskService taskService;
    
	@Autowired
	private PositionManager positionManager;
	
    private Random random = new Random();

	@Override
	public JdCResponse<Boolean> submitData(JyWorkGridManagerTaskEditRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("保存成功！");
		if(request == null
				|| request.getTaskData() == null) {
			result.toFail("请求参数不能为空！");
			return result;
		}
		if(request.getUser() == null 
				|| StringUtils.isBlank(request.getUser().getUserErp())) {
			result.toFail("操作人erp不能为空！");
			return result;
		}
		request.getCurrentOperate();
		JyWorkGridManagerData taskData = request.getTaskData();
		String bizId = taskData.getBizId();
		String userErp = request.getUser().getUserErp();
		String userName = request.getUser().getUserName();
		Date currentTime = new Date();
		JyWorkGridManagerData oldData = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
		if(oldData == null) {
			result.toFail("任务不存在|已删除！");
			return result;
		}
		if(WorkTaskStatusEnum.COMPLETE.getCode().equals(oldData.getStatus())) {
			result.toFail("任务已完成，不能重复提交！");
			return result;
		}
		if(WorkTaskStatusEnum.OVER_TIME.getCode().equals(oldData.getStatus())
				|| (oldData.getPreFinishTime() != null && currentTime.after(oldData.getPreFinishTime()))) {
			result.toFail("任务已超时，不能提交！");
			return result;
		}
		JyBizTaskWorkGridManager updateTaskData = new JyBizTaskWorkGridManager();
		updateTaskData.setStatus(WorkTaskStatusEnum.COMPLETE.getCode());
		updateTaskData.setHandlerPositionCode("");
		updateTaskData.setUpdateUser(userErp);
		updateTaskData.setUpdateUserName(userName);
		updateTaskData.setUpdateTime(currentTime);
		updateTaskData.setProcessEndTime(currentTime);
		updateTaskData.setId(oldData.getId());
		List<JyAttachmentDetailEntity> addAttachmentList = new ArrayList<>();
		List<JyWorkGridManagerCase> updateCase = new ArrayList<>();
		List<JyWorkGridManagerCase> addCase = new ArrayList<>();
		List<JyWorkGridManagerCaseItem> addCaseItem = new ArrayList<>(); 
		int caseIndex = 0;
		//保存case信息
		for(JyWorkGridManagerCaseData caseData : taskData.getCaseList()) {
			caseIndex ++;
			caseData.setBizId(bizId);
			caseData.setOrderNum(caseIndex);
			if(EditTypeEnum.ADD.getCode().equals(caseData.getEditType())
					|| caseData.getId() == null) {
				addCase.add(toJyWorkGridManagerCaseForAdd(userErp,currentTime,taskData,caseData));
			} else if(EditTypeEnum.MODIFY.getCode().equals(caseData.getEditType())) {
				updateCase.add(toJyWorkGridManagerCaseForUpdate(userErp,currentTime,taskData,caseData));
			}
			if(!CollectionUtils.isEmpty(caseData.getAttachmentList())) {
				for(AttachmentDetailData attachmentData : caseData.getAttachmentList()) {
					addAttachmentList.add(toJyAttachmentDetailEntity(userErp,currentTime,taskData,caseData,attachmentData));
				}
			}
			if(!CollectionUtils.isEmpty(caseData.getItemList())) {
				for(JyWorkGridManagerCaseItemData caseItem: caseData.getItemList()) {
					if(caseItem.checkIsSelected()) {
						addCaseItem.add(toJyWorkGridManagerCaseItem(userErp,currentTime,taskData,caseData,caseItem));
					}
				}
			}
		}
		
		jyWorkGridManagerCaseService.batchInsert(addCase);
		jyWorkGridManagerCaseService.batchUpdate(updateCase);
		jyWorkGridManagerCaseItemService.batchInsert(addCaseItem);
		jyAttachmentDetailService.batchInsert(addAttachmentList);
		jyBizTaskWorkGridManagerService.finishTask(updateTaskData);
		return result;
	}

	private JyWorkGridManagerCase toJyWorkGridManagerCaseForUpdate(String userErp, Date currentTime,
			JyWorkGridManagerData taskData, JyWorkGridManagerCaseData caseData) {
		JyWorkGridManagerCase caseEntity = new JyWorkGridManagerCase();
		caseEntity.setId(caseData.getId());
		caseEntity.setBizId(caseData.getBizId());
		caseEntity.setCaseCode(caseData.getCaseCode());
		caseEntity.setCheckResult(caseData.getCheckResult());
		caseEntity.setCaseTitle(caseData.getCaseTitle());
		caseEntity.setCaseContent(caseData.getCaseContent());
		caseEntity.setUpdateUser(userErp);
		caseEntity.setUpdateTime(currentTime);
		return caseEntity;
	}

	private JyWorkGridManagerCase toJyWorkGridManagerCaseForAdd(String userErp, Date currentTime,
			JyWorkGridManagerData taskData, JyWorkGridManagerCaseData caseData) {
		JyWorkGridManagerCase caseEntity = new JyWorkGridManagerCase();
		caseEntity.setBizId(caseData.getBizId());
		caseEntity.setCaseCode(caseData.getCaseCode());
		caseEntity.setCheckResult(caseData.getCheckResult());
		caseEntity.setCaseTitle(caseData.getCaseTitle());
		caseEntity.setCaseContent(caseData.getCaseContent());
		caseEntity.setCreateUser(userErp);
		caseEntity.setCreateTime(currentTime);
		return caseEntity;
	}

	private JyWorkGridManagerCaseItem toJyWorkGridManagerCaseItem(String userErp, Date currentTime, JyWorkGridManagerData taskData,
			JyWorkGridManagerCaseData caseData, JyWorkGridManagerCaseItemData caseItem) {
		JyWorkGridManagerCaseItem caseItemEntity = new JyWorkGridManagerCaseItem();
        caseItemEntity.setBizId(caseData.getBizId());
        caseItemEntity.setCaseCode(caseData.getCaseCode());
        caseItemEntity.setCaseItemCode(caseItem.getCaseItemCode());
        caseItemEntity.setSelectFlag(caseItem.getSelectFlag());
        caseItemEntity.setCreateUser(userErp);
        caseItemEntity.setCreateTime(currentTime);
		return caseItemEntity;
	}

	private JyAttachmentDetailEntity toJyAttachmentDetailEntity(String userErp, Date currentTime, JyWorkGridManagerData taskData,JyWorkGridManagerCaseData caseData,AttachmentDetailData attachmentData) {
		JyAttachmentDetailEntity attachmentEntity = new JyAttachmentDetailEntity();
        attachmentEntity.setBizId(caseData.getBizId());
        attachmentEntity.setSiteCode(taskData.getSiteCode());
        attachmentEntity.setBizType(JyAttachmentBizTypeEnum.TASK_WORK_GRID_MANAGER.getCode());
        attachmentEntity.setBizSubType(caseData.getCaseCode());
        attachmentEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
        attachmentEntity.setCreateUserErp(userErp);
        attachmentEntity.setCreateTime(currentTime);
        attachmentEntity.setUpdateUserErp(userErp);
        
        attachmentEntity.setAttachmentUrl(attachmentData.getAttachmentUrl());
		return attachmentEntity;
	}

	@Override
	public JdCResponse<Boolean> saveData(JyWorkGridManagerTaskEditRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.toSucceed("保存成功！");
		return result;
	}

	@Override
	public void startWorkGridManagerScanTask(WorkGridManagerTaskConfig workGridManagerTaskConfig) {
		//初始化-workGridManagerScanTask任务数据
		Result<WorkGridManagerTaskConfigVo> configResult = workGridManagerTaskConfigJsfManager.queryByTaskConfigCode(workGridManagerTaskConfig.getTaskConfigCode());
		if(configResult == null
				|| configResult.isEmptyData()) {
			logger.warn("startWorkGridManagerScanTask-fail! taskConfig={}",JsonHelper.toJson(workGridManagerTaskConfig));
			return;
		}
		//根据taskConfigCode查询是否存在未执行的任务
		WorkGridManagerTaskConfigVo configData = configResult.getData();
		
		TaskWorkGridManagerScanData taskData = new TaskWorkGridManagerScanData();
		taskData.setTaskConfigCode(configData.getTaskConfigCode());
		taskData.setExecuteTime(getExecuteTime(configData));
		
		Task tTask = taskService.findLastWorkGridManagerScanTask(taskData);
		//判断是否存在任务
		if(tTask != null) {
			TaskWorkGridManagerScanData oldTaskData = JsonHelper.fromJson(tTask.getBody(), TaskWorkGridManagerScanData.class);
			if(!taskData.getExecuteTime().equals(oldTaskData.getExecuteTime())) {
				Task updateTask = new Task();
				//更新任务执行时间
				updateTask.setType(Task.TASK_TYPE_WorkGridManagerScan);
				updateTask.setBody(JsonHelper.toJson(taskData));
			      //提前10分钟执行
				updateTask.setExecuteTime(DateHelper.add(taskData.getExecuteTime(),Calendar.MINUTE , -30));
				updateTask.setId(tTask.getId());
		        taskService.updateBySelectiveWithBody(updateTask);		        
			}
		}else {
			//新增任务
	        addWorkGridManagerScanTask(taskData);
		}
//		tTask = new Task();
//		tTask.setBody(JsonHelper.toJson(taskData));
//      executeWorkGridManagerScanTask(tTask);
	}
	private Date getExecuteTime(WorkGridManagerTaskConfigVo configData) {
		FrequencyTypeEnum  frequencyType = FrequencyTypeEnum.getEnum(configData.getFrequencyType());
		if(frequencyType == null) {
			logger.warn("任务配置-frequencyType无效，无法计算执行时间");
			return null;
		}
		Date executeTime = frequencyType.getNextTime(new Date(), configData.getFrequencyHour(), configData.getFrequencyMinute(), configData.getFrequencyDayList());
        return executeTime;
	}
	private Date getNextExecuteTime(Date lastExecuteTime,WorkGridManagerTaskConfigVo configData) {
		FrequencyTypeEnum  frequencyType = FrequencyTypeEnum.getEnum(configData.getFrequencyType());
		if(frequencyType == null) {
			logger.warn("任务配置-frequencyType无效，无法计算执行时间");
			return null;
		}
		Date executeTime = frequencyType.getNextTime(lastExecuteTime, configData.getFrequencyHour(), configData.getFrequencyMinute(), configData.getFrequencyDayList());
        return executeTime;
	}	
	@Override
	public boolean executeWorkGridManagerScanTask(Task task) {
		Log.info("executeWorkGridManagerScanTask-start! task={}",JsonHelper.toJson(task));
		TaskWorkGridManagerScanData taskWorkGridManagerScan = JsonHelper.fromJson(task.getBody(), TaskWorkGridManagerScanData.class);
		Result<WorkGridManagerTaskConfigVo> configResult = workGridManagerTaskConfigJsfManager.queryByTaskConfigCode(taskWorkGridManagerScan.getTaskConfigCode());
		if(configResult == null
				|| configResult.isEmptyData()) {
			Log.warn("startWorkGridManagerScanTask-fail! taskConfig={}",JsonHelper.toJson(taskWorkGridManagerScan));
			return false;
		}
		//根据taskConfigCode查询是否存在未执行的任务，
		WorkGridManagerTaskConfigVo configData = configResult.getData();
		//查询网格场地
		WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
		workStationGridQuery.setAreaCodeList(configData.getWorkAreaCodeList());
		workStationGridQuery.setPageSize(100);
		int pageNum = 1;
		List<Integer> siteCodeList = null;
		do {
			workStationGridQuery.setPageNumber(pageNum);
			siteCodeList = workStationGridManager.querySiteListForManagerScan(workStationGridQuery);
			for(Integer siteCode : siteCodeList) {
				TaskWorkGridManagerSiteScanData taskData = new TaskWorkGridManagerSiteScanData();
				taskData.setTaskConfigCode(configData.getTaskConfigCode());
				taskData.setSiteCode(siteCode);
				taskData.setTaskBatchCode(String.format("%s_%s_%s",configData.getTaskConfigCode(),siteCode,DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)));
				taskData.setExecuteTime(getExecuteTime(configData));
				Task tTask = taskService.findLastWorkGridManagerSiteScanTask(taskData);
				if(tTask != null) {
					TaskWorkGridManagerSiteScanData oldTaskData = JsonHelper.fromJson(tTask.getBody(), TaskWorkGridManagerSiteScanData.class);
					taskData.setTaskBatchCode(oldTaskData.getTaskBatchCode());
					taskData.setExecuteCount(oldTaskData.getExecuteCount());
					taskData.setLastExecuteTime(oldTaskData.getLastExecuteTime());
					taskData.setExecuteTime(getNextExecuteTime(oldTaskData.getLastExecuteTime(),configData));
					//判断上次执行时间和这次是否同一天,改成下个执行周期时间
					if(DateHelper.isSameDay(taskData.getExecuteTime(),oldTaskData.getLastExecuteTime())) {
						taskData.setExecuteTime(getNextExecuteTime(taskData.getExecuteTime(),configData));
					}
					if(!taskData.getExecuteTime().equals(oldTaskData.getExecuteTime())) {
						Task updateTask = new Task();
						updateTask.setType(Task.TASK_TYPE_WorkGridManagerSiteScan);
						//更新任务执行时间
						updateTask.setBody(JsonHelper.toJson(taskData));
					    //提前15分钟执行
						updateTask.setExecuteTime(DateHelper.add(taskData.getExecuteTime(),Calendar.MINUTE , -15));
						updateTask.setId(tTask.getId());
				        taskService.updateBySelectiveWithBody(updateTask);
					}
				}else {
					//新增任务
			        this.addWorkGridManagerSiteScanTask(taskData);
				}
			}
			pageNum ++;
		}while(!CollectionUtils.isEmpty(siteCodeList));
		
		//插入一条新的待执行任务
		TaskWorkGridManagerScanData taskData = new TaskWorkGridManagerScanData();
		taskData.setTaskConfigCode(configData.getTaskConfigCode());
		taskData.setExecuteTime(getNextExecuteTime(taskWorkGridManagerScan.getExecuteTime(),configData));
        addWorkGridManagerScanTask(taskData);
		return true;
	}

	@Override
	public boolean executeWorkGridManagerSiteScanTask(Task task) {
		Log.info("executeWorkGridManagerSiteScanTask-start! task={}",JsonHelper.toJson(task));
		TaskWorkGridManagerSiteScanData taskWorkGridManagerScan = JsonHelper.fromJson(task.getBody(), TaskWorkGridManagerSiteScanData.class);
		Result<WorkGridManagerTaskConfigVo> configResult = workGridManagerTaskConfigJsfManager.queryByTaskConfigCode(taskWorkGridManagerScan.getTaskConfigCode());
		if(configResult == null
				|| configResult.isEmptyData()) {
			Log.warn("executeWorkGridManagerSiteScanTask-fail! taskConfig={}",JsonHelper.toJson(taskWorkGridManagerScan));
			return false;
		}
		//根据taskConfigCode查询是否存在未执行的任务
		WorkGridManagerTaskConfigVo configData = configResult.getData();
		Integer siteCode = taskWorkGridManagerScan.getSiteCode();
		String taskBatchCode = taskWorkGridManagerScan.getTaskBatchCode();
		Integer site910 = 910;
		if(!site910.equals(siteCode)) {
			return true;
		}
		Result<WorkGridManagerTask> taskInfoResult = workGridManagerTaskJsfManager.queryByTaskCode(configData.getTaskCode());
		if(taskInfoResult == null
				|| taskInfoResult.getData() == null) {
			Log.warn("startWorkGridManagerScanTask-fail! taskCode={}",configData.getTaskCode());
			return false;
		}
		WorkGridManagerTask taskInfo = taskInfoResult.getData();
		//查询网格场地
		WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
		workStationGridQuery.setSiteCode(siteCode);
		workStationGridQuery.setAreaCodeList(configData.getWorkAreaCodeList());
		workStationGridQuery.setPageSize(100);
		
		boolean hasGridData = false;
		int initTaskNum = 0;
		int pageNum = 1;
		Date curDate = new Date();
		Date taskTime = taskWorkGridManagerScan.getExecuteTime();
		Date taskEndTime = DateHelper.addDate(taskTime, 1);
		if(WorkFinishTypeEnum.ONE_WEEK.getCode().equals(configData.getFinishType())) {
			taskEndTime = DateHelper.addDate(taskTime, 7);
		}
		workStationGridQuery.setPageNumber(pageNum);
		List<WorkStationGrid> gridList = workStationGridManager.queryListForManagerSiteScan(workStationGridQuery);
		if(!CollectionUtils.isEmpty(gridList)) {
			hasGridData = true;
		}
		if(taskWorkGridManagerScan.getExecuteCount() == 0) {
			List<JyBizTaskWorkGridManager> jyTaskInitList = new ArrayList<>();
			while(!CollectionUtils.isEmpty(gridList)) {
				for(WorkStationGrid grid: gridList) {
					jyTaskInitList.add(this.initJyBizTaskWorkGridManager(taskWorkGridManagerScan, taskInfo, configData, grid, curDate));
					initTaskNum ++;
				}
				pageNum ++;
				workStationGridQuery.setPageNumber(pageNum);
				gridList = workStationGridManager.queryListForManagerSiteScan(workStationGridQuery);
			}
			jyBizTaskWorkGridManagerService.batchAddTask(jyTaskInitList);
		}
		boolean needDistribution = true;
		boolean changeBatchCode = false;
		List<String> userList = getUserList(siteCode,configData.getHandlerUserPositionCode());
		int needGridNum = userList.size() * configData.getPerGridNum();
		if(CollectionUtils.isEmpty(userList)) {
			needDistribution = false;
			logger.warn("任务分配失败，场地【{}】岗位【{}】人员为空！",siteCode,configData.getHandlerUserPositionName());
		}
		//判断上次执行时间和这次是否同一天,改成下个执行周期时间
		if(DateHelper.isSameDay(taskWorkGridManagerScan.getExecuteTime(),taskWorkGridManagerScan.getLastExecuteTime())) {
			needDistribution = false;
			logger.warn("本次不执行任务分配，本次执行时间和上次执行时间相同！");
		}
		if(needDistribution) {
			JyBizTaskWorkGridManagerQuery query = new JyBizTaskWorkGridManagerQuery();
			query.setTaskBatchCode(taskBatchCode);
			query.setAreaCodeList(configData.getWorkAreaCodeList());
			query.setStatus(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());
			query.setLimit(needGridNum + 1);
			
			List<JyBizTaskWorkGridManager> jyTaskList = jyBizTaskWorkGridManagerService.queryDataListForDistribution(query);
			if(jyTaskList == null
					|| jyTaskList.size() <= needGridNum) {
				changeBatchCode = true;
			}
			this.doDistributionTask(taskWorkGridManagerScan, taskInfo, configData, userList, jyTaskList, needGridNum, curDate, taskTime, taskEndTime);
		}
		//批次完结，取消无用的任务
		if(changeBatchCode) {
			JyBizTaskWorkGridManagerBatchUpdate closeData = new JyBizTaskWorkGridManagerBatchUpdate();
			JyBizTaskWorkGridManager data = new JyBizTaskWorkGridManager();
			closeData.setData(data);
			data.setStatus(WorkTaskStatusEnum.CANCEL.getCode());
			data.setUpdateTime(new Date());
			data.setUpdateUser(DmsConstants.SYS_AUTO_USER_CODE);
			data.setUpdateUserName(DmsConstants.SYS_AUTO_USER_CODE);			
			jyBizTaskWorkGridManagerService.closeTaskForEndBatch(closeData);
		}
		//新增下次执行时间的任务
		TaskWorkGridManagerSiteScanData taskData = new TaskWorkGridManagerSiteScanData();
		taskData.setTaskConfigCode(configData.getTaskConfigCode());
		taskData.setSiteCode(siteCode);
		
		taskData.setExecuteTime(getNextExecuteTime(taskWorkGridManagerScan.getExecuteTime(),configData));
		if(changeBatchCode) {
			taskBatchCode = String.format("%s_%s_%s",configData.getTaskConfigCode(),siteCode,DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS));
			taskData.setExecuteCount(0);
		}else {
			taskData.setTaskBatchCode(taskBatchCode);
			taskData.setExecuteCount(taskWorkGridManagerScan.getExecuteCount() + 1);
		}
		taskData.setLastExecuteTime(taskWorkGridManagerScan.getExecuteTime());
		addWorkGridManagerSiteScanTask(taskData);
		return true;
	}
	/**
	 * 分配成功的，发送咚咚通知
	 * @param user
	 */
	private void sendTimeLineNotice(String user) {
		//发送咚咚通知
        String title = "场地自检巡检任务通知";
        String content = "您已收到场地自检巡检任务，请进入拣运APP，扫描管理者网格码，查看任务，并按时完成，逾期将记录在册";
        List<String> erpList = Lists.newArrayList();
        erpList.add(user);
		logger.info("分配任务完成，发送咚咚通知：{} 标题：{} 内容：{}",title,user,content);
        NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);		
	}
	private void doDistributionTask(TaskWorkGridManagerSiteScanData taskWorkGridManagerScan,WorkGridManagerTask taskInfo,WorkGridManagerTaskConfigVo configData,
	 List<String> userList,List<JyBizTaskWorkGridManager> jyTaskList,int needGridNum,
	 Date curDate,Date taskTime,Date taskEndTime){
		String taskBatchCode = taskWorkGridManagerScan.getTaskBatchCode();
		int gridIndex = 0 ;
		boolean finishTask = false;
		List<String> bizIdListAutoClose = new ArrayList<>();
		for(String user : userList) {
			if(finishTask) {
				break;
			}
			JyBizTaskWorkGridManagerBatchUpdate distributionData = new JyBizTaskWorkGridManagerBatchUpdate();
			List<String> bizIdUserList = new ArrayList<>();
			distributionData.setBizIdList(bizIdUserList);
			for(int i=0;i<configData.getPerGridNum();i++) {
				if(gridIndex >= jyTaskList.size() || gridIndex >= needGridNum) {
					finishTask = true;
					break;
				}
				JyBizTaskWorkGridManager jyTask = jyTaskList.get(gridIndex);
				bizIdUserList.add(jyTask.getBizId());
				bizIdListAutoClose.add(jyTask.getBizId());
				//设置任务配置信息
				jyTask.setHandlerUserPositionCode(configData.getHandlerUserPositionCode());
				jyTask.setHandlerUserPositionName(configData.getHandlerUserPositionName());
				jyTask.setCreateTime(curDate);
				jyTask.setProcessBeginTime(taskTime);
				jyTask.setHandlerErp(user);
				jyTask.setPreFinishTime(taskEndTime);
				jyTask.setStatus(WorkTaskStatusEnum.TODO.getCode());
				jyTask.setTaskDate(taskTime);
				gridIndex++;
				distributionData.setData(jyTask);
			}
			jyBizTaskWorkGridManagerService.distributionTask(distributionData);
			//发送咚咚通知
			sendTimeLineNotice(user);
		}
		//已分配的任务，新增一个自动关闭任务
		if(bizIdListAutoClose.size() > 0) {
			TaskWorkGridManagerAutoCloseData autoCloseTaskData = new TaskWorkGridManagerAutoCloseData();
			autoCloseTaskData.setTaskConfigCode(configData.getTaskConfigCode());
			autoCloseTaskData.setSiteCode(taskWorkGridManagerScan.getSiteCode());
			autoCloseTaskData.setTaskBatchCode(taskBatchCode);
			autoCloseTaskData.setExecuteTime(taskEndTime);
			autoCloseTaskData.setBizIdList(bizIdListAutoClose);
			addWorkGridManagerAutoCloseTask(autoCloseTaskData);
		}		
	}
	private JyBizTaskWorkGridManager initJyBizTaskWorkGridManager(TaskWorkGridManagerSiteScanData taskWorkGridManagerScan,
			WorkGridManagerTask taskInfo,WorkGridManagerTaskConfigVo configData,
			WorkStationGrid grid,Date curDate) {
		JyBizTaskWorkGridManager jyTask = new JyBizTaskWorkGridManager();
		jyTask.setBizId(UUID.randomUUID().toString());
		//设置任务配置信息
		jyTask.setTaskConfigCode(taskWorkGridManagerScan.getTaskConfigCode());
		jyTask.setTaskBatchCode(taskWorkGridManagerScan.getTaskBatchCode());
		jyTask.setHandlerUserPositionCode(configData.getHandlerUserPositionCode());
		jyTask.setHandlerUserPositionName(configData.getHandlerUserPositionName());
		jyTask.setCreateTime(curDate);
		jyTask.setStatus(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());
		
		//设置网格信息
		jyTask.setTaskRefGridKey(grid.getBusinessKey());
		jyTask.setAreaCode(grid.getAreaCode());
		jyTask.setAreaName(grid.getAreaName());
		jyTask.setGridName(grid.getGridName());			
		jyTask.setSiteCode(grid.getSiteCode());
		jyTask.setSiteName(grid.getSiteName());
		//省区相关字段等，上线后设置
		jyTask.setAreaHubCode("");
		jyTask.setProvinceAgencyName("北京市");
		//设置任务信息
		jyTask.setTaskType(taskInfo.getTaskType());
		jyTask.setNeedScanGrid(taskInfo.getNeedScanGrid());
		jyTask.setTaskCode(taskInfo.getTaskCode());
		jyTask.setTaskName(taskInfo.getTaskName());
		jyTask.setTaskDescription(taskInfo.getTaskDescription());
		jyTask.setOrderNum(random.nextInt(1000));
		return jyTask;
	}
	private List<String> getUserList(Integer siteCode,String userPositionCode){
		Result<List<String>> userResult = jyUserManager.queryUserListBySiteAndPosition(siteCode, userPositionCode);
		if(userResult != null && userResult.getData() != null) {
			return userResult.getData();
		}
		return new ArrayList<>();
	}
	@Override
	public boolean executeWorkGridManagerAutoCloseTask(Task task) {
		TaskWorkGridManagerAutoCloseData taskData = JsonHelper.fromJson(task.getBody(), TaskWorkGridManagerAutoCloseData.class);
		JyBizTaskWorkGridManagerBatchUpdate closeData = new JyBizTaskWorkGridManagerBatchUpdate();
		closeData.setBizIdList(taskData.getBizIdList());
		
		JyBizTaskWorkGridManager data = new JyBizTaskWorkGridManager();
		data.setStatus(WorkTaskStatusEnum.TODO.getCode());
		data.setUpdateTime(new Date());
		data.setUpdateUser(DmsConstants.SYS_AUTO_USER_CODE);
		data.setUpdateUserName(DmsConstants.SYS_AUTO_USER_CODE);
		closeData.setData(data);
		jyBizTaskWorkGridManagerService.autoCloseTask(closeData);
		return true;
	}
	void addWorkGridManagerScanTask(TaskWorkGridManagerScanData taskData){
		//新增|修改任务-修改执行时间
		Task tTask = new Task();
		tTask.setBody(JsonHelper.toJson(taskData));

        tTask.setCreateSiteCode(0);
        tTask.setKeyword1(taskData.getTaskConfigCode());
        tTask.setKeyword2(DateHelper.formatDateTime(taskData.getExecuteTime()));
        tTask.setType(Task.TASK_TYPE_WorkGridManagerScan);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WorkGridManagerScan));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s", tTask.getKeyword1(), tTask.getKeyword2())));
      //提前10分钟执行
        tTask.setExecuteTime(DateHelper.add(taskData.getExecuteTime(),Calendar.MINUTE , -30));
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        tTask.setExecuteCount(0);

        logger.info("startWorkGridManagerScanTask-taskData={}", JsonHelper.toJson(taskData));
        taskService.doAddTask(tTask, false);
	}
	void addWorkGridManagerSiteScanTask(TaskWorkGridManagerSiteScanData taskData) {
		Task tTask = new Task();
		tTask.setBody(JsonHelper.toJson(taskData));

        tTask.setCreateSiteCode(taskData.getSiteCode());
        tTask.setKeyword1(taskData.getTaskConfigCode());
        tTask.setKeyword2(taskData.getSiteCode().toString());
        tTask.setType(Task.TASK_TYPE_WorkGridManagerSiteScan);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WorkGridManagerSiteScan));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), DateHelper.formatDate(taskData.getExecuteTime()))));
        //提前5分钟执行
        tTask.setExecuteTime(DateHelper.add(taskData.getExecuteTime(),Calendar.MINUTE , -15));
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        tTask.setExecuteCount(0);

        logger.info("executeWorkGridManagerScanTask-taskSiteData={}", JsonHelper.toJson(taskData));
        taskService.doAddTask(tTask, false);
	}
	void addWorkGridManagerAutoCloseTask(TaskWorkGridManagerAutoCloseData taskData) {
		Task tTask = new Task();
		tTask.setBody(JsonHelper.toJson(taskData));

        tTask.setCreateSiteCode(taskData.getSiteCode());
        tTask.setKeyword1(taskData.getTaskConfigCode());
        tTask.setKeyword2(taskData.getSiteCode().toString());
        tTask.setType(Task.TASK_TYPE_JyWorkGridManagerAutoClose);
        tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JyWorkGridManagerAutoClose));
        String ownSign = BusinessHelper.getOwnSign();
        tTask.setOwnSign(ownSign);
        tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), DateHelper.formatDate(taskData.getExecuteTime()))));
        //延后1分钟执行
        tTask.setExecuteTime(DateHelper.add(taskData.getExecuteTime(),Calendar.MINUTE , 1));
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        tTask.setExecuteCount(0);

        logger.info("executeWorkGridManagerScanTask-taskSiteData={}", JsonHelper.toJson(taskData));
        taskService.doAddTask(tTask, false);
	}

	@Override
	public JdCResponse<Boolean> scanTaskPosition(ScanTaskPositionRequest request) {
		JdCResponse<Boolean> result = new JdCResponse<Boolean>();
		result.setData(Boolean.FALSE);
		result.toSucceed("扫描成功！");
		if(StringUtils.isBlank(request.getScanPositionCode())) {
			result.toFail("扫描的网格码不能为空！");
			return result;
		}
		if(StringUtils.isBlank(request.getTaskRefGridKey())) {
			result.toFail("任务网格码不能为空！");
			return result;
		}

		Result<PositionData> positionRecord = this.positionManager.queryPositionByGridKey(request.getTaskRefGridKey());
		if(positionRecord == null
				|| positionRecord.getData() == null) {
			result.toFail("任务网格码无效！");
			return result;
		}
		if(!request.getScanPositionCode().equals(positionRecord.getData().getPositionCode())) {
			WorkStationGridQuery checkQuery = new WorkStationGridQuery();
			checkQuery.setBusinessKey(request.getTaskRefGridKey());
			Result<WorkStationGrid> gridResult = this.workStationGridManager.queryByGridKey(checkQuery);
			if(gridResult == null
					|| gridResult.getData() == null) {
				result.toFail("任务网格无效！");
				return result;
			}
			result.toFail("请扫描#"+gridResult.getData().getGridName()+"#的网格码！");
			return result;
		}else {
			result.setData(Boolean.TRUE);
		}
		return result;
	}
}
