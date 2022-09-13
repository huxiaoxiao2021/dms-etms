package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadTaskCompleteDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
        // 封车时更新发货任务最晚封车时间
        if (JyBizTaskSendDetailStatusEnum.SEALED.equals(updateStatus)) {
            sendStatusQ.setLastSealCarTime(this.getLastSealCarTime(sendStatusQ));
        }
        return sendStatusQ;
    }

    private Date getLastSealCarTime(JyBizTaskSendVehicleEntity taskSend) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(taskSend.getStartSiteId(), taskSend.getBizId());
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
     * 获得发货明细最下的状态
     * @param sendDetail
     * @return
     */
    private Integer getTaskSendMinStatus(JyBizTaskSendVehicleDetailEntity sendDetail) {
        List<JyBizTaskSendCountDto> sendCountDtos = taskSendVehicleDetailService.sumByVehicleStatus(new JyBizTaskSendVehicleDetailEntity(sendDetail.getStartSiteId(), sendDetail.getSendVehicleBizId()));
        Collections.sort(sendCountDtos, new Comparator<JyBizTaskSendCountDto>() {
            @Override
            public int compare(JyBizTaskSendCountDto o1, JyBizTaskSendCountDto o2) {
                return o1.getVehicleStatus().compareTo(o2.getVehicleStatus());
            }
        });

        return sendCountDtos.get(0).getVehicleStatus();
    }
    /**
     * 1、按批次查询相应的子任务列表
     * 2、回退任务状态
     * @param sendCodeList
     * @param operateUserCode
     * @param operateTime
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "SendVehicleTransactionManager.resetSendStatusForUnseal",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)    
	public boolean resetSendStatusForUnseal(List<String> sendCodeList, String operateUserCode, Date operateTime) {
		List<JySendCodeEntity> jySendCodeList = this.jySendCodeService.queryDataListBySendCodeList(sendCodeList);
		if(CollectionUtils.isEmpty(jySendCodeList)){
			this.logInfo("根据取消封车批次{}查询jySendCode列表为空！", JsonHelper.toJson(sendCodeList));
			return true;
		}
		for(JySendCodeEntity jySendCode:jySendCodeList) {
			this.logInfo("取消封车-发货任务状态回退{}",JsonHelper.toJson(jySendCode));
			
			JyBizTaskSendVehicleEntity taskSend = new JyBizTaskSendVehicleEntity();
			JyBizTaskSendVehicleDetailEntity sendDetail = new JyBizTaskSendVehicleDetailEntity();
			taskSend.setBizId(jySendCode.getSendVehicleBizId());
			taskSend.setUpdateTime(operateTime);
			taskSend.setUpdateUserErp(operateUserCode);
			taskSend.setUpdateUserName(operateUserCode);
			
			sendDetail.setBizId(jySendCode.getSendVehicleBizId());
			sendDetail.setSendVehicleBizId(jySendCode.getSendDetailBizId());
			sendDetail.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode());
			Integer[] siteCodes = BusinessUtil.getSiteCodeBySendCode(jySendCode.getSendCode());
			if(siteCodes.length == 2 && siteCodes[0]>0 && siteCodes[1]>0  ) {
				sendDetail.setStartSiteId(new Long(siteCodes[0]));
				sendDetail.setEndSiteId(new Long(siteCodes[1]));
			}
			sendDetail.setUpdateTime(operateTime);
			sendDetail.setUpdateUserErp(operateUserCode);
			sendDetail.setUpdateUserName(operateUserCode);
			
			this.updateStatusWithoutCompare(taskSend, sendDetail, JyBizTaskSendDetailStatusEnum.TO_SEAL);
		}
		return true;
	}
}
