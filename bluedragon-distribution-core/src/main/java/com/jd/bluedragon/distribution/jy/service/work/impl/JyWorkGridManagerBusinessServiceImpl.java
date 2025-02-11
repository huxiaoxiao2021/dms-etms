package com.jd.bluedragon.distribution.jy.service.work.impl;


import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.jd.bluedragon.common.dto.work.*;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.jy.dto.work.*;
import com.jd.bluedragon.distribution.jy.manager.IQuotaTargetConfigManager;
import com.google.common.base.Objects;
import com.jd.bluedragon.core.jsf.work.ScheduleJSFServiceManager;
import com.jd.bluedragon.distribution.jy.service.work.*;
import com.jd.bluedragon.distribution.jy.work.enums.WorkCheckResultEnum;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskTypeEnum;
import com.jd.bluedragon.distribution.station.domain.BaseUserSignRecordVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.distribution.station.service.UserSignRecordService;
import com.jd.bluedragon.distribution.workingConfig.WorkingConfigQueryService;
import com.jd.bluedragon.utils.easydata.OneTableEasyDataConfig;
import com.jd.dms.wb.report.sdk.model.vo.working.data.SupplierVO;
import com.jd.dms.wb.sdk.enums.oneTable.BusinessTypeEnum;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.easydata.DmsWEasyDataConfig;
import com.jd.bluedragon.utils.easydata.EasyDataClientUtil;
import com.jd.dms.wb.sdk.enums.oneTable.Class2TypeEnum;
import com.jd.fds.lib.dto.server.FdsPage;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.user.JyThirdpartyUser;
import com.jdl.basic.api.domain.user.JyTpUserScheduleQueryDto;
import com.jdl.basic.api.domain.user.JyUser;
import com.jdl.basic.api.domain.work.WorkGridCandidate;
import com.jdl.basic.api.enums.WorkGridManagerTaskBizType;
import com.jdl.basic.api.service.work.WorkGridCandidateJsfService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.ScheduleDetailDto;
import com.jdl.jy.flat.dto.schedule.UserDateScheduleQueryDto;
import com.jdl.jy.flat.dto.schedule.UserScheduleDto;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.terracotta.statistics.jsr166e.ThreadLocalRandom;

import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.AttachmentDetailData;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskConfigJsfManager;
import com.jd.bluedragon.core.jsf.work.WorkGridManagerTaskJsfManager;
import com.jd.bluedragon.core.jsf.workStation.JyUserManager;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.SysConfigContent;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.enums.EditTypeEnum;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.jy.work.enums.WorkTaskStatusEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.work.WorkGridManagerTask;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfig;
import com.jdl.basic.api.domain.work.WorkGridManagerTaskConfigVo;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkGridModifyMqData;
import com.jdl.basic.api.domain.workStation.WorkGridQuery;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.api.enums.FrequencyTypeEnum;
import com.jdl.basic.api.enums.WorkFinishTypeEnum;
import com.jdl.basic.common.utils.Result;

import static com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentSubBizTypeEnum.TASK_WORK_GRID_MANAGER_IMPROVE;
import static com.jd.bluedragon.distribution.jy.service.work.impl.JyWorkGridManagerCaseServiceImpl.CASE_ZHIBIAO_QITA_ITEM_CODE;
import static com.jd.bluedragon.distribution.station.enums.JobTypeEnum.*;
import static com.jdl.basic.api.enums.WorkGridManagerTaskBizType.KPI_IMPROVE;

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
	//丢失一表通 发货扫描率指标key
	private static final String SEND_SCAN_QUOTA_CODE="dis000034";
	// 指标改善任务场地数量 sysconfig配置
	private static final String KPI_IMPROVE_TASK_SITE_NUM_KEY = "kpi.improve.task.site.num";
	//任务责任人-人员签到数据开始时间查询偏移量 sysconfig配置
	private static final String SIGN_DATE_START_OFFSET = "sign.date.start.offset";

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
	private WorkGridManager workGridManager;

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
	@Autowired
	private WorkGridCandidateJsfService workGridCandidateJsfService;

	@Autowired
	private ScheduleJSFServiceManager scheduleJSFServiceManager;


    @Autowired
    SysConfigService sysConfigService;
    @Autowired
    private BaseMajorManager baseMajorManager;
	@Autowired
	protected EasyDataClientUtil easyDataClientUtil;
	@Autowired
	private DmsWEasyDataConfig dmsWEasyDataConfig;

	@Autowired
	private OneTableEasyDataConfig oneTableEasyDataConfig;

	@Autowired
	private IQuotaTargetConfigManager iQuotaTargetConfigManager;
	
	@Autowired
	private UserSignRecordService userSignRecordService;
	
	@Autowired
	private WorkingConfigQueryService workingConfigQueryService;
	
	@Autowired
	private JyWorkGridManagerResponsibleInfoService jyWorkGridManagerResponsibleInfoService;
    /**
     * 任务提前执行时间 （单位：秒）
     */
    @Value("${beans.jyWorkGridManagerBusinessService.taskBeforeExcuteTimes:1800}")
	private int taskBeforeExcuteTimes;
    /**
     * 站点任务提前执行时间 （单位：秒）
     */
    @Value("${beans.jyWorkGridManagerBusinessService.siteTaskBeforeExcuteTimes:900}")
	private int siteTaskBeforeExcuteTimes;
    /**
     * 任务提前执行时间-随机范围 （单位：秒）
     */
    @Value("${beans.jyWorkGridManagerBusinessService.rangeMaxSeconds:300}")
	private int rangeMaxSeconds;
    /**
     * 自动关闭任务延迟执行时间 （单位：秒）
     */
    @Value("${beans.jyWorkGridManagerBusinessService.closeTaskAfterExcuteTimes:30}")
	private int closeTaskAfterExcuteTimes;
    /**
     * 任务提前执行时间-随机范围 （单位：秒）
     */
    @Value("${beans.jyWorkGridManagerBusinessService.closeTaskRangeMaxSeconds:30}")
	private int closeTaskRangeMaxSeconds;
	/**
	 * 指标任务-发生扫描率 查询指标倒数的场地数量
	 */
	@Value("${jyWorkGridManager.lostOneTable.sendQuotaNum:20}")
	private int lostOneTableSendQuotaNum;

    private static final String UMP_KEY_PREFIX = "dmsWeb.beans.jyWorkGridManagerBusinessService.";

	@Autowired
	@Qualifier("violentSortingResponsibleInfoProducer")
	private DefaultJMQProducer violentSortingResponsibleInfoProducer;

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "submitData",mState={JProEnum.TP,JProEnum.FunctionError})
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
		//非待处理状态，不能提交
		if(!WorkTaskStatusEnum.TODO.getCode().equals(oldData.getStatus())) {
			result.toFail("任务状态已变更，不能提交！");
			return result;
		}
		//转派的任务检查处理人在不在备选池中
		if (oldData.getTransfered().intValue() == 1){
			List<WorkGridCandidate> workGridCandidates = workGridCandidateJsfService.queryCandidateList(oldData.getSiteCode());
			List<String> erpList = Lists.newArrayList();
			if (!com.jd.ldop.utils.CollectionUtils.isEmpty(workGridCandidates)){
				workGridCandidates.stream().forEach(item->{
					erpList.add(item.getErp());
				});
				if (!erpList.contains(userErp)){
					result.toFail("该ERP权限不足");
					return result;
				}
			}
		}
		
		//暴力分拣任务责任人信息校验
		result = jyWorkGridManagerResponsibleInfoService.checkResponsibleInfo(taskData, result);
		if(result.isFail()){
			return result;
		}
		
		JyBizTaskWorkGridManager updateTaskData = new JyBizTaskWorkGridManager();
		updateTaskData.setStatus(WorkTaskStatusEnum.COMPLETE.getCode());
		updateTaskData.setHandlerPositionCode("");
		updateTaskData.setHandlerUserName(userName);
		updateTaskData.setUpdateUser(userErp);
		updateTaskData.setUpdateUserName(userName);
		updateTaskData.setUpdateTime(currentTime);
		updateTaskData.setProcessEndTime(currentTime);
		updateTaskData.setId(oldData.getId());
		List<JyAttachmentDetailEntity> addAttachmentList = new ArrayList<>();
		//指标改进附件
		List<JyAttachmentDetailEntity> improveAttachmentList = new ArrayList<>();
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
					if(attachmentData == null) {
						continue;
					}
					addAttachmentList.add(toJyAttachmentDetailEntity(userErp,currentTime,taskData,caseData,
							attachmentData,caseData.getCaseCode()));
				}
			}
			//改善反馈附件,指标改善任务 只有1个case, 附件bizSubType为TASK_WORK_GRID_MANAGER_IMPROVE.code
			WorkGridManagerTaskBizType taskBizType = WorkGridManagerTaskBizType.getEnum(oldData.getTaskBizType());
			if(!CollectionUtils.isEmpty(caseData.getImproveAttachmentList())) {
				String subBizType = TASK_WORK_GRID_MANAGER_IMPROVE.getCode();
				if(!KPI_IMPROVE.equals(taskBizType)){
					subBizType += ("|" + caseData.getCaseCode());
				}
				for(AttachmentDetailData attachmentData : caseData.getImproveAttachmentList()) {
					if(attachmentData == null) {
						continue;
					}
					improveAttachmentList.add(toJyAttachmentDetailEntity(userErp,currentTime,taskData,caseData,attachmentData,
							subBizType));
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
		//巡检任务表增加是否匹配字段  0-未选择,1-符合 2-不符合
		reportAddMatchField(taskData, updateTaskData);

		jyWorkGridManagerCaseService.batchInsert(addCase);
		jyWorkGridManagerCaseService.batchUpdate(updateCase);
		jyWorkGridManagerCaseItemService.batchInsert(addCaseItem);
		jyAttachmentDetailService.batchInsert(addAttachmentList);
		jyAttachmentDetailService.batchInsert(improveAttachmentList);
		jyBizTaskWorkGridManagerService.finishTask(updateTaskData);
		//保存任务责任人信息
		jyWorkGridManagerResponsibleInfoService.saveTaskResponsibleInfo(oldData, taskData.getResponsibleInfo());
		//暴力分拣任务 发送责任人信息
		jyWorkGridManagerResponsibleInfoService.sendViolentSortingResponsibleInfo(oldData, taskData.getResponsibleInfo());
		return result;
	}
	

	/**
	 * 巡检任务表增加是否匹配字段
	 * TaskType=3时 0-未选择,1-符合 2-不符合    TaskType=1或者2时  1-符合
	 *
	 * @param taskData
	 * @param updateTaskData
	 */
	private void reportAddMatchField(JyWorkGridManagerData taskData, JyBizTaskWorkGridManager updateTaskData) {
		if (WorkTaskTypeEnum.WORKING.getCode().equals(taskData.getTaskType())) {
			if (!CollectionUtils.isEmpty(taskData.getCaseList())) {
				List<Integer> resultList = taskData.getCaseList().stream().map(JyWorkGridManagerCaseData::getCheckResult).collect(Collectors.toList());
				//任务中有一个不符合      不符合
				//任务中有符合和未选择    不符合
				//任务中全是符合         符合
				//任务中全是未选择       未选择
				if (resultList.contains(WorkCheckResultEnum.UNPASS.getCode())) {
					updateTaskData.setIsMatch(WorkCheckResultEnum.UNPASS.getCode());
				} else if (resultList.contains(WorkCheckResultEnum.UNDO.getCode()) && resultList.contains(WorkCheckResultEnum.PASS.getCode())) {
					updateTaskData.setIsMatch(WorkCheckResultEnum.UNPASS.getCode());
				} else if (!resultList.contains(WorkCheckResultEnum.UNDO.getCode()) && !resultList.contains(WorkCheckResultEnum.UNPASS.getCode())) {
					updateTaskData.setIsMatch(WorkCheckResultEnum.PASS.getCode());
				} else if (!resultList.contains(WorkCheckResultEnum.PASS.getCode()) && !resultList.contains(WorkCheckResultEnum.UNPASS.getCode())) {
					updateTaskData.setIsMatch(WorkCheckResultEnum.UNDO.getCode());
				}
			}
		}
		if (WorkTaskTypeEnum.MEETING.getCode().equals(taskData.getTaskType()) || WorkTaskTypeEnum.MEETING_RECORD.getCode().equals(taskData.getTaskType())) {
			updateTaskData.setIsMatch(WorkCheckResultEnum.PASS.getCode());
		}
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
		caseEntity.setImproveEndTime(caseData.getImproveEndTime());
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
		caseEntity.setImproveEndTime(caseData.getImproveEndTime());
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
		caseItemEntity.setFeedBackContent(caseItem.getFeedbackContent());
		if(CASE_ZHIBIAO_QITA_ITEM_CODE.equals(caseItem.getCaseItemCode())){
			caseItemEntity.setUserDefinedTitle(caseItem.getUserDefinedTitle());
		}
		return caseItemEntity;
	}

	private JyAttachmentDetailEntity toJyAttachmentDetailEntity(String userErp, Date currentTime,
																JyWorkGridManagerData taskData,
																JyWorkGridManagerCaseData caseData,
																AttachmentDetailData attachmentData,
																String bizSubType) {
		JyAttachmentDetailEntity attachmentEntity = new JyAttachmentDetailEntity();
        attachmentEntity.setBizId(caseData.getBizId());
        attachmentEntity.setSiteCode(taskData.getSiteCode());
        attachmentEntity.setBizType(JyAttachmentBizTypeEnum.TASK_WORK_GRID_MANAGER.getCode());
        attachmentEntity.setBizSubType(bizSubType);
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
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "startWorkGridManagerScanTask",mState={JProEnum.TP,JProEnum.FunctionError})
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
				updateTask.setExecuteTime(DateHelper.getBeforeTime(taskData.getExecuteTime(), taskBeforeExcuteTimes, rangeMaxSeconds));
				updateTask.setId(tTask.getId());
		        taskService.updateBySelectiveWithBody(updateTask);
			}
		}else {
			//新增任务
	        addWorkGridManagerScanTask(taskData);
		}
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
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "executeWorkGridManagerScanTask",mState={JProEnum.TP,JProEnum.FunctionError})
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
		int pageNum = 1;
		List<Integer> siteCodeList = null;
		do {
			TaskScanSiteInfo taskScanSiteInfo = getSiteCodeList(configData, pageNum);
			if(taskScanSiteInfo == null || CollectionUtils.isEmpty(taskScanSiteInfo.getSiteCodes())){
				break;
			}
			siteCodeList = taskScanSiteInfo.getSiteCodes();
			Map<Integer, BusinessQuotaInfoData> businessQuotaInfoDataMap = taskScanSiteInfo.getBusinessQuotaInfoDataMap();
			for(Integer siteCode : siteCodeList) {
				TaskWorkGridManagerSiteScanData taskData = new TaskWorkGridManagerSiteScanData();
				taskData.setTaskConfigCode(configData.getTaskConfigCode());
				taskData.setSiteCode(siteCode);
				taskData.setTaskBatchCode(String.format("%s_%s_%s",configData.getTaskConfigCode(),siteCode,DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)));
				taskData.setExecuteTime(getExecuteTime(configData));
				if(businessQuotaInfoDataMap != null && businessQuotaInfoDataMap.get(siteCode) != null){
					taskData.setBusinessQuotaInfoData(businessQuotaInfoDataMap.get(siteCode));
				}
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
						updateTask.setExecuteTime(DateHelper.getBeforeTime(taskData.getExecuteTime(), siteTaskBeforeExcuteTimes, rangeMaxSeconds));
						updateTask.setId(tTask.getId());
				        taskService.updateBySelectiveWithBody(updateTask);
					}
				}else {
					//新增任务
			        this.addWorkGridManagerSiteScanTask(taskData, null);
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

	private TaskScanSiteInfo getSiteCodeList(WorkGridManagerTaskConfigVo configData, int pageNum) {
		TaskScanSiteInfo scanSiteInfo = new TaskScanSiteInfo();
		List<Integer> siteCodeList = Collections.EMPTY_LIST;
		scanSiteInfo.setSiteCodes(siteCodeList);
		String taskCode = configData.getTaskCode();
		Result<WorkGridManagerTask> taskResult = workGridManagerTaskJsfManager.queryByTaskCode(taskCode);
		if(taskResult == null || taskResult.getData() == null){
			logger.error("根据任务编码未查到任务定义信息，taskcode:{}", taskCode);
			return scanSiteInfo;
		}
		WorkGridManagerTask managerTask = taskResult.getData();
		WorkStationGridQuery workStationGridQuery = new WorkStationGridQuery();
		workStationGridQuery.setAreaCodeList(configData.getWorkAreaCodeList());
		workStationGridQuery.setPageSize(100);
		workStationGridQuery.setPageNumber(pageNum);
		WorkGridManagerTaskBizType bizTypeEnum = WorkGridManagerTaskBizType.getEnum(managerTask.getTaskBizType());
		if(bizTypeEnum == null){
			logger.error("根据任务bizType未查到枚举，bizType:{}", managerTask.getTaskBizType());
			return scanSiteInfo;
		}
		switch (bizTypeEnum){
			case DAILY_PATROL:
				siteCodeList =  workStationGridManager.querySiteListForManagerScan(workStationGridQuery);
				scanSiteInfo.setSiteCodes(siteCodeList);
				return scanSiteInfo;
			case KPI_IMPROVE:
				return getSiteCodesFromLoseOneTable(pageNum, lostOneTableSendQuotaNum);
		}
		scanSiteInfo.setSiteCodes(siteCodeList);
		return scanSiteInfo;

	}
	private TaskScanSiteInfo getSiteCodesFromLoseOneTable(int pageNum, int pageSize){
		TaskScanSiteInfo scanSiteInfo = new TaskScanSiteInfo();
		List<Integer> siteCodes = new ArrayList<>();
		scanSiteInfo.setSiteCodes(siteCodes);
		//指标业务只查一页
		if(pageNum > 1){
			return scanSiteInfo;
		}
		Map<String, Object> queryParam = new HashMap<>();
		queryParam.put("dt", DateHelper.formatDate(DateHelper.getZeroFromDay(new Date(), 2)));
		//发货扫描率编码指标名称
		queryParam.put("quotaCode", SEND_SCAN_QUOTA_CODE);
		//发货指标
		String year = DateHelper.formatDate(DateHelper.getZeroFromDay(new Date(), 2), "yyyyy");
		Double target = iQuotaTargetConfigManager.getLostOneTableSendScanTarget(SEND_SCAN_QUOTA_CODE,year,
				String.valueOf(BusinessTypeEnum.SORT.getCode()), Class2TypeEnum.LOST.getCode());
		String targetStr = "";
		if(target != null){
			queryParam.put("target", target/100);
			targetStr = String.format("%.2f",target) + "%";
		}
		Integer limit = 20;
		SysConfig kpiImproveTaskSiteNumConfig = sysConfigService.findConfigContentByConfigName(KPI_IMPROVE_TASK_SITE_NUM_KEY);
		if(kpiImproveTaskSiteNumConfig != null && org.apache.commons.lang3.StringUtils.isNumeric(kpiImproveTaskSiteNumConfig.getConfigContent())){
			limit = Integer.parseInt(kpiImproveTaskSiteNumConfig.getConfigContent());
		}

		//调用easydata
		FdsPage edresult = easyDataClientUtil.query(oneTableEasyDataConfig.getQueryLostOneTableSite(), queryParam,
				oneTableEasyDataConfig.getApiGroupName(),oneTableEasyDataConfig.getAppToken(),
				limit,pageNum - 1,
				oneTableEasyDataConfig.getTenant());
		if(edresult == null || CollectionUtils.isEmpty(edresult.getContent())){
			logger.info("查询一表通指标倒数的场地和指标达成-未查到,queryParam:{},limit:{},pageNum:{}", JsonHelper.toJson(queryParam),
					limit, pageNum -1);
			return scanSiteInfo;
		}

		Map<Integer, BusinessQuotaInfoData> businessQuotaInfoDataMap = new HashMap<>();
		scanSiteInfo.setBusinessQuotaInfoDataMap(businessQuotaInfoDataMap);
		String quotaAchieveInfo = DateHelper.formatDate(DateHelper.getZeroFromDay(new Date(), 2), "MM/dd") + "未达标";
		for(Object map : edresult.getContent()){
			Map data = ((Map)map);
			Object obSiteCode = data.get("siteCode");
			if(obSiteCode == null){
				continue;
			}
			Integer siteCode = Integer.valueOf(obSiteCode.toString());
			siteCodes.add(siteCode);
			if(data.containsKey("actual") &&
					org.apache.commons.lang3.StringUtils.isNotBlank(String.valueOf(data.get("actual")))){
				BusinessQuotaInfoData businessQuotaInfoData = new BusinessQuotaInfoData();
				businessQuotaInfoData.setTarget(targetStr);
				Double actual = Double.parseDouble(data.get("actual").toString());
				actual = actual * 100;
				businessQuotaInfoData.setActual(String.format("%.2f",actual) + "%");
				businessQuotaInfoData.setQuotaAchieveInfo(quotaAchieveInfo);
				businessQuotaInfoDataMap.put(siteCode, businessQuotaInfoData);
			}
		}
		logger.info("查询一表通指标倒数的场地和指标达成-,queryParam:{},查到场地：{}", JsonHelper.toJson(queryParam),
				JsonHelper.toJson(siteCodes));
		return scanSiteInfo;
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = UMP_KEY_PREFIX + "executeWorkGridManagerSiteScanTask",mState={JProEnum.TP,JProEnum.FunctionError})
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
		if(!checkSiteSwitch(siteCode)) {
			logger.warn("场地【{}】未开启推送任务！",siteCode);
			return true;
		}
		BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
		if(siteInfo == null) {
			logger.warn("场地【{}】在青龙基础资料不存在！",siteCode);
			return true;
		}
		Result<WorkGridManagerTask> taskInfoResult = workGridManagerTaskJsfManager.queryByTaskCode(configData.getTaskCode());
		if(taskInfoResult == null
				|| taskInfoResult.getData() == null) {
			Log.warn("startWorkGridManagerScanTask-fail! taskCode={}",configData.getTaskCode());
			return false;
		}
		WorkGridManagerTask taskInfo = taskInfoResult.getData();
		boolean hasGridData = false;
		boolean needInitTaskData = false;
		boolean needDistribution = true;
		boolean endBatchCode = false;
		String oldBatchCode = taskWorkGridManagerScan.getTaskBatchCode();
		boolean changeBatchCode = false;
		int initTaskNum = 0;
		int pageNum = 1;
		Date curDate = new Date();
		Date taskTime = taskWorkGridManagerScan.getExecuteTime();
		Date taskEndTime = DateHelper.addDate(taskTime, 1);
		if(WorkFinishTypeEnum.ONE_WEEK.getCode().equals(configData.getFinishType())) {
			taskEndTime = DateHelper.addDate(taskTime, 7);
		}
		//场地下存在需要推送的网格
		List<WorkGrid> gridList = queryListForManagerSiteScan(siteCode, configData.getWorkAreaCodeList(), pageNum,
				taskInfo.getTaskBizType(), configData.getPerGridNum());
		if(!CollectionUtils.isEmpty(gridList)) {
			hasGridData = true;
		}else {
			//批次完结，取消无用的任务
			endBatchCode(oldBatchCode);
			logger.warn("任务分配失败，场地【{}】待推送网格数据为空！",siteCode);
			return true;
		}

		//判断是否需要初始化数据
		if(taskWorkGridManagerScan.getExecuteCount() == 0) {
			//首次执行，需要初始化
			needInitTaskData = true;
		}else {
			//判断是否有待分配的网格
			JyBizTaskWorkGridManagerQuery checkQuery = new JyBizTaskWorkGridManagerQuery();
			checkQuery.setTaskBatchCode(taskWorkGridManagerScan.getTaskBatchCode());
			checkQuery.setAreaCodeList(configData.getWorkAreaCodeList());
			checkQuery.setStatus(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());
			checkQuery.setLimit(1);
			List<JyBizTaskWorkGridManager> toDistributionTaskCheckList = jyBizTaskWorkGridManagerService.queryDataListForDistribution(checkQuery);
			if(CollectionUtils.isEmpty(toDistributionTaskCheckList)) {
				//重新初始化
				needInitTaskData = true;
				taskWorkGridManagerScan.setTaskBatchCode(String.format("%s_%s_%s",configData.getTaskConfigCode(),siteCode,DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)));
				//完结旧批次
				endBatchCode(oldBatchCode);
			}
		}
		if(StringUtil.isBlank(siteInfo.getOrganizationCode())) {
			needDistribution = false;
			logger.warn("任务分配失败，场地【{}】未绑定人资组织机构！",siteCode);
			return true;
		}
		List<JyUserDto> userList = getTaskHandleUser(configData, siteInfo, taskInfo.getTaskBizType());
		//查询网格排班,校验用户是否在班
		userList = filterJyUserDtoInSchedule(configData.getTaskConfigCode(),taskTime,taskEndTime,userList);

		int needGridNum = userList.size() * configData.getPerGridNum();
		if(needGridNum == 0) {
			needDistribution = false;
			logger.warn("任务分配失败，场地【{}】岗位【{}】人员为空！",siteCode,configData.getHandlerUserPositionName());
			return true;
		}
		//初始化网格数据
		if(needInitTaskData) {
			logger.info("初始化任务数据：batchCode={},executeTime={}",taskWorkGridManagerScan.getTaskBatchCode(),DateHelper.formatDateTime(taskWorkGridManagerScan.getExecuteTime()));
			while(!CollectionUtils.isEmpty(gridList)) {
				List<JyBizTaskWorkGridManager> jyTaskInitList = new ArrayList<>();
				for(WorkGrid grid: gridList) {
					jyTaskInitList.add(this.initJyBizTaskWorkGridManager(siteInfo,taskWorkGridManagerScan, taskInfo, configData, grid, curDate));
					initTaskNum ++;
				}
				pageNum ++;
				gridList = queryListForManagerSiteScan(siteCode, configData.getWorkAreaCodeList(), pageNum,
						taskInfo.getTaskBizType(), configData.getPerGridNum());
				jyBizTaskWorkGridManagerService.batchAddTask(jyTaskInitList);
			}
		}
		//判断上次执行时间和这次是否同一天,改成下个执行周期时间
		if(DateHelper.isSameDay(taskWorkGridManagerScan.getExecuteTime(),taskWorkGridManagerScan.getLastExecuteTime())) {
			needDistribution = false;
			logger.warn("本次不执行任务分配，本次执行时间和上次执行时间相同！");
		}
		if(needDistribution) {
			JyBizTaskWorkGridManagerQuery query = new JyBizTaskWorkGridManagerQuery();
			query.setTaskBatchCode(taskWorkGridManagerScan.getTaskBatchCode());
			query.setAreaCodeList(configData.getWorkAreaCodeList());
			query.setStatus(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());
			//多查询一个网格用于判断是否换批次
			query.setLimit(needGridNum+1);
			List<JyBizTaskWorkGridManager> toDistributionTaskList = jyBizTaskWorkGridManagerService.queryDataListForDistribution(query);
			this.doDistributionTask(taskWorkGridManagerScan, taskInfo, configData, userList, toDistributionTaskList, needGridNum, curDate, taskTime, taskEndTime);
			//网格已分配完成，完结当前批次，生成新批次任务
			if(toDistributionTaskList.size() <= needGridNum) {
				changeBatchCode = true;
				endBatchCode(taskWorkGridManagerScan.getTaskBatchCode());
			}
		}
		//新增下次执行时间的任务
		TaskWorkGridManagerSiteScanData taskData = new TaskWorkGridManagerSiteScanData();
		taskData.setTaskConfigCode(configData.getTaskConfigCode());
		taskData.setSiteCode(siteCode);

		taskData.setExecuteTime(getNextExecuteTime(taskWorkGridManagerScan.getExecuteTime(),configData));
		if(changeBatchCode) {
			taskData.setTaskBatchCode(String.format("%s_%s_%s",configData.getTaskConfigCode(),siteCode,DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)));
			taskData.setExecuteCount(0);
		}else {
			taskData.setTaskBatchCode(taskWorkGridManagerScan.getTaskBatchCode());
			taskData.setExecuteCount(taskWorkGridManagerScan.getExecuteCount() + 1);
		}
		taskData.setLastExecuteTime(taskWorkGridManagerScan.getExecuteTime());
		addWorkGridManagerSiteScanTask(taskData, taskInfo.getTaskBizType());
		logger.info("新增下次执行时间的任务：batchCode={},executeTime={}",taskData.getTaskBatchCode(),DateHelper.formatDateTime(taskData.getExecuteTime()));
		return true;
	}
	@Override
	public List<JyUserDto> getTaskHandleUser(WorkGridManagerTaskConfigVo configData, BaseSiteInfoDto siteInfo,
											 Integer taskBizType){
		WorkGridManagerTaskBizType bizTypeEnum = WorkGridManagerTaskBizType.getEnum(taskBizType);
		switch (bizTypeEnum){
			case DAILY_PATROL:
			case EXP_INSPECT:
			case KPI_IMPROVE:
				List<JyUserDto> userDtos = getUserList(siteInfo.getSiteCode(),siteInfo.getOrganizationCode(),
						configData.getHandlerUserPositionCode(),configData.getHandlerUserPositionName());
				return sortUserListByLast(userDtos, configData.getTaskCode(), siteInfo.getSiteCode());
		}
		logger.error("获取任务处理人失败，无匹配方法");
		return null;

	}

	private List<WorkGrid> queryListForManagerSiteScan(Integer siteCode, List<String> areaCodeList, Integer pageNum,
													   Integer taskBizType, Integer perGridNum){
		//查询网格场地
		WorkGridQuery workGridQuery = new WorkGridQuery();
		workGridQuery.setSiteCode(siteCode);
		workGridQuery.setAreaCodeList(areaCodeList);
		workGridQuery.setPageSize(100);
		workGridQuery.setPageNumber(pageNum);
		WorkGridManagerTaskBizType bizTypeEnum = WorkGridManagerTaskBizType.getEnum(taskBizType);
		if(bizTypeEnum == null){
			logger.error("查询任务实例推送网格-根据任务bizType未查到枚举，bizType:{}", taskBizType);
			return null;
		}
		switch (bizTypeEnum){
			case DAILY_PATROL:
				return workGridManager.queryListForManagerSiteScan(workGridQuery);
			case KPI_IMPROVE:
				return getWorkGridByLoadCarQualityReport(areaCodeList, siteCode, perGridNum, pageNum);
		}
		logger.error("查询任务实例推送网格-根据任务bizType:{}未查到处理方法", taskBizType);
		return null;
	}

	@Override
	public List<JyUserDto> filterJyUserDtoInSchedule(String taskConfigCode, Date taskTime, Date taskEndTime, List<JyUserDto> userList) {
		if (CollectionUtils.isEmpty(userList)){
			return userList;
		}
		List<JyUserDto> result = Lists.newArrayList();
		for (JyUserDto user:userList){
			//erp
			List<String> userErpList = Lists.newArrayList();
			userErpList.add(user.getUserErp());
			//任务时间区间
			String start = DateHelper.formatDate(taskTime);
			String end = DateHelper.formatDate(taskEndTime);
			List<String> scheduleDateList = Lists.newArrayList();
			scheduleDateList.add(start);
			if (!Objects.equal(start,end)){
				scheduleDateList.add(end);
			}

			UserDateScheduleQueryDto dto = new UserDateScheduleQueryDto();
			dto.setUserErpList(userErpList);
			dto.setScheduleDateList(scheduleDateList);
			ServiceResult<UserScheduleDto> userScheduleDtoServiceResult = scheduleJSFServiceManager.listUserDateSchedule(dto);
			if (userScheduleDtoServiceResult == null
					|| !userScheduleDtoServiceResult.getSuccess()
					|| userScheduleDtoServiceResult.getData() == null
					|| CollectionUtils.isEmpty(userScheduleDtoServiceResult.getData().getScheduleDeatilDtoList())){
				logger.warn("查询不到排班信息，任务编号{}过滤用户{}",taskConfigCode,user.getUserErp());
				continue;
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(com.jdl.basic.common.utils.DateHelper.DATE_FORMAT_YYYY_MM_DD);
			Instant instant1 = taskTime.toInstant();
			LocalDateTime startDateTime1 = LocalDateTime.ofInstant(instant1, ZoneId.systemDefault());
			Instant instant2 = taskEndTime.toInstant();
			LocalDateTime endDateTime1 = LocalDateTime.ofInstant(instant2, ZoneId.systemDefault());

			List<ScheduleDetailDto> scheduleDeatilDtoList = userScheduleDtoServiceResult.getData().getScheduleDeatilDtoList();
			for (ScheduleDetailDto scheduleDate:scheduleDeatilDtoList){
				String timeRange2 =scheduleDate.getStartTime()+"-"+scheduleDate.getEndTime();
				LocalDate localDate2 = LocalDate.parse(scheduleDate.getScheduleDate(), formatter);
				if (hasIntersection(startDateTime1,endDateTime1,localDate2,timeRange2)){
					result.add(user);
					break;
				}
			}
			if (result.contains(user.getUserErp())){
				logger.info("任务taskConfigCode"+taskConfigCode+"分配erp："+user.getUserErp()+"满足排班");
			}else{
				logger.info("任务taskConfigCode"+taskConfigCode+"分配erp："+user.getUserErp()+"不满足排班");
			}
		}
		return result;
	}

	/**
	 * 判断两个时间区间是否有交叉/交集
	 * @param timeRange2
	 * @return
	 */
	public static boolean hasIntersection(LocalDateTime startDateTime1,LocalDateTime endDateTime1,LocalDate date2, String timeRange2) {
		String[] range2 = timeRange2.split("-");
		if (StringUtils.isEmpty(range2[0]) || "null".equals(range2[0]) || StringUtils.isEmpty(range2[1]) || "null".equals(range2[1]) ){
			return true;
		}
		LocalTime startTime2 = LocalTime.parse(range2[0]);
		LocalTime endTime2 = LocalTime.parse(range2[1]);

		// 将时间转换为带日期的 LocalDateTime
		LocalDateTime startDateTime2 = LocalDateTime.of(date2, startTime2);
		LocalDateTime endDateTime2 = LocalDateTime.of(date2, endTime2);

		if (!startDateTime2.isBefore(endDateTime2)) {
			endDateTime2 = endDateTime2.plusDays(1);
		}

		// 判断是否有交叉
		return !(endDateTime1.isBefore(startDateTime2) || endDateTime2.isBefore(startDateTime1));
	}

	/**
     * 判断是否开启站点推送
     * @param siteCode
     * @return
     */
    private boolean checkSiteSwitch(Integer siteCode){
        //判断是否进行过组板，如果已经组板则从板中取消，并发送取消组板的全称跟踪
        SysConfigContent content = sysConfigService.getSysConfigJsonContent(Constants.SYS_CONFIG_WORK_GRID_MANAGER_SITES);
        if (content != null) {
            if (Boolean.TRUE.equals(content.getMasterSwitch())
            		|| (content.getSiteCodes() != null && content.getSiteCodes().contains(siteCode))) {
                return true;
            }
            return false;
        }
        return true;
    }
	/**
	 * 分配成功的，发送咚咚通知
	 * @param user
	 */
	@Override
	public void sendTimeLineNotice(WorkGridManagerTaskBizType type, JyUserDto user) {
		//发送咚咚通知
        String title = type.getName() + "通知";
		String area = "管理者";
		if (type.getCode().intValue() == WorkGridManagerTaskBizType.MONITOR_PATROL.getCode()){
			area = "监控区";
		}
        String content = "您已收到场地【"+type.getName()+"】，请进入拣运APP，扫描"+area+"网格码，查看任务，并按时完成，逾期将记录在册";
        List<String> erpList = Lists.newArrayList();
        erpList.add(user.getUserErp());
		logger.info("分配任务完成，发送咚咚通知：{} 标题：{} 内容：{}",user.getUserErp(),title,content);
        NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);
	}
	/**
	 * 任务分配操作
	 * @param taskWorkGridManagerScan
	 * @param taskInfo
	 * @param configData
	 * @param userList
	 * @param jyTaskList
	 * @param needGridNum
	 * @param curDate
	 * @param taskTime
	 * @param taskEndTime
	 */
	private void doDistributionTask(TaskWorkGridManagerSiteScanData taskWorkGridManagerScan,WorkGridManagerTask taskInfo,WorkGridManagerTaskConfigVo configData,
		List<JyUserDto> userList,List<JyBizTaskWorkGridManager> jyTaskList,int needGridNum,
	 Date curDate,Date taskTime,Date taskEndTime){
		String taskBatchCode = taskWorkGridManagerScan.getTaskBatchCode();
		int gridIndex = 0 ;
		boolean finishTask = false;
		List<String> bizIdListAutoClose = new ArrayList<>();
		for(JyUserDto user : userList) {
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
				jyTask.setHandlerErp(user.getUserErp());
				jyTask.setHandlerUserName(user.getUserName());
				jyTask.setPreFinishTime(taskEndTime);
				jyTask.setStatus(WorkTaskStatusEnum.TODO.getCode());
				jyTask.setTaskDate(taskTime);
				gridIndex++;
				distributionData.setData(jyTask);
			}
			jyBizTaskWorkGridManagerService.distributionTask(distributionData);
			//发送咚咚通知
			if(bizIdUserList.size() > 0) {
				sendTimeLineNotice(WorkGridManagerTaskBizType.getEnum(taskInfo.getTaskBizType()),user);
			}
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
	/**
	 * 完结一个任务
	 */
	private void endBatchCode(String batchCode) {
		logger.info("完结一个任务批次：batchCode={}",batchCode);
		JyBizTaskWorkGridManagerBatchUpdate closeData = new JyBizTaskWorkGridManagerBatchUpdate();
		JyBizTaskWorkGridManager data = new JyBizTaskWorkGridManager();
		closeData.setData(data);
		data.setStatus(WorkTaskStatusEnum.CANCEL.getCode());
		data.setUpdateTime(new Date());
		data.setUpdateUser(DmsConstants.SYS_AUTO_USER_CODE);
		data.setUpdateUserName(DmsConstants.SYS_AUTO_USER_CODE);
		data.setTaskBatchCode(batchCode);
		jyBizTaskWorkGridManagerService.closeTaskForEndBatch(closeData);
	}
	@Override
	public JyBizTaskWorkGridManager initJyBizTaskWorkGridManager(BaseSiteInfoDto siteInfo,TaskWorkGridManagerSiteScanData taskWorkGridManagerScan,
			WorkGridManagerTask taskInfo,WorkGridManagerTaskConfigVo configData,
			WorkGrid grid,Date curDate) {
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
		jyTask.setFloor(grid.getFloor());
		//设置省区相关字段
		jyTask.setSiteName(siteInfo.getSiteName());
		jyTask.setAreaHubCode(StringHelper.getStringValue(siteInfo.getAreaCode()));
		jyTask.setAreaHubName(StringHelper.getStringValue(siteInfo.getAreaName()));
		jyTask.setProvinceAgencyCode(StringHelper.getStringValue(siteInfo.getProvinceAgencyCode()));
		jyTask.setProvinceAgencyName(StringHelper.getStringValue(siteInfo.getProvinceAgencyName()));
		//设置任务信息
		jyTask.setTaskType(taskInfo.getTaskType());
		jyTask.setTaskBizType(taskInfo.getTaskBizType());
		jyTask.setNeedScanGrid(taskInfo.getNeedScanGrid());
		jyTask.setTaskCode(taskInfo.getTaskCode());
		jyTask.setTaskName(taskInfo.getTaskName());
		jyTask.setTaskDescription(taskInfo.getTaskDescription());
		jyTask.setOrderNum(ThreadLocalRandom.current().nextInt(1000));
		if(taskWorkGridManagerScan.getBusinessQuotaInfoData() != null){
			jyTask.setExtendInfo(JsonHelper.toJson(taskWorkGridManagerScan.getBusinessQuotaInfoData()));
		}
		return jyTask;
	}


	private List<JyUserDto> getUserList(Integer siteCode,String organizationCode,String userPositionCode,String userPositionName){
		Result<List<JyUserDto>> userResult = jyUserManager.queryUserListBySiteAndPosition(siteCode, organizationCode, userPositionCode, userPositionName);
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
		data.setStatus(WorkTaskStatusEnum.OVER_TIME.getCode());
		data.setUpdateTime(new Date());
		data.setUpdateUser(DmsConstants.SYS_AUTO_USER_CODE);
		data.setUpdateUserName(DmsConstants.SYS_AUTO_USER_CODE);
		closeData.setData(data);
		jyBizTaskWorkGridManagerService.autoCloseTask(closeData);
		//保存任务责任信息
		List<JyWorkGridManagerData>  jyWorkGridManagerData = jyWorkGridManagerResponsibleInfoService.workGridManagerExpiredSaveResponsibleInfo(taskData.getBizIdList());
		//超时暴力分拣发送jmq 给判责系统
		if(!CollectionUtils.isEmpty(jyWorkGridManagerData)){
			jyWorkGridManagerResponsibleInfoService.workGridManagerExpiredSendMq(jyWorkGridManagerData);
		}
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
      //提前30分钟执行
        tTask.setExecuteTime(DateHelper.getBeforeTime(taskData.getExecuteTime(), taskBeforeExcuteTimes, rangeMaxSeconds));
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        tTask.setExecuteCount(0);

        logger.info("startWorkGridManagerScanTask-taskData={}", JsonHelper.toJson(taskData));
        taskService.doAddTask(tTask, false);
	}
	void addWorkGridManagerSiteScanTask(TaskWorkGridManagerSiteScanData taskData, Integer taskBizType) {
		if(taskBizType != null){
			WorkGridManagerTaskBizType bizTypeEnum = WorkGridManagerTaskBizType.getEnum(taskBizType);
			if(bizTypeEnum != null && bizTypeEnum.getCode().equals(KPI_IMPROVE.getCode())){
				logger.info("指标周期改善,不用新增任务");
				return;
			}
		}

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
        //提前15分钟执行
        tTask.setExecuteTime(DateHelper.getBeforeTime(taskData.getExecuteTime(), siteTaskBeforeExcuteTimes, rangeMaxSeconds));
        tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
        tTask.setExecuteCount(0);

        logger.info("executeWorkGridManagerScanTask-taskSiteData={}", JsonHelper.toJson(taskData));
        taskService.doAddTask(tTask, false);
	}
	@Override
	public void addWorkGridManagerAutoCloseTask(TaskWorkGridManagerAutoCloseData taskData) {
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
        tTask.setExecuteTime(DateHelper.getAfterTime(taskData.getExecuteTime(), closeTaskAfterExcuteTimes, closeTaskRangeMaxSeconds));
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
		String gridName = request.getGridName();
		Result<PositionDetailRecord> positionRecord = this.positionManager.queryOneByPositionCode(request.getScanPositionCode());
		if(positionRecord == null
				|| positionRecord.getData() == null) {
			result.toFail("扫描的网格码无效！");
			return result;
		}
		//兼容-网格和场地网格判断
		if(!request.getTaskRefGridKey().equals(positionRecord.getData().getRefGridKey())
				&& !request.getTaskRefGridKey().equals(positionRecord.getData().getRefWorkGridKey())) {
			if(StringUtils.isBlank(gridName)) {
				gridName = this.getGridNameByTaskRefGridKey(request.getTaskRefGridKey());
			}
			if(StringUtils.isBlank(gridName)) {
				result.toFail("任务的网格已失效，无需处理！");
				return result;
			}
			result.toFail("请扫描#"+gridName+"#的网格码！");
			return result;
		}else {
			result.setData(Boolean.TRUE);
		}
		return result;
	}
	/**
	 * 获取任务对应的网格名称
	 * @param taskRefGridKey
	 * @return
	 */
	private String getGridNameByTaskRefGridKey(String taskRefGridKey) {
		if(BusinessUtil.isWorkGridKey(taskRefGridKey)) {
			Result<WorkGrid> workGridResult= this.workGridManager.queryByWorkGridKey(taskRefGridKey);
			if(workGridResult != null
					&& workGridResult.getData() != null) {
				return workGridResult.getData().getGridName();
			}
		}
		if(BusinessUtil.isWorkStationGridKey(taskRefGridKey)) {
			WorkStationGridQuery checkQuery = new WorkStationGridQuery();
			checkQuery.setBusinessKey(taskRefGridKey);
			Result<WorkStationGrid> gridResult = this.workStationGridManager.queryByGridKey(checkQuery);
			if(gridResult != null
					&& gridResult.getData() != null) {
				return gridResult.getData().getGridName();
			}
		}
		return null;
	}

	@Override
	public boolean dealWorkGridModifyTask(WorkGridModifyMqData workGridModifyMqData) {
		if(workGridModifyMqData == null
				|| workGridModifyMqData.getGridData() == null) {
			return false;
		}
		//目前只处理网格删除的数据
		if(!EditTypeEnum.DELETE.getCode().equals(workGridModifyMqData.getEditType())) {
			return true;
		}
		JyBizTaskWorkGridManagerBatchUpdate cancelData = new JyBizTaskWorkGridManagerBatchUpdate();
		JyBizTaskWorkGridManager data = new JyBizTaskWorkGridManager();
		cancelData.setData(data);
		data.setTaskRefGridKey(workGridModifyMqData.getGridData().getBusinessKey());
		data.setStatus(WorkTaskStatusEnum.CANCEL_GRID_DELETE.getCode());
		data.setUpdateTime(new Date());
		data.setUpdateUser(DmsConstants.SYS_AUTO_USER_CODE);
		data.setUpdateUserName(DmsConstants.SYS_AUTO_USER_CODE);
		List<Integer> statusList = new ArrayList<>();
		cancelData.setStatusList(statusList);
		statusList.add(WorkTaskStatusEnum.TO_DISTRIBUTION.getCode());
		statusList.add(WorkTaskStatusEnum.TODO.getCode());
		statusList.add(WorkTaskStatusEnum.HANDLING.getCode());
		int num = jyBizTaskWorkGridManagerService.autoCancelTaskForGridDelete(cancelData);

		logger.info("dealWorkGridModifyTask-网格[{}]删除，线上化任务-取消{}条",workGridModifyMqData.getGridData().getBusinessKey(),num);
		return true;
	}

	@Override
	public JdCResponse<List<ResponsibleInfo>> queryResponsibleInfos(String bizId) {
		JdCResponse<List<ResponsibleInfo>> result = new JdCResponse<List<ResponsibleInfo>>();
		result.toSucceed("查询成功！");
		JyWorkGridManagerData taskData  = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
		if(taskData == null) {
			logger.warn("查询责任选择信息,任务信息不存在，bizId:{}", bizId);
			result.toFail("任务信息不存在！");
			return result;
		}
		//非暴力分拣任务
		if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
			logger.warn("非暴力分拣任务，不用指定责任人，bizId:{}", bizId);
			result.toFail("非暴力分拣任务，不用指定责任人！");
			return result;
		}

		//任务对应网格
		WorkGrid workGrid = getTaskWorkGrid(taskData.getTaskRefGridKey(), bizId);
		if(workGrid == null){
			logger.warn("查询责任选择信息，获取任务网格信息失败，bizId:{}", bizId);
			result.toFail("获取任务网格信息失败！");
			return result;
		}
		String gridKeys = taskData.getViolenceSortInfoData().getGridKeys();
		if(StringUtils.isBlank(gridKeys) || ArrayUtils.isEmpty(gridKeys.split(","))){
			result.toFail("获取任务网格信息失败！");
			return result;
		}
		
		//查询任务所在网格下所有工序
		List<String> refWorkGridKeys = new ArrayList<>(Arrays.asList(gridKeys.split(",")));
		List<String> workStationGrids = workStationGridManager.queryBusinessKeyByRefWorkGridKeys(refWorkGridKeys);
		if(CollectionUtils.isEmpty(workStationGrids)){
			result.toFail("网格无效或网格下无工序,请先配置");
			return result;
		}
		Date beginTime = new Date(taskData.getViolenceSortInfoData().getOperateTime());
		List<BaseUserSignRecordVo> userSignRecords = getWorkStationGridJobSignRecordList(workStationGrids, beginTime);
		if(CollectionUtils.isEmpty(userSignRecords)){
			result.toFail("该任务所在网格无签到数据！");
			return result;
		}
		
		//正式工
		List<ResponsibleInfo> formalWorkerResponsibleInfo = getFormalWorkerResponsibleInfo(userSignRecords, bizId);
		
		//网格工序对应外包商
		ResponsibleSupplier workGridSupplier = getWorkGridSupplier(workGrid, bizId);
		//外包工
		List<ResponsibleInfo> outsourcingWorkerResponsibleInfo = getOutsourcingWorkerResponsibleInfo(userSignRecords,
				taskData.getSiteCode(), bizId, workGridSupplier);
		
		//临时工
		List<ResponsibleInfo> temporaryWorkerResponsibleInfo = getTemporaryWorkerResponsibleInfo(userSignRecords, 
				workGrid, bizId);

		List<ResponsibleInfo> responsibleInfos = new ArrayList<>();
		responsibleInfos.addAll(formalWorkerResponsibleInfo);
		responsibleInfos.addAll(outsourcingWorkerResponsibleInfo);
		responsibleInfos.addAll(temporaryWorkerResponsibleInfo);
		result.setData(responsibleInfos);
		return result;
	}

	@Override
	public JdCResponse<List<JyWorkGridOwnerDto>> queryWorkGridOwners(String bizId) {
		JdCResponse<List<JyWorkGridOwnerDto>> result = new JdCResponse<List<JyWorkGridOwnerDto>>();
		result.toSucceed("查询成功！");
		JyWorkGridManagerData taskData  = jyBizTaskWorkGridManagerService.queryTaskDataByBizId(bizId);
		if(taskData == null) {
			result.toFail("任务数据不存在！");
			return result;
		}
		//非暴力分拣任务
		if(!WorkTaskTypeEnum.VIOLENCE_SORT.getCode().equals(taskData.getTaskType())){
			result.toFail("非暴力分拣任务，不用指定责任人！");
			return result;
		}
		
		
		ViolenceSortInfoData violenceSortInfoData = taskData.getViolenceSortInfoData();
		String gridKeys = violenceSortInfoData.getGridKeys();
		//切判责前历史数据无网格，取任务所属网格
		if(StringUtils.isBlank(gridKeys)){
			gridKeys = taskData.getTaskRefGridKey();
		}
		String[] gridKeyArray = gridKeys.split(",");
		
		//摄像头最多绑3个网格 可以循环查下
		List<JyWorkGridOwnerDto> gridOwnerDtos = new ArrayList<>();
		for(String gridKey : gridKeyArray){
			Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(gridKey);
			if(workGridResult == null){
				logger.info("根据gridKey获取网格信息，方法返回null,gridKey:{}", gridKey);
				continue;
			}
			WorkGrid workGrid = null;
			if((workGrid = workGridResult.getData()) == null){
				logger.info("根据gridKey获取网格信息，未查到网格信息，网格可能已删除,gridKey:{}", gridKey);
				continue;
			}
			if(StringUtils.isBlank(workGrid.getOwnerUserErp())){
				logger.info("根据gridKey获取网格信息，未查到网格信息，网格未维护组长,gridKey:{}", gridKey);
				continue;
			}
			JyWorkGridOwnerDto dto = getJyWorkGridOwnerDto(workGrid.getOwnerUserErp());
			if(dto != null){
				gridOwnerDtos.add(dto);
			}
			
		}
		result.setData(gridOwnerDtos);
		return result;
	}
	
	private JyWorkGridOwnerDto getJyWorkGridOwnerDto(String erp){
		if(StringUtils.isBlank(erp)){
			return null;
		}
		JyWorkGridOwnerDto dto = new JyWorkGridOwnerDto();
		if(erp.contains(",") && ArrayUtils.isNotEmpty(erp.split(","))){
			erp = erp.split(",")[0];
		}
		dto.setErp(erp);
		Result<JyUser> userResult = jyUserManager.queryUserInfo(dto.getErp());
		JyUser jyUser = null;
		if(userResult != null && (jyUser = userResult.getData()) != null){
			dto.setName(jyUser.getUserName());
		}else{
			dto.setName(erp);
			logger.info("根据erp:{}获取岗位信息失败", erp);
		}
		return dto;
	}
	
	

	/**
	 * 临时工-责任备选人
	 * @param userSignRecords
	 * @return
	 */
	private List<ResponsibleInfo> getTemporaryWorkerResponsibleInfo(List<BaseUserSignRecordVo> userSignRecords, WorkGrid workGrid,
																	String bizId){
		List<ResponsibleInfo> responsibleInfos = new ArrayList<>();
		//过滤临时工签到数据
		List<BaseUserSignRecordVo> temporaryWorkerSignRecords = userSignRecords.stream()
				.filter(u -> JOBTYPE4.getCode().equals(u.getJobCode())).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(temporaryWorkerSignRecords)){
			logger.info("签到数据无临时工签到记录,bizId:{}", bizId);
			return responsibleInfos;
		}
		JyWorkGridOwnerDto workGridOwnerDto = getJyWorkGridOwnerDto(workGrid.getOwnerUserErp());
		for(BaseUserSignRecordVo vo : temporaryWorkerSignRecords){
			ResponsibleInfo responsibleInfo = new ResponsibleInfo();
			responsibleInfo.setWorkType(ResponsibleWorkTypeEnum.TEMPORARY_WORKERS.getCode());
			responsibleInfo.setIdCard(vo.getUserCode());
			responsibleInfo.setName(vo.getUserName());
			responsibleInfo.setGridOwner(workGridOwnerDto);
			responsibleInfos.add(responsibleInfo);
		}
		return responsibleInfos;
	}
	/**
	 * 获取网格绑定的外包商
	 * @return
	 */
	WorkGrid getTaskWorkGrid(String workGridKey, String bizId){
		Result<WorkGrid> result = workGridManager.queryByWorkGridKey(workGridKey);
		if(result == null){
			logger.error("根据网格业务主键查询网格信息失败,返回值为空，workGridKey:{},bizId:{},", workGridKey, bizId);
			return null;
		}
		if(result.getData() == null){
			logger.error("根据网格业务主键未查询到网格信息，workGridKey:{},bizId:{},result.code:{}, result.messge:{}",
					workGridKey, bizId, result.getCode(), result.getMessage());
			return null;
		}
		return result.getData();
	}
	
	/**
	 * 获取网格绑定的外包商
	 * @return
	 */
	ResponsibleSupplier getWorkGridSupplier(WorkGrid workGrid, String bizId){
		if(StringUtils.isBlank(workGrid.getSupplierCode()) || StringUtils.isBlank(workGrid.getSupplierName())){
			logger.info("网格未配置外包商，workGridKey:{},bizId:{}",
					workGrid.getBusinessKey(), bizId);
			return null;
		}
		ResponsibleSupplier supplier = new ResponsibleSupplier();
		supplier.setSupplierName(workGrid.getSupplierName());
		supplier.setSupplierId(workGrid.getSupplierCode());
		return supplier;
	}
	/**
	 * 正式工-责任备选人
	 * @param userSignRecords
	 * @return
	 */
	private List<ResponsibleInfo> getFormalWorkerResponsibleInfo(List<BaseUserSignRecordVo> userSignRecords, String bizId){
		List<ResponsibleInfo> responsibleInfos = new ArrayList<>();
		//过滤正式工签到数据
		List<BaseUserSignRecordVo> formalWorkerUserSignRecords = userSignRecords.stream()
				.filter(u -> JOBTYPE1.getCode().equals(u.getJobCode())).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(formalWorkerUserSignRecords)){
			logger.info("签到数据无正式工签到记录,bizId:{}", bizId);
			return responsibleInfos;
		}
		for(BaseUserSignRecordVo userSignRecord : formalWorkerUserSignRecords){
			ResponsibleInfo responsibleInfo = new ResponsibleInfo();
			responsibleInfo.setWorkType(ResponsibleWorkTypeEnum.FORMAL_WORKER.getCode());
			responsibleInfo.setErp(userSignRecord.getUserCode());
			responsibleInfo.setName(userSignRecord.getUserName());
			responsibleInfos.add(responsibleInfo);
		}
		return responsibleInfos;
	}
	/**
	 * 外包工-责任备选人
	 * @param userSignRecords
	 * @return
	 */
	private List<ResponsibleInfo> getOutsourcingWorkerResponsibleInfo(List<BaseUserSignRecordVo> userSignRecords,
																   Integer siteCode, String bizId, ResponsibleSupplier workGridSupplier){
		List<ResponsibleInfo> responsibleInfos = new ArrayList<>();
		//过滤外包工签到数据
		List<BaseUserSignRecordVo> outsourcingUserSignRecords = userSignRecords.stream()
				.filter(u -> JOBTYPE3.getCode().equals(u.getJobCode())).collect(Collectors.toList());
		if(CollectionUtils.isEmpty(outsourcingUserSignRecords)){
			logger.info("签到数据无外包工签到记录,bizId:{},siteCode:{}", bizId, siteCode);
			return responsibleInfos;
		}
		
		List<JyTpUserScheduleQueryDto> dtos = new ArrayList<>();
		for(BaseUserSignRecordVo userSignRecord : outsourcingUserSignRecords){
			JyTpUserScheduleQueryDto dto = new JyTpUserScheduleQueryDto();
			dto.setSiteCode(siteCode);
			//外包工种
			dto.setNature(String.valueOf(JOBTYPE3.getCode()));
			dto.setUserCode(userSignRecord.getUserCode());
			dto.setScheduleDate(userSignRecord.getSignInTime());
			dtos.add(dto);
		}
		//查询三方人员储备，获取外包人员对应的外包商
		Result<List<JyThirdpartyUser>> jyThirdpartyUserResult = jyUserManager.batchQueryJyThirdpartyUser(dtos);
		Map<String, JyThirdpartyUser> userCodeToUser = null;
		if(jyThirdpartyUserResult.isFail() || CollectionUtils.isEmpty(jyThirdpartyUserResult.getData())){
			logger.info("外包工-责任备选人，未查到外包工,bizId:{}, siteCode:{}", bizId,siteCode);
		}else {
			userCodeToUser = jyThirdpartyUserResult.getData().stream().
					collect(Collectors.toMap(JyThirdpartyUser::getUserCode, user->user));
		}
		
		for(BaseUserSignRecordVo vo : outsourcingUserSignRecords){
			ResponsibleInfo responsibleInfo = new ResponsibleInfo();
			responsibleInfo.setWorkType(ResponsibleWorkTypeEnum.OUTWORKER.getCode());
			responsibleInfo.setIdCard(vo.getUserCode());
			responsibleInfo.setName(vo.getUserName());
			JyThirdpartyUser jyThirdpartyUser = null;
			//从三方储备信息获取外包商信息
			if(userCodeToUser != null && (jyThirdpartyUser = userCodeToUser.get(vo.getUserCode())) != null && 
			StringUtils.isNotBlank(jyThirdpartyUser.getCompanyId()) && StringUtils.isNotBlank(jyThirdpartyUser.getCompanyName())){
				ResponsibleSupplier supplier = new ResponsibleSupplier();
				supplier.setSupplierId(jyThirdpartyUser.getCompanyId());
				supplier.setSupplierName(jyThirdpartyUser.getCompanyName());
				responsibleInfo.setSupplier(supplier);
			}else {
				//无三方储备信息 取网格外包商家信息
				responsibleInfo.setSupplier(workGridSupplier);
			}
			responsibleInfos.add(responsibleInfo);
		}
		//外包计提场地外包商
		
		
		//外包计提配置的外包商
		List<SupplierVO> supplierVOs = workingConfigQueryService.querySupplierBySiteCode(siteCode);
		List<ResponsibleSupplier> supplierList = new ArrayList<>();
		if(!CollectionUtils.isEmpty(supplierVOs)){
			for(SupplierVO supplierVO : supplierVOs){
				ResponsibleSupplier supplier = new ResponsibleSupplier();
				supplier.setSupplierId(supplierVO.getSupplierCode());
				supplier.setSupplierName(supplierVO.getSupplierName());
				supplierList.add(supplier);
			}
		}
		
		//无三方储备和网格外包商的，设置无场地外包计提配置的外包商
		for(ResponsibleInfo responsibleInfo : responsibleInfos){
			if(responsibleInfo.getSupplier() == null){
				responsibleInfo.setSupplierList(supplierList);
			}
		}
		//无外包商信息的删除
		Boolean removed = responsibleInfos.removeIf(r -> r.getSupplier() == null && CollectionUtils.isEmpty(r.getSupplierList()));
		logger.info("部分外包无外包商，bizId:{},删除结果removed:{}", bizId, removed);
		return responsibleInfos;
		
	}

	/**
	 * 查询任务网格签到数据
	 * @param refWorkKeys 工序外键
	 * @param processBeginTime 任务开始时间
	 * @return
	 */
	private List<BaseUserSignRecordVo> getWorkStationGridJobSignRecordList(List<String> refWorkKeys, Date processBeginTime){
		UserSignRecordQuery query = new UserSignRecordQuery();
		query.setRefGridKeyList(refWorkKeys);
		//正式 外包 临时
		List<Integer> jobCodeList = Arrays.asList(JOBTYPE1.getCode(), JOBTYPE3.getCode(), JOBTYPE4.getCode());
		query.setJobCodeList(jobCodeList);
		//签到时间往前偏移n小时
		Date signDateStart = DateHelper.addHours(processBeginTime, -1 * getSignDateStartOffset());
		query.setSignDateStart(signDateStart);
		query.setSignDateEnd(processBeginTime);
		//查询任务开始前签到数据
		List<BaseUserSignRecordVo> records = userSignRecordService.queryByGridSign(query);
		if(CollectionUtils.isEmpty(records)){
			return records;
		}
		//userCode 在库里为加密存储，不方便在查询sql中去重
		List<BaseUserSignRecordVo> recordList = new ArrayList<>(records.size());
		//根据userCode 去重
		records.stream().filter(StringHelper.distinctByKey(BaseUserSignRecordVo::getUserCode)).forEach(recordList::add);
		return recordList;
	}

	/**
	 * 开始时间偏移量
	 * @return
	 */
	private int getSignDateStartOffset(){
		int hours = 18;
		SysConfig signDateStartOffsetConfig = sysConfigService.findConfigContentByConfigName(SIGN_DATE_START_OFFSET);
		if(signDateStartOffsetConfig != null && org.apache.commons.lang3.StringUtils.isNumeric(signDateStartOffsetConfig.getConfigContent())){
			hours = Integer.parseInt(signDateStartOffsetConfig.getConfigContent());
		}
		return hours;
	}
	

	/**
	 * 从装车质量报表ck查询该场地网格
	 * @param areaCodes
	 * @param siteCode
	 * @return
	 */
	private List<WorkGrid> getWorkGridByLoadCarQualityReport(List<String> areaCodes, Integer siteCode, Integer limit, Integer pageNum){
		CallerInfo callerInfo = Profiler.registerInfo("dmsWork.JyWorkGridManagerBusinessService.getWorkGridByLoadCarQualityReport",
				Constants.UMP_APP_NAME_DMSWORKER,false,true);
		List<WorkGrid> workGrids = null;
		try {
			//指标业务只查一页
			if(pageNum > 1){
				return null;
			}
			Map<String, Object> queryParam = new HashMap<>();
			String dt = DateHelper.formatDate(DateHelper.getZeroFromDay(new Date(), 2));
			queryParam.put("dt", dt);
			queryParam.put("siteCode", siteCode.toString());
			//调用easydata 装车质量 发货扫描率倒数第一的网格
			FdsPage edresult = easyDataClientUtil.query(dmsWEasyDataConfig.getQueryLoadCarQualityGrid(), queryParam,
					dmsWEasyDataConfig.getApiGroupName(),dmsWEasyDataConfig.getAppToken(),
					limit,pageNum - 1, null);
			if(edresult == null || CollectionUtils.isEmpty(edresult.getContent())) {
				logger.info("从装车质量报表ck未查到网格信息,dt:{},siteCode:{}", dt, siteCode);
				return null;
			}
			workGrids = new ArrayList<>();
			List<String> refGridKeys = new ArrayList<>();
			for(Object data : edresult.getContent()){
				Map map = (Map)data;
				String refGridKey = null;
				if(map.containsKey("refGridKey") && map.get("refGridKey") != null) {
					refGridKey = map.get("refGridKey").toString();
					Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(refGridKey);
					if(workGridResult == null || workGridResult.getData() == null) {
						logger.info("从装车质量报表ck未查到网格信息,网格信息为空,refGridKey:{}", refGridKey);
						continue;
					}
					refGridKeys.add(refGridKey);
					workGrids.add(workGridResult.getData());
				}
			}
			logger.info("装车质量报表查到指标平均得分倒数的网格,dt:{},siteCode:{},refGridKeys:{}", dt, siteCode,
					JsonHelper.toJson(refGridKeys));
		} catch (Exception e) {
			logger.error("从装车质量报表ck查询该场地网格异常，siteCode:{}",siteCode,e);
			Profiler.functionError(callerInfo);
		} finally {
			Profiler.registerInfoEnd(callerInfo);
		}
		return workGrids;
	}

	/**
	 * 根据最近处理人erp在userDtos位置，所有元素右移，最近处理人erp移至最后。
	 * @param userDtos
	 * @param taskCode
	 * @return
	 */
	public List<JyUserDto> sortUserListByLast(List<JyUserDto> userDtos, String taskCode, Integer siteCode){
		if(CollectionUtils.isEmpty(userDtos) || userDtos.size() == 1){
			return userDtos;
		}
		String lastErp = jyBizTaskWorkGridManagerService.selectLastHandlerErp(taskCode, siteCode);
		if(org.apache.commons.lang3.StringUtils.isBlank(lastErp)){
			logger.info("根据任务未查到任务实例，处理人list不重新排序，taskCode:{}", taskCode);
			return userDtos;
		}
		Integer lastUserIndex = null;
		for(int i=0; i<userDtos.size(); i++){
			JyUserDto jyUserDto = userDtos.get(i);
			if(lastErp.equals(jyUserDto.getUserErp())){
				lastUserIndex = i;
				break;
			}
		}
		if(lastUserIndex == null){
			return userDtos;
		}
		//已经是最后是最后一个
		if(lastUserIndex == userDtos.size() -1){
			return userDtos;
		}
		//是第一位所有元素左移，0位放到最后一位
		if(lastUserIndex == 0){
			JyUserDto first = userDtos.remove(0);
			userDtos.add(first);
			return userDtos;
		}

		List<JyUserDto> list = new ArrayList<>(userDtos.size());
		//后半部分list,不包含上次已分任务erp
		List<JyUserDto> secondHalfList = userDtos.subList(lastUserIndex + 1, userDtos.size());
		list.addAll(secondHalfList);
		List<JyUserDto> firstHalfList = userDtos.subList(0, lastUserIndex + 1);
		list.addAll(firstHalfList);
		return list;
	}
}
