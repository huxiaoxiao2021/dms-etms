package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.unload.JyBizTaskUnloadVehicleStageDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSourceTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskStageTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.JyBizTaskUnloadVehicleStageService;
import com.jd.bluedragon.distribution.jy.service.unload.UnloadVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.TASK_NO_FOUND_BY_PARAMS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.TASK_NO_FOUND_BY_PARAMS_MESSAGE;

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

    @Override
    @JProfiler(jKey = "DMS.WORKER.jyUnloadScanConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     *
     * @param unloadScanDto
     */
    private void doUnloadScan(UnloadScanDto unloadScanDto) {

        // 首次扫描分配卸车任务，变更任务状态
        startAndDistributeUnloadTask(unloadScanDto);

        JyUnloadEntity unloadEntity = copyFromDto(unloadScanDto);

        Long id = null;
        if (JyBizTaskSourceTypeEnum.TRANSPORT.getCode().equals(unloadScanDto.getTaskType())) {
            // 查询子任务bizId
            JyBizTaskUnloadVehicleStageEntity entity = getCurrentUnloadTaskStage(unloadScanDto);
            if (ObjectHelper.isNotNull(entity)){
                unloadEntity.setStageBizId(entity.getBizId());
                id = entity.getId();
            }
        }

        if (jyUnloadDao.insert(unloadEntity) <= 0) {
            logger.error("保存卸车扫描记录异常. {}", JsonHelper.toJson(unloadEntity));
            throw new RuntimeException("保存卸车扫描记录异常");
        }
        // 转运补扫子任务完结
        if (JyBizTaskSourceTypeEnum.TRANSPORT.getCode().equals(unloadScanDto.getTaskType()) && id != null) {
            if (unloadScanDto.getSupplementary()) {
                JyBizTaskUnloadVehicleStageEntity condition = new JyBizTaskUnloadVehicleStageEntity();
                condition.setId(id);
                condition.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
                jyBizTaskUnloadVehicleStageService.updateByPrimaryKeySelective(condition);
            }
        }

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
            } catch (InterruptedException e) {
                logger.error("再次插入卸车验货任务异常. {}", JsonHelper.toJson(unloadScanDto), e);
            }
        }
    }

    /**
     * 扫描第一单时开始卸车任务
     *
     * @param unloadScanDto
     */
    private void startAndDistributeUnloadTask(UnloadScanDto unloadScanDto) {

        // 卸车任务首次扫描
        if (judgeBarCodeIsFirstScanFromTask(unloadScanDto)) {

            startJyScheduleTask(unloadScanDto);

            recordTaskMembers(unloadScanDto);

            if (JyBizTaskSourceTypeEnum.TRANSPORT.getCode().equals(unloadScanDto.getTaskType())) {
                // 初始化子任务
                jyBizTaskUnloadVehicleStageService.insertSelective(generateUnloadTaskStage(unloadScanDto));
            }
        } else {
            // 转运卸车非首次扫描
            if (JyBizTaskSourceTypeEnum.TRANSPORT.getCode().equals(unloadScanDto.getTaskType())) {
                // 查询子任务bizId
                JyBizTaskUnloadVehicleStageEntity entity = getCurrentUnloadTaskStage(unloadScanDto);
                if (entity == null) {
                    // 初始化子任务
                    jyBizTaskUnloadVehicleStageService.insertSelective(generateUnloadTaskStage(unloadScanDto));
                }
            }
        }
    }

    private JyBizTaskUnloadVehicleStageEntity getCurrentUnloadTaskStage(UnloadScanDto unloadScanDto) {
        JyBizTaskUnloadVehicleStageEntity condition = new JyBizTaskUnloadVehicleStageEntity();
        condition.setUnloadVehicleBizId(unloadScanDto.getBizId());
        condition.setType(unloadScanDto.getSupplementary() ? JyBizTaskStageTypeEnum.SUPPLEMENT.getCode() : JyBizTaskStageTypeEnum.HANDOVER.getCode());
        if (!unloadScanDto.getSupplementary()) {
            condition.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
        }
        return jyBizTaskUnloadVehicleStageService.queryCurrentStage(condition);
    }

    private JyBizTaskUnloadVehicleStageEntity generateUnloadTaskStage(UnloadScanDto unloadScanDto) {
        Date now = new Date();
        JyBizTaskUnloadVehicleStageEntity firstStage = new JyBizTaskUnloadVehicleStageEntity();
        firstStage.setUnloadVehicleBizId(unloadScanDto.getBizId());
        // 用于判断当前子任务的序号
        List<Long> idList = jyBizTaskUnloadVehicleStageService.countByBizId(unloadScanDto.getBizId());
        int serialNumber = CollectionUtils.isEmpty(idList) ? 1 : idList.size() + 1;
        firstStage.setBizId(unloadScanDto.getBizId() + Constants.SEPARATOR_HYPHEN + serialNumber);
        // firstStage.setBizId(jyBizTaskUnloadVehicleStageService.generateStageBizId(unloadScanDto.getBizId()));
        firstStage.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
        firstStage.setType(unloadScanDto.getSupplementary() ? JyBizTaskStageTypeEnum.SUPPLEMENT.getCode() : JyBizTaskStageTypeEnum.HANDOVER.getCode());
        firstStage.setStartTime(now);
        firstStage.setCreateTime(now);
        firstStage.setUpdateTime(now);
        firstStage.setCreateUserErp(unloadScanDto.getCreateUserErp());
        firstStage.setCreateUserName(unloadScanDto.getCreateUserName());
        firstStage.setUpdateUserErp(unloadScanDto.getCreateUserErp());
        firstStage.setUpdateUserName(unloadScanDto.getCreateUserName());
        firstStage.setYn(Constants.YN_YES);
        return firstStage;
    }

    private void recordTaskMembers(UnloadScanDto unloadScanDto) {
        JyTaskGroupMemberEntity startData = new JyTaskGroupMemberEntity();
        startData.setRefGroupCode(unloadScanDto.getGroupCode());
        startData.setRefTaskId(unloadScanDto.getTaskId());

        startData.setSiteCode(unloadScanDto.getOperateSiteId().intValue());
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(startData.getSiteCode());
        startData.setOrgCode(baseSite != null ? baseSite.getOrgId() : -1);

        startData.setCreateUser(unloadScanDto.getCreateUserErp());
        startData.setCreateUserName(unloadScanDto.getCreateUserName());

        try {
            Result<Boolean> startTask = taskGroupMemberService.startTask(startData);

            logInfo("卸车任务[{}-{}]首次扫描记录组员。{}",
                    unloadScanDto.getBizId(), unloadScanDto.getVehicleNumber(), JsonHelper.toJson(unloadScanDto));

            logInfo("卸车任务首次扫描记录组员. {}, {}, {}", unloadScanDto.getBizId(), JsonHelper.toJson(unloadScanDto), JsonHelper.toJson(startTask));
        } catch (Exception e) {
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
        } catch (JyBizException bizException) {
            logger.warn("卸车任务领取和分配发生业务异常，将重试！ {}", JsonHelper.toJson(unloadScanDto), bizException);

            // 任务分配失败，删除缓存
            redisClientOfJy.del(getUnloadBizCacheKey(unloadScanDto));

            throw bizException;
        } catch (Exception ex) {
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
     *
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

    /**
     * 插入验货或收货任务
     *
     * @param unloadScanDto
     * @return
     */
    private InvokeResult<Boolean> addInspectionTask(UnloadScanDto unloadScanDto) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(unloadScanDto.getBarCode()));
        inspectionVO.setSiteCode(unloadScanDto.getOperateSiteId().intValue());

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(inspectionVO.getSiteCode());
        inspectionVO.setSiteName(baseSite != null ? baseSite.getSiteName() : StringUtils.EMPTY);

        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(unloadScanDto.getCreateUserErp());
        inspectionVO.setUserCode(baseStaff != null ? baseStaff.getStaffNo() : 0);
        inspectionVO.setUserName(unloadScanDto.getCreateUserName());

        inspectionVO.setOperateTime(DateHelper.formatDateTime(unloadScanDto.getOperateTime()));
        return inspectionService.addInspection(inspectionVO, InspectionBizSourceEnum.JY_UNLOAD_INSPECTION);
    }

    private void logInfo(String message, Object... objects) {
        if (logger.isInfoEnabled()) {
            logger.info(message, objects);
        }
    }
}
