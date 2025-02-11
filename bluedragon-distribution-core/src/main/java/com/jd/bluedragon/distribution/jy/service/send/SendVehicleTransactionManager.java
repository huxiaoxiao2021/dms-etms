package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BasicSelectWsManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.jy.dao.send.JySendTransferLogDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.task.SealSyncOpenCloseSendTaskDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.summary.JySealStatisticsSummaryService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.enums.JySendTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;

/**
 * @ClassName SendVehicleTransactionManager
 * @Description
 * @Author wyh
 * @Date 2022/6/12 20:41
 **/
@Component("sendVehicleTransactionManager")
public class SendVehicleTransactionManager {

    private static final Logger log = LoggerFactory.getLogger(SendVehicleTransactionManager.class);
    private static final String DEFAULT_USER = "sys";

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JyVehicleSendRelationService jySendCodeService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private BasicSelectWsManager basicSelectWsManager;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Value("${beans.sendVehicleTransactionManager.checkLineTypeDays:7}")
    private int checkLineTypeDays;

    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;

    @Resource
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private JySealStatisticsSummaryService statisticsSummaryService;

    @Autowired
    private JySendTransferLogDao jySendTransferLogDao;

    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    private NewSealVehicleService newsealVehicleService;
    @Autowired
    private SendMDao sendMDao;

    /**
     * 保存发货任务和发货流向
     * @param sendVehicleEntity
     * @param detailEntity
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.saveTaskSendAndDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveTaskSendAndDetail(JyBizTaskSendVehicleEntity sendVehicleEntity, JyBizTaskSendVehicleDetailEntity detailEntity) {
        if (sendVehicleEntity != null) {

            logInfo("初始化派车单.{}", JsonHelper.toJson(sendVehicleEntity));

            taskSendVehicleService.initTaskSendVehicle(sendVehicleEntity);

            // 创建发货调度任务
            createSendScheduleTask(sendVehicleEntity);
        }
        if (detailEntity != null) {

            logInfo("初始化派车单明细.{}", JsonHelper.toJson(detailEntity));

            if (taskSendVehicleDetailService.saveTaskSendDetail(detailEntity) > 0) {
                // 首次创建发货流向时，同时生成批次
                saveSendCode(detailEntity);
            }
        }

        return true;
    }

    /**
     * 创建发货调度任务
     * @param sendVehicleEntity
     * @return
     */
    public JyScheduleTaskResp createSendScheduleTask(JyBizTaskSendVehicleEntity sendVehicleEntity){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(sendVehicleEntity.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        req.setOpeUser(sendVehicleEntity.getCreateUserErp());
        req.setOpeUserName(sendVehicleEntity.getCreateUserName());
        req.setOpeTime(new Date());
        return jyScheduleTaskManager.createScheduleTask(req);
    }

    /**
     * 更新车辆到来时间 和 即将到来时间
     * @param sendVehicleDetail
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.saveComeOrNearComeTime",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveComeOrNearComeTime(JyBizTaskSendVehicleEntity sendVehicleDetail){
        //获取子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = taskSendVehicleDetailService.findByBizId(sendVehicleDetail.getBizId());
        if(detailEntity == null || org.springframework.util.StringUtils.isEmpty(detailEntity.getSendVehicleBizId())){
            log.warn("未获取到对应发货子任务,{}",JsonHelper.toJson(sendVehicleDetail));
            return true;
        }

        //记录主任务数据 需要关心时间比原来的小才会更新
        JyBizTaskSendVehicleEntity saveComeTimeParam = new JyBizTaskSendVehicleEntity();
        saveComeTimeParam.setBizId(detailEntity.getSendVehicleBizId());
        saveComeTimeParam.setComeTime(sendVehicleDetail.getComeTime());
        saveComeTimeParam.setNearComeTime(sendVehicleDetail.getNearComeTime());
        taskSendVehicleService.updateComeTimeOrNearComeTime(saveComeTimeParam);

        //记录子任务数据
        JyBizTaskSendVehicleDetailEntity saveComeTimeDetailParam = new JyBizTaskSendVehicleDetailEntity();
        saveComeTimeDetailParam.setBizId(detailEntity.getBizId());
        saveComeTimeDetailParam.setComeTime(sendVehicleDetail.getComeTime());
        saveComeTimeDetailParam.setNearComeTime(sendVehicleDetail.getNearComeTime());
        return taskSendVehicleDetailService.updateByBiz(saveComeTimeDetailParam) > 0;
    }

    /**
     * 生成发货批次
     * @param sendDetailDomain
     */
    public int saveSendCode(JyBizTaskSendVehicleDetailEntity sendDetailDomain) {
        JySendCodeEntity sendCodeEntity = new JySendCodeEntity();
        sendCodeEntity.setSendVehicleBizId(sendDetailDomain.getSendVehicleBizId());
        sendCodeEntity.setSendDetailBizId(sendDetailDomain.getBizId());
        sendCodeEntity.setSendCode(generateSendCode(sendDetailDomain.getStartSiteId(), sendDetailDomain.getEndSiteId(), sendDetailDomain.getCreateUserErp()));
        sendCodeEntity.setCreateUserErp(sendDetailDomain.getCreateUserErp());
        sendCodeEntity.setCreateUserName(sendDetailDomain.getCreateUserName());
        sendCodeEntity.setUpdateUserErp(sendCodeEntity.getCreateUserErp());
        sendCodeEntity.setUpdateUserName(sendCodeEntity.getCreateUserName());
        sendCodeEntity.setSource(0);
        Date now = new Date();
        sendCodeEntity.setCreateTime(now);
        sendCodeEntity.setUpdateTime(now);

        return jySendCodeService.add(sendCodeEntity);
    }

    private String generateSendCode(Long startSiteId, Long destSiteId, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(startSiteId));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(destSiteId));
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    /**
     * 更新发货任务状态
     * @param taskSend
     * @param sendDetail
     * @param updateStatus
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.updateTaskStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateTaskStatus(JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity sendDetail, JyBizTaskSendDetailStatusEnum updateStatus) {
        JyBizTaskSendVehicleDetailEntity detailQ = getSendVehicleDetailEntity(sendDetail, updateStatus);
        if (taskSendVehicleDetailService.updateStatus(detailQ, sendDetail.getVehicleStatus()) > 0) {
            logInfo("发货任务流向[{}]状态更新为“{}”. {}", sendDetail.getBizId(), updateStatus.getName(), JsonHelper.toJson(sendDetail));

            // 发货发货任务明细的最小状态
            Integer taskSendMinStatus = getTaskSendMinStatus(sendDetail);
            JyBizTaskSendVehicleEntity sendStatusQ = getSendVehicleEntity(sendDetail, updateStatus, taskSendMinStatus);

            if (taskSendVehicleService.updateStatus(sendStatusQ, taskSend.getVehicleStatus()) > 0) {
                logInfo("发货任务[{}]状态更新为“{}”. {}", taskSend.getBizId(), JyBizTaskSendStatusEnum.getNameByCode(taskSendMinStatus), JsonHelper.toJson(taskSend));

                // 发货流向全部封闭完成，关闭调度任务
                if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(taskSendMinStatus)) {

                    finishUnloadTaskGroup(detailQ);

                    // 关闭调度任务
                    closeScheduleTask(detailQ);
                }
            }
        }

        return true;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.sealCarStatusUpdateAndSummary",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean sealCarStatusUpdateAndSummary(JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity sendDetail, JyStatisticsSummaryEntity summaryEntity) {
        if(sendVehicleTransactionManager.updateTaskStatus(taskSend, sendDetail, JyBizTaskSendDetailStatusEnum.SEALED)) {
            if(!Objects.isNull(summaryEntity)) {
                statisticsSummaryService.insertSelective(summaryEntity);
            }
        }
        return true;
    }

    /**
     * 更新发货任务状态--航空类型发货任务
     * @param taskSend
     * @param sendDetail
     * @param updateStatus
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.updateAviationTaskStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateAviationTaskStatus(JyBizTaskSendAviationPlanEntity aviationPlanEntity,
                                            JyBizTaskSendVehicleEntity taskSend,
                                            JyBizTaskSendVehicleDetailEntity sendDetail,
                                            JyStatisticsSummaryEntity summaryEntity,
                                            JyBizTaskSendDetailStatusEnum updateStatus) {
        //
        sendVehicleTransactionManager.updateTaskStatus(taskSend, sendDetail, updateStatus);
        //
        jyBizTaskSendAviationPlanService.updateStatus(aviationPlanEntity);
        if(log.isInfoEnabled()){
            log.info("航空任务封车完成，订舱号={}", aviationPlanEntity.getBookingCode());
        }
        if (JyBizTaskSendDetailStatusEnum.SEALED.equals(updateStatus)) {
            //封车节点做数据汇总
            statisticsSummaryService.insertSelective(summaryEntity);
        }
        return true;
    }


    /**
     * 更新发货任务状态 （不比较原状态顺序方式）
     * @param taskSend
     * @param sendDetail
     * @param updateStatus
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.updateStatusWithoutCompare",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean updateStatusWithoutCompare(JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity sendDetail, JyBizTaskSendDetailStatusEnum updateStatus) {
        JyBizTaskSendVehicleDetailEntity detailQ = getSendVehicleDetailEntity(sendDetail, updateStatus);
        detailQ.setBizId(sendDetail.getBizId());
        if (taskSendVehicleDetailService.updateStatusWithoutCompare(detailQ, sendDetail.getVehicleStatus()) > 0) {
            logInfo("发货任务流向[{}]状态更新（不比较原状态）为“{}”. {}", sendDetail.getBizId(), updateStatus.getName(), JsonHelper.toJson(sendDetail));

            // 发货发货任务明细的最小状态
            Integer taskSendMinStatus = getTaskSendMinStatus(sendDetail);
            JyBizTaskSendVehicleEntity sendStatusQ = getSendVehicleEntity(sendDetail, updateStatus, taskSendMinStatus);

            if (taskSendVehicleService.updateStatusWithoutCompare(sendStatusQ, taskSend.getVehicleStatus()) > 0) {
                logInfo("发货任务[{}]状态更新（不比较原状态）为“{}”. {}", taskSend.getBizId(), JyBizTaskSendStatusEnum.getNameByCode(taskSendMinStatus), JsonHelper.toJson(taskSend));

                // 发货流向全部封闭完成，关闭调度任务
                if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(taskSendMinStatus)) {

                    finishUnloadTaskGroup(detailQ);

                    // 关闭调度任务
                    closeScheduleTask(detailQ);
                }
            }
        }

        return true;
    }

    /**
     * 刷新发货主任务状态 （不比较原状态顺序方式）
     * @param taskSend
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.reloadStatusWithoutCompare",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean reloadStatusWithoutCompare(JyBizTaskSendVehicleEntity taskSend) {
        JyBizTaskSendVehicleDetailEntity querySendDetail = new JyBizTaskSendVehicleDetailEntity();
        querySendDetail.setSendVehicleBizId(taskSend.getBizId());
        querySendDetail.setStartSiteId(taskSend.getStartSiteId());
        // 发货发货任务明细的最小状态
        Integer taskSendMinStatus = getTaskSendMinStatus(querySendDetail);
        JyBizTaskSendVehicleEntity sendStatusQ = new JyBizTaskSendVehicleEntity();
        if(taskSend.getUpdateTime() == null){
            sendStatusQ.setUpdateTime(new Date());
        }
        sendStatusQ.setUpdateUserErp(taskSend.getUpdateUserErp());
        sendStatusQ.setUpdateUserName(taskSend.getUpdateUserName());
        sendStatusQ.setVehicleStatus(taskSendMinStatus);
        sendStatusQ.setBizId(taskSend.getBizId());
        if (taskSendVehicleService.updateStatusWithoutCompare(sendStatusQ, taskSend.getVehicleStatus()) > 0) {
            logInfo("发货任务-刷新发货主任务状态[{}]状态更新（不比较原状态）为“{}”. {}", taskSend.getBizId(), JyBizTaskSendStatusEnum.getNameByCode(taskSendMinStatus), JsonHelper.toJson(taskSend));
        }else{
            log.warn("发货任务-刷新发货主任务状态[{}]状态更新失败未执行（不比较原状态）为“{}”. {}", taskSend.getBizId(), JyBizTaskSendStatusEnum.getNameByCode(taskSendMinStatus), JsonHelper.toJson(taskSend));
            return false;
        }
        return true;
    }

    /**
     * 关闭调度任务
     * @param detailQ
     * @return
     */
    private boolean closeScheduleTask(JyBizTaskSendVehicleDetailEntity detailQ){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(detailQ.getSendVehicleBizId());
        req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.SEND.getCode()));
        req.setOpeUser(detailQ.getUpdateUserErp());
        req.setOpeUserName(detailQ.getUpdateUserName());
        req.setOpeTime(detailQ.getSealCarTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.closeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 查询调度任务ID
     * @param bizId
     * @return
     */
    private String getJyScheduleTaskId(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    private void finishUnloadTaskGroup(JyBizTaskSendVehicleDetailEntity sendStatusQ) {
        String taskId = getJyScheduleTaskId(sendStatusQ.getSendVehicleBizId());
        if (StringUtils.isBlank(taskId)) {
            return;
        }
        JyTaskGroupMemberEntity endData = new JyTaskGroupMemberEntity();
        endData.setRefTaskId(taskId);
        endData.setUpdateUser(sendStatusQ.getUpdateUserErp());
        endData.setUpdateUserName(sendStatusQ.getUpdateUserName());
        endData.setUpdateTime(sendStatusQ.getSealCarTime());
        Result<Boolean> result = taskGroupMemberService.endTask(endData);

        logInfo("封车完成关闭小组任务. data:{}, result:{}", JsonHelper.toJson(endData), JsonHelper.toJson(result));
    }

    private JyBizTaskSendVehicleDetailEntity getSendVehicleDetailEntity(JyBizTaskSendVehicleDetailEntity sendDetail, JyBizTaskSendDetailStatusEnum updateStatus) {
        JyBizTaskSendVehicleDetailEntity statusQ = new JyBizTaskSendVehicleDetailEntity(sendDetail.getStartSiteId(), sendDetail.getEndSiteId(), sendDetail.getSendVehicleBizId());
        statusQ.setBizId(sendDetail.getBizId());
        statusQ.setVehicleStatus(updateStatus.getCode());
        statusQ.setUpdateTime(sendDetail.getUpdateTime());
        statusQ.setUpdateUserName(sendDetail.getUpdateUserName());
        statusQ.setUpdateUserErp(sendDetail.getUpdateUserErp());
        statusQ.setSealCarTime(sendDetail.getSealCarTime());
        return statusQ;
    }

    private JyBizTaskSendVehicleEntity getSendVehicleEntity(JyBizTaskSendVehicleDetailEntity sendDetail, JyBizTaskSendDetailStatusEnum updateStatus, Integer taskSendMinStatus) {
        JyBizTaskSendVehicleEntity sendStatusQ = new JyBizTaskSendVehicleEntity();
        sendStatusQ.setBizId(sendDetail.getSendVehicleBizId());
        sendStatusQ.setVehicleStatus(taskSendMinStatus);
        sendStatusQ.setStartSiteId(sendDetail.getStartSiteId());
        sendStatusQ.setUpdateTime(sendDetail.getUpdateTime());
        sendStatusQ.setUpdateUserName(sendDetail.getUpdateUserName());
        sendStatusQ.setUpdateUserErp(sendDetail.getUpdateUserErp());
        sendStatusQ.setTaskType(sendDetail.getTaskType());
        // 封车时更新发货任务最晚封车时间
        if (JyBizTaskSendDetailStatusEnum.SEALED.equals(updateStatus)) {
            sendStatusQ.setLastSealCarTime(this.getLastSealCarTime(sendStatusQ));
        }
        return sendStatusQ;
    }

    private Date getLastSealCarTime(JyBizTaskSendVehicleEntity taskSend) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(taskSend.getStartSiteId(), taskSend.getBizId());
        detailQ.setTaskType(taskSend.getTaskType());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        Date lastSealCarTime = vehicleDetailList.get(0).getSealCarTime();
        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            if (lastSealCarTime != null) {
                if (detailEntity.getSealCarTime() != null) {
                    if (lastSealCarTime.before(detailEntity.getSealCarTime())) {
                        lastSealCarTime = detailEntity.getSealCarTime();
                    }
                }
            }
            else {
                if (detailEntity.getSealCarTime() != null) {
                    lastSealCarTime = detailEntity.getSealCarTime();
                }
            }
        }

        return lastSealCarTime;
    }
    /**
     * 根据站点流向判断是否干支类型:
     * 1、查询7天内的主任务id列表
     * 2、查询是否包含非干支任务（非干支）
     * @param startSiteId
     * @param endSiteId
     * @return
     */
    public boolean isTrunkOrBranchLine(Long startSiteId,Long endSiteId) {
    	Date beginTime = DateHelper.addDate(new Date(), -checkLineTypeDays);
    	JyBizTaskSendVehicleDetailEntity query = new JyBizTaskSendVehicleDetailEntity();
    	query.setStartSiteId(startSiteId);
    	query.setEndSiteId(endSiteId);
    	query.setCreateTimeBegin(beginTime);
    	List<String> sendVehicleBizList = taskSendVehicleDetailService.findSendVehicleBizListBySendFlow(query);
    	if(CollectionUtils.isEmpty(sendVehicleBizList)) {
    		return false;
    	}
    	//数据分组
    	List<List<String>> groupBizList = CollectionHelper.splitList(sendVehicleBizList, Constants.DB_SQL_IN_MAX_GROUP_NUM,Constants.DB_SQL_IN_LIMIT_NUM);

    	JyBizTaskSendVehicleEntity checkQuery = new JyBizTaskSendVehicleEntity();
    	checkQuery.setStartSiteId(startSiteId);
    	checkQuery.setCreateTimeBegin(beginTime);
        checkQuery.setManualCreatedFlag(Constants.NUMBER_ZERO);
    	List<Integer> lineTypes = new ArrayList<>();
    	for(JyLineTypeEnum lineType : JyLineTypeEnum.values()) {
    		if(JyLineTypeEnum.TRUNK_LINE.equals(lineType)
    				||JyLineTypeEnum.BRANCH_LINE.equals(lineType)) {
    			continue;
    		}
    		lineTypes.add(lineType.getCode());
    	}
    	//查询是否包含非干支任务
    	for(List<String> bizList: groupBizList) {
    		int bizNum = taskSendVehicleService.countBizNumForCheckLineType(checkQuery, bizList,lineTypes);
    		if(bizNum > 0) {
    			return false;
    		}
    	}
    	return true;
    }
    /**
     * 获得发货明细最下的状态
     * @param sendDetail
     * @return
     */
    private Integer getTaskSendMinStatus(JyBizTaskSendVehicleDetailEntity sendDetail) {
        List<JyBizTaskSendCountDto> sendCountDtos = taskSendVehicleDetailService.sumByVehicleStatus(new JyBizTaskSendVehicleDetailEntity(sendDetail.getStartSiteId(), sendDetail.getSendVehicleBizId()));
        if(!CollectionUtils.isEmpty(sendCountDtos)){
            Collections.sort(sendCountDtos, new Comparator<JyBizTaskSendCountDto>() {
                @Override
                public int compare(JyBizTaskSendCountDto o1, JyBizTaskSendCountDto o2) {
                    return o1.getVehicleStatus().compareTo(o2.getVehicleStatus());
                }
            });

            return sendCountDtos.get(0).getVehicleStatus();
        }
        return JyBizTaskSendStatusEnum.CANCEL.getCode();

    }
    /**
     * 1、按transWorkItemCode查询相应的子任务
     * 2、回退任务状态
     * @param sealCarData
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.resetSendStatusToseal",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public boolean resetSendStatusToseal(SealCarDto sealCarData, String operateUserCode, String operateUserName,
			Long operateTime) {
    	this.logInfo("取消封车-发货任务状态回退sealCarData：{}",JsonHelper.toJson(sealCarData));
    	JyBizTaskSendVehicleDetailEntity query = new JyBizTaskSendVehicleDetailEntity();
    	query.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
    	query.setTransWorkItemCode(sealCarData.getTransWorkItemCode());
    	JyBizTaskSendVehicleDetailEntity taskDetail = this.taskSendVehicleDetailService.findByTransWorkItemCode(query);
		if(taskDetail == null){
			this.logInfo("根据取消封车数据{}查询taskDetail为空！", JsonHelper.toJson(query));
			return true;
		}
		this.logInfo("取消封车-发货任务状态回退taskDetail：{}",JsonHelper.toJson(taskDetail));
		Date currentDate = new Date();
		JyBizTaskSendVehicleEntity taskSend = new JyBizTaskSendVehicleEntity();
		JyBizTaskSendVehicleDetailEntity sendDetail = new JyBizTaskSendVehicleDetailEntity();
		taskSend.setBizId(taskDetail.getSendVehicleBizId());
		taskSend.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
		taskSend.setStartSiteId(taskDetail.getStartSiteId());
		taskSend.setUpdateTime(currentDate);
		taskSend.setUpdateUserErp(operateUserCode);
		taskSend.setUpdateUserName(operateUserName);

		sendDetail.setBizId(taskDetail.getBizId());
		sendDetail.setSendVehicleBizId(taskDetail.getSendVehicleBizId());
		sendDetail.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
		sendDetail.setStartSiteId(taskDetail.getStartSiteId());
		sendDetail.setEndSiteId(taskDetail.getEndSiteId());
		sendDetail.setUpdateTime(currentDate);
		sendDetail.setUpdateUserErp(operateUserCode);
		sendDetail.setUpdateUserName(operateUserName);

		return this.updateStatusWithoutCompare(taskSend, sendDetail, JyBizTaskSendDetailStatusEnum.TO_SEAL);
	}

    /**
     * 传站功能拦截
     * @param receiveSiteId
     * @param currentOperate
     * @return
     */
    public boolean needInterceptOfCz(Integer receiveSiteId,CurrentOperate currentOperate){
        Integer orgId =currentOperate.getOrgId();
        Integer siteId =currentOperate.getSiteCode();
        if ((dmsConfigManager.getPropertyConfig().getCzOrgForbiddenList().contains(String.valueOf(orgId)) || dmsConfigManager.getPropertyConfig().getCzSiteForbiddenList().contains(String.valueOf(siteId)))
            && checkIsCz(receiveSiteId)){
            return true;
        }
        return false;
    }

    private boolean checkIsCz(Integer receiveSiteId) {
        BaseStaffSiteOrgDto baseSiteInfoDto = baseService.getSiteBySiteID(Integer.valueOf(receiveSiteId));
        if (ObjectHelper.isNotNull(baseSiteInfoDto) && dmsConfigManager.getPropertyConfig().getCzSiteTypeForbiddenList().contains(String.valueOf(baseSiteInfoDto.getSiteType()))){
            return true;
        }
        return false;
    }


    /**
     * 干支 拦截功能方法
     *
     * @return
     */
    public InvokeResult<Boolean> needInterceptOfGZ(String sendCode, String menuCode, CurrentOperate currentOperate, User user) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.FALSE);
        result.success();
        try{

            Integer[] sites = BusinessUtil.getSiteCodeBySendCode(sendCode);
            if(sites == null || sites.length != 2){
                return result;
            }
            Integer createSite = sites[0];
            Integer receiveSite = sites[1];
            BaseStaffSiteOrgDto receiveSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
            BaseStaffSiteOrgDto createSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(createSite));

            MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
            menuUsageConfigRequestDto.setMenuCode(menuCode);
            menuUsageConfigRequestDto.setCurrentOperate(currentOperate);
            menuUsageConfigRequestDto.setUser(user);
            MenuUsageProcessDto menuUsageProcessDto = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
            if(menuUsageProcessDto != null && Constants.FLAG_OPRATE_OFF.equals(menuUsageProcessDto.getCanUse())) {
                boolean isTrunkOrBranch = isTrunkOrBranchLine(Long.valueOf(createSite), Long.valueOf(receiveSite));
                if (isTrunkOrBranch){
                    boolean needIntercept = Boolean.TRUE;
                    //补充判断运力的运输方式是否包含铁路或者航空
                    if(receiveSiteDto != null && createSiteDto != null){
                        TransportResourceDto transportResourceDto = new TransportResourceDto();
                        // 始发区域
                        transportResourceDto.setStartOrgCode(String.valueOf(createSiteDto.getOrgId()));
                        // 始发站
                        transportResourceDto.setStartNodeId(createSite);
                        // 目的区域
                        transportResourceDto.setEndOrgCode(String.valueOf(receiveSiteDto.getOrgId()));
                        // 目的站
                        transportResourceDto.setEndNodeId(receiveSite);
                        List<TransportResourceDto> transportResourceDtos = basicSelectWsManager.queryPageTransportResourceWithNodeId(transportResourceDto);
                        if(transportResourceDtos!=null){
                            for(TransportResourceDto trd: transportResourceDtos){
                                if(dmsConfigManager.getPropertyConfig().notValidateTransType(trd.getTransWay())){
                                    needIntercept = Boolean.FALSE;
                                    break;
                                }
                            }
                        }
                    }
                    if(needIntercept){
                        log.info("needInterceptOfGZ:干支校验拦截！{},{}",sendCode,menuCode);
                        result.setData(Boolean.TRUE);
                        result.setMessage(menuUsageProcessDto.getMsg());
                        return result;
                    }else{
                        log.info("needInterceptOfGZ:干支校验未拦截！匹配到存在空铁场景下的运力，{},{}",sendCode,menuCode);
                    }
                }else{
                    log.info("needInterceptOfGZ:干支校验未拦截！未匹配到近7天的干支发货任务，{},{}",sendCode,menuCode);
                }
            }else{
                log.info("needInterceptOfGZ:干支校验未拦截！未匹配到拦截场地配置，{},{}",sendCode,menuCode);
            }
        }catch (Exception e) {
            log.error("needInterceptOfGZ:干支校验异常！{},{}",sendCode,menuCode, e);
        }
        return result;
    }

    /**
     * 航空计划生成发货任务：保存发货任务、发货流向、航空发货计划
     * @param aviationPlanEntity
     * @param sendVehicleEntity
     * @param sendVehicleDetailEntity
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.saveAviationPlanAndTaskSendAndDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean saveAviationPlanAndTaskSendAndDetail(JyBizTaskSendAviationPlanEntity aviationPlanEntity,
                                                     JyBizTaskSendVehicleEntity sendVehicleEntity,
                                                     JyBizTaskSendVehicleDetailEntity sendVehicleDetailEntity) {
        if (!Objects.isNull(sendVehicleEntity)) {

            logInfo("初始化派车单.{}", JsonHelper.toJson(sendVehicleEntity));

            taskSendVehicleService.initAviationTaskSendVehicle(sendVehicleEntity);

            // 创建发货调度任务
            createSendScheduleTask(sendVehicleEntity);
        }
        if (!Objects.isNull(sendVehicleDetailEntity)) {

            logInfo("初始化派车单明细.{}", JsonHelper.toJson(sendVehicleDetailEntity));

            if (taskSendVehicleDetailService.saveAviationTaskSendDetail(sendVehicleDetailEntity) > 0) {
                // 首次创建发货流向时，同时生成批次
                saveSendCode(sendVehicleDetailEntity);
            }
        }
        //航空计划
        if(!Objects.isNull(aviationPlanEntity)) {
            jyBizTaskSendAviationPlanService.initTaskSendVehicle(aviationPlanEntity);
        }
        return true;
    }
    /**
     * 调用场景： 消费封车消息时，根据封车批次反查jy发货任务，将未封车任务状态置为封车
     *  解决问题：非新版app发货岗封车后，新版发货任务状态依然未封车
     * @param detailBizId
     * @param taskSend
     * @param myBody
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.syncSendTaskSealStatusHandler",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean syncSendTaskSealStatusHandler(String detailBizId, JyBizTaskSendVehicleEntity taskSend, SealSyncOpenCloseSendTaskDto myBody) {
        JyBizTaskSendVehicleDetailEntity taskSendDetail = taskSendVehicleDetailService.findByBizId(detailBizId);
        //从未封车（待发货、发货中、待封车）改为封车状态
        if(!JyBizTaskSendDetailStatusEnum.TO_SEND.getCode().equals(taskSendDetail.getVehicleStatus()) && !JyBizTaskSendDetailStatusEnum.SENDING.getCode().equals(taskSendDetail.getVehicleStatus()) && !JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode().equals(taskSendDetail.getVehicleStatus())) {
            if(log.isInfoEnabled()) {
                log.info("封车同步新版发货任务数据，任务{}|{}状态为{}，不做处理(仅处理未封车状态)", taskSendDetail.getSendVehicleBizId(), taskSendDetail.getBizId(), taskSendDetail.getVehicleStatus());
            }
            return true;
        }
        //查看该流向下是否多批次，如果多批次需要保证每个批次都封车才修改状态
        List<String> batchCodesList = jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(taskSendDetail.getBizId());
        if(!CollectionUtils.isEmpty(batchCodesList)) {
            Set toSealBatchCodeSet = new HashSet();
            Set nullBatchCodeSet = new HashSet();
            for(String batchCode : batchCodesList) {
                if(myBody.getSingleBatchCode().equals(batchCode)) {
                   continue;
                }
                if(!newsealVehicleService.newCheckSendCodeSealed(batchCode, new StringBuffer())) {
                    Integer itemNum = sendMDao.countBoxCodeNumBySendCode(batchCode, taskSendDetail.getStartSiteId().intValue());
                    if(NumberHelper.gt0(itemNum)) {
                        toSealBatchCodeSet.add(batchCode);
                    }else {
                        nullBatchCodeSet.add(batchCode);
                    }
                }
            }
            if(toSealBatchCodeSet.size() > 0) {
                if(log.isInfoEnabled()) {
                    log.info("该发货流向【】下有批次{}个，存在未封车的非空批次【{}】，状态不做变更", taskSendDetail.getBizId(), batchCodesList.size(), JsonHelper.toJson(toSealBatchCodeSet));
                }
                return true;
            }
            if(nullBatchCodeSet.size() > 0) {
                if(log.isInfoEnabled()) {
                    log.info("该发货流向【】下有批次{}个，存在未封车的空批次【{}】，执行状态变更", taskSendDetail.getBizId(), batchCodesList.size(), JsonHelper.toJson(nullBatchCodeSet));
                }
            }
        }
        //发货主表
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(DEFAULT_USER);
        taskSend.setUpdateUserName(DEFAULT_USER);
        //发货明细表
        taskSendDetail.setSealCarTime(new Date(myBody.getOperateTime()));
        taskSendDetail.setUpdateTime(taskSend.getUpdateTime());
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        //发货航空表（特殊场景）
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = null;
        if(JySendTaskTypeEnum.AVIATION.getCode().equals(taskSendDetail.getTaskType())) {
            JyBizTaskSendAviationPlanEntity sendAviationPlanEntity = jyBizTaskSendAviationPlanService.findByBizId(taskSend.getBizId());
            //只处理未发货
            if(!Objects.isNull(sendAviationPlanEntity) && (JyBizTaskSendDetailStatusEnum.TO_SEND.getCode().equals(taskSendDetail.getVehicleStatus()) || JyBizTaskSendDetailStatusEnum.SENDING.getCode().equals(taskSendDetail.getVehicleStatus()) || JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode().equals(taskSendDetail.getVehicleStatus()))){
                aviationPlanEntity = new JyBizTaskSendAviationPlanEntity();
                aviationPlanEntity.setTaskStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());//航空任务不存在主子状态不一致场景，不需要计算主任务取最小子任务状态，所有表状态相同，直接置为封车状态
                aviationPlanEntity.setUpdateTime(taskSend.getUpdateTime());
                aviationPlanEntity.setUpdateUserName(taskSend.getUpdateUserName());
                aviationPlanEntity.setUpdateUserErp(taskSend.getUpdateUserErp());
                aviationPlanEntity.setBizId(taskSend.getBizId());
            }
        }

        sendVehicleTransactionManager.updateTaskStatus(taskSend, taskSendDetail, JyBizTaskSendDetailStatusEnum.SEALED);
        if(!Objects.isNull(aviationPlanEntity)) {
            jyBizTaskSendAviationPlanService.updateStatus(aviationPlanEntity);
        }
        if(log.isInfoEnabled()) {
            log.info("封车同步运输任务状态变更结束，detailBizId={}", detailBizId);
        }
        return true;
    }


    /**
     * jy主子发货任务删除：yn=0
     * @param detailBizId
     * @return
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.deleteSendTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteSendTask(String detailBizId) {
        //封车批次关联的bizId是自建任务：yn=0
        JyBizTaskSendVehicleDetailEntity taskSendDetail = taskSendVehicleDetailService.findByBizId(detailBizId);
        if(Objects.isNull(taskSendDetail)) {
            if(log.isInfoEnabled()) {
                log.info("封车同步自建任务删除未查到该任务信息，bizId={}", detailBizId);
            }
            return true;
        }
        Date now = new Date();
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(taskSendDetail.getSendVehicleBizId());
        entity.setYn(Constants.YN_NO);
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(DEFAULT_USER);
        entity.setUpdateUserName(DEFAULT_USER);
        taskSendVehicleService.updateSendVehicleTask(entity);
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(taskSendDetail.getSendVehicleBizId());
        detailEntity.setYn(Constants.YN_NO);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(DEFAULT_USER);
        detailEntity.setUpdateUserName(DEFAULT_USER);
        taskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        if(log.isInfoEnabled()) {
            log.info("封车同步自建任务删除结束，bizId={}", taskSendDetail.getSendVehicleBizId());
        }
        return true;
    }

}
