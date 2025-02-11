package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.distribution.jy.service.unload.JyBizTaskUnloadVehicleStageService;
import com.jd.bluedragon.distribution.jy.service.unload.UnloadVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.transport.dto.StopoverQueryDto;
import com.jd.bluedragon.distribution.transport.enums.StopoverSiteUnloadAndLoadTypeEnum;
import com.jd.bluedragon.distribution.transport.service.TransportRelatedService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JyUnloadScanConsumer
 * @Description 拣运卸车扫描
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service("jyUnloadScanConsumer")
public class JyUnloadScanConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyUnloadScanConsumer.class);

    private static final int UNLOAD_SCAN_BIZ_EXPIRE = 6;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JyUnloadDao jyUnloadDao;

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private UnloadVehicleTransactionManager transactionManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;

    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    JyBizTaskUnloadVehicleStageService jyBizTaskUnloadVehicleStageService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JyBizTaskUnloadVehicleDao jyBizTaskUnloadVehicleDao;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private TransportRelatedService transportRelatedService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.jyUnloadScanConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyUnloadScanConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyUnloadScanConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        UnloadScanDto unloadScanDto = JsonHelper.fromJson(message.getText(), UnloadScanDto.class);
        if (unloadScanDto == null) {
            logger.error("jyUnloadScanConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        doUnloadScan(unloadScanDto);

    }

    /**
     * 保存扫描记录，发运单全程跟踪
     * @param unloadScanDto
     */
    private void doUnloadScan(UnloadScanDto unloadScanDto) {

        // 首次扫描分配卸车任务，变更任务状态
        startAndDistributeUnloadTask(unloadScanDto);

        JyUnloadEntity unloadEntity = copyFromDto(unloadScanDto);

        if (jyUnloadDao.insert(unloadEntity) <= 0) {
            logger.error("保存卸车扫描记录异常. {}", JsonHelper.toJson(unloadEntity));
            throw new RuntimeException("保存卸车扫描记录异常");
        }

       /* 说明： 补扫任务一生成就是完成状态，后续不能再修改完成时间。
        原因： 计提在任务完成时开始计算，
            第一次任务完成时间在21号0点，为上一计提数据，21号7点前补扫包裹100件
            25号0点再次补扫变更完成时间，任务为第二计提周期，补扫了200个包裹  此时本计提周期会推送 100 + 200 个包裹，计提重复
        结果：任务完成时间只记录第一次
        */
        // 转运补扫子任务完结
//        if (JyBizTaskSourceTypeEnum.TRANSPORT.getCode().equals(unloadScanDto.getTaskType())) {
//            if (unloadScanDto.getSupplementary()) {
//                JyBizTaskUnloadVehicleStageEntity condition = new JyBizTaskUnloadVehicleStageEntity();
//                condition.setBizId(unloadScanDto.getStageBizId());
//                condition.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
//                condition.setEndTime(new Date());
//                condition.setUpdateTime(new Date());
//                condition.setUpdateUserErp(unloadScanDto.getUpdateUserErp());
//                condition.setUpdateUserName(unloadScanDto.getUpdateUserName());
//                jyBizTaskUnloadVehicleStageService.updateStatusByBizId(condition);
//            }
//        }

        // 插入验货或收货任务，发全程跟踪
        addTaskPersistent(unloadScanDto);
    }

    private JyUnloadEntity copyFromDto(UnloadScanDto unloadScanDto) {
        JyUnloadEntity unloadEntity = new JyUnloadEntity();
        unloadEntity.setBizId(unloadScanDto.getBizId());
        unloadEntity.setSealCarCode(unloadScanDto.getSealCarCode());
        unloadEntity.setVehicleNumber(unloadScanDto.getVehicleNumber());
        unloadEntity.setStartSiteId(unloadScanDto.getStartSiteId());
        unloadEntity.setManualCreatedFlag(unloadScanDto.getManualCreatedFlag());
        unloadEntity.setEndSiteId(unloadScanDto.getEndSiteId());
        unloadEntity.setOperateSiteId(unloadScanDto.getOperateSiteId());
        unloadEntity.setBarCode(unloadScanDto.getBarCode());
        unloadEntity.setOperateTime(unloadScanDto.getOperateTime());
        unloadEntity.setCreateUserErp(unloadScanDto.getCreateUserErp());
        unloadEntity.setCreateUserName(unloadScanDto.getCreateUserName());
        unloadEntity.setUpdateUserErp(unloadScanDto.getUpdateUserErp());
        unloadEntity.setUpdateUserName(unloadScanDto.getUpdateUserName());
        unloadEntity.setCreateTime(unloadScanDto.getCreateTime());
        unloadEntity.setUpdateTime(unloadScanDto.getUpdateTime());
        unloadEntity.setStageBizId(unloadScanDto.getStageBizId());
        unloadEntity.setMoreFlag(unloadScanDto.getMoreFlag());
        return unloadEntity;
    }

    private void addTaskPersistent(UnloadScanDto unloadScanDto) {
        InvokeResult<Boolean> taskResult = addInspectionTask(unloadScanDto);
        if (!taskResult.codeSuccess()) {
            Profiler.businessAlarm("dms.web.jyUnloadScanConsumer.addInspectionTask", "卸车扫描插入验货任务失败，将重试");
            logger.warn("卸车扫描插入验货任务失败，将重试. {}，{}", JsonHelper.toJson(unloadScanDto), JsonHelper.toJson(taskResult));
            // 再次尝试插入任务
            try {
                Thread.sleep(100);
                addInspectionTask(unloadScanDto);
            }
            catch (InterruptedException e) {
                logger.error("再次插入卸车验货任务异常. {}", JsonHelper.toJson(unloadScanDto), e);
            }
        }
    }

    /**
     * 扫描第一单时开始卸车任务
     * @param unloadScanDto
     */
    private void startAndDistributeUnloadTask(UnloadScanDto unloadScanDto) {
        if (!judgeStartScheduleTask(unloadScanDto)) {
            logger.warn("不满足开始卸车任务的条件, 扫描时间晚于任务结束的时间或任务已结束. {}", JsonHelper.toJson(unloadScanDto));
            return;
        }

        // 卸车任务首次扫描
        if (judgeBarCodeIsFirstScanFromTask(unloadScanDto)) {

            startJyScheduleTask(unloadScanDto);

            recordTaskMembers(unloadScanDto);

            updateTaskBusinessInfo(unloadScanDto);
        } else {
            this.updateAutoCloseTaskCacheLastScanTime(unloadScanDto);
        }
    }

    private void updateAutoCloseTaskCacheLastScanTime(UnloadScanDto unloadScanDto){
        // 更新最后扫描事件缓存，每次续期4小时
        final AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = dmsConfigManager.getPropertyConfig().getAutoCloseJyBizTaskConfigObj();
        if (autoCloseJyBizTaskConfig == null) {
            logger.info("pushBizTaskAutoCloseTask no config, will not push auto close task");
        } else {
            final String unloadBizLastScanTimeKey = this.getUnloadBizLastScanTimeKey(unloadScanDto.getBizId());
            redisClientOfJy.setEx(unloadBizLastScanTimeKey, unloadScanDto.getOperateTime().getTime() + "", 36, TimeUnit.HOURS);
        }
    }

    private void updateTaskBusinessInfo(UnloadScanDto unloadScanDto) {
        JyBizTaskUnloadVehicleEntity taskEntity = jyBizTaskUnloadVehicleDao.findByBizId(unloadScanDto.getBizId());
        if (taskEntity != null) {
            JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
            entity.setId(taskEntity.getId());
            entity.setUnloadStartTime(unloadScanDto.getOperateTime());
            entity.setRefGroupCode(unloadScanDto.getGroupCode());
            entity.setUpdateTime(new Date());
            // 处理是否只卸不装
            this.handleOnlyUnloadAttr(taskEntity, entity);
            jyBizTaskUnloadVehicleDao.updateOfBusinessInfoById(entity);
        }
    }

    /**
     * 处理只卸不装属性
     * @param taskEntity 任务数据
     */
    private void handleOnlyUnloadAttr(JyBizTaskUnloadVehicleEntity taskEntity, JyBizTaskUnloadVehicleEntity taskEntityUpdate) {
        try {
            logger.info("handleOnlyUnloadAttr {}", JsonHelper.toJson(taskEntity));
            final StopoverQueryDto stopoverQueryDto = new StopoverQueryDto();
            stopoverQueryDto.setSiteCode(taskEntity.getEndSiteId().intValue());
            stopoverQueryDto.setSealCarCode(taskEntity.getSealCarCode());
            stopoverQueryDto.setVehicleNumber(taskEntity.getVehicleNumber());
            final com.jd.dms.java.utils.sdk.base.Result<Integer> checkResult = transportRelatedService.queryStopoverLoadAndUnloadType(stopoverQueryDto);
            logger.info("handleOnlyUnloadAttr result {}", JsonHelper.toJson(checkResult));
            if(checkResult.isSuccess() && Objects.equals(checkResult.getData(), StopoverSiteUnloadAndLoadTypeEnum.ONLY_UNLOAD_NO_LOAD.getCode())){
                taskEntityUpdate.setOnlyUnloadNoLoad(Constants.YN_YES);
                logger.info("handleOnlyUnloadAttr match {}", JsonHelper.toJson(taskEntityUpdate));
            }
        } catch (Exception e) {
            logger.info("handleOnlyUnloadAttr exception {}", JsonHelper.toJson(taskEntity), e);
        }
    }


    /**
     * 判断是否触发开始调度任务
     * @param unloadScanDto
     * @return
     */
    private boolean judgeStartScheduleTask(UnloadScanDto unloadScanDto) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(unloadScanDto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        if (scheduleTask != null) {
            if (scheduleTask.getTaskEndTime() == null) {
                return true;
            }
            else {
                return unloadScanDto.getOperateTime().before(scheduleTask.getTaskEndTime());
            }
        }

        return false;
    }

    private void recordTaskMembers(UnloadScanDto unloadScanDto) {
        JyTaskGroupMemberEntity startData = new JyTaskGroupMemberEntity();
        startData.setRefGroupCode(unloadScanDto.getGroupCode());
        startData.setRefTaskId(unloadScanDto.getTaskId());

        startData.setSiteCode(unloadScanDto.getOperateSiteId().intValue());
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(startData.getSiteCode());
        startData.setOrgCode(baseSite != null ? baseSite.getOrgId() : -1);
        startData.setProvinceAgencyCode(baseSite == null ? Constants.EMPTY_FILL : baseSite.getProvinceAgencyCode());
        startData.setAreaHubCode(baseSite == null ? Constants.EMPTY_FILL : baseSite.getAreaCode());

        startData.setCreateUser(unloadScanDto.getCreateUserErp());
        startData.setCreateUserName(unloadScanDto.getCreateUserName());

        try {
            Result<Boolean> startTask = taskGroupMemberService.startTask(startData);

            logInfo("卸车任务[{}-{}]首次扫描记录组员。{}",
                    unloadScanDto.getBizId(), unloadScanDto.getVehicleNumber(), JsonHelper.toJson(unloadScanDto));

            logInfo("卸车任务首次扫描记录组员. {}, {}, {}", unloadScanDto.getBizId(), JsonHelper.toJson(unloadScanDto), JsonHelper.toJson(startTask));
        }
        catch (Exception e) {
            // 异常不重试
            logger.error("卸车任务记录组员异常. {}", JsonHelper.toJson(unloadScanDto), e);
        }
    }

    private void startJyScheduleTask(UnloadScanDto unloadScanDto) {
        try {
            logInfo("卸车任务[{}-{}]首次扫描，修改任务状态，锁定任务。{}",
                    unloadScanDto.getBizId(), unloadScanDto.getVehicleNumber(), JsonHelper.toJson(unloadScanDto));

            JyBizTaskUnloadDto taskUnloadDto = getJyBizTaskUnloadDto(unloadScanDto);
            transactionManager.drawUnloadTask(taskUnloadDto);
        }
        catch (JyBizException bizException) {
            logger.warn("卸车任务领取和分配发生业务异常，将重试！ {}", JsonHelper.toJson(unloadScanDto), bizException);

            // 任务分配失败，删除缓存
            redisClientOfJy.del(getUnloadBizCacheKey(unloadScanDto));

            throw bizException;
        }
        catch (Exception ex) {
            Profiler.businessAlarm("dms.web.jyUnloadScanConsumer.drawUnloadTask", "拣运卸车任务领取和分配失败");
            logger.error("卸车任务领取和分配失败. {}", JsonHelper.toJson(unloadScanDto), ex);

            // 任务分配失败，删除缓存
            redisClientOfJy.del(getUnloadBizCacheKey(unloadScanDto));

            throw new RuntimeException(ex);
        }
    }

    private JyBizTaskUnloadDto getJyBizTaskUnloadDto(UnloadScanDto unloadScanDto) {
        JyBizTaskUnloadDto taskUnloadDto = new JyBizTaskUnloadDto();
        taskUnloadDto.setBizId(unloadScanDto.getBizId());
        taskUnloadDto.setOperateUserErp(unloadScanDto.getCreateUserErp());
        taskUnloadDto.setOperateUserName(unloadScanDto.getCreateUserName());
        taskUnloadDto.setOperateTime(unloadScanDto.getOperateTime());
        taskUnloadDto.setGroupCode(unloadScanDto.getGroupCode());
        return taskUnloadDto;
    }

    /**
     * 判断该单号是否是本次卸车任务扫描的第一单
     * @param unloadScanDto
     * @return
     */
    private boolean judgeBarCodeIsFirstScanFromTask(UnloadScanDto unloadScanDto) {
        boolean firstScanned = false;
        String mutexKey = getUnloadBizCacheKey(unloadScanDto);
        if (redisClientOfJy.set(mutexKey, unloadScanDto.getBarCode(), UNLOAD_SCAN_BIZ_EXPIRE, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(unloadScanDto.getOperateSiteId(), unloadScanDto.getBizId());
            if (jyUnloadDao.findByBizId(queryDb) == null) {

                logInfo("卸车任务{}判定为首次扫描. {}", unloadScanDto.getBizId(), JsonHelper.toJson(unloadScanDto));

                firstScanned = true;
            }
        }

        return firstScanned;
    }

    private String getUnloadBizCacheKey(UnloadScanDto unloadScanDto) {
        return String.format(CacheKeyConstants.JY_UNLOAD_TASK_FIRST_SCAN_KEY, unloadScanDto.getBizId());
    }
    private String getUnloadBizLastScanTimeKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_TASK_LAST_SCAN_TIME_KEY, bizId);
    }

    /**
     * 插入验货或收货任务
     * @param unloadScanDto
     * @return
     */
    private InvokeResult<Boolean> addInspectionTask(UnloadScanDto unloadScanDto) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(unloadScanDto.getBarCode()));
        inspectionVO.setSiteCode(unloadScanDto.getOperateSiteId().intValue());

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(inspectionVO.getSiteCode());
        inspectionVO.setSiteName(baseSite != null ? baseSite.getSiteName() : StringUtils.EMPTY);

        inspectionVO.setUserCode(unloadScanDto.getCreateUserId());
        inspectionVO.setUserName(unloadScanDto.getCreateUserName());

        inspectionVO.setOperateTime(DateHelper.formatDateTime(unloadScanDto.getOperateTime()));
        inspectionVO.setOperatorData(unloadScanDto.getOperatorData());
        return inspectionService.addInspection(inspectionVO, InspectionBizSourceEnum.JY_UNLOAD_INSPECTION);
    }

    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
}
