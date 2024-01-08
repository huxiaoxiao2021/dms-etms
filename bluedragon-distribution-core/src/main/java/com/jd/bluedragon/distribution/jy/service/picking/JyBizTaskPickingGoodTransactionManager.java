package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/12/21 21:34
 * @Description
 */
@Service
public class JyBizTaskPickingGoodTransactionManager {
    private static final Logger log = LoggerFactory.getLogger(JyBizTaskPickingGoodTransactionManager.class);

    @Autowired
    private JyBizTaskPickingGoodService jyBizTaskPickingGoodService;
    @Autowired
    private JyPickingSendRecordService jyPickingSendRecordService;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }


    /**
     * 首单提货逻辑
     * 1、更新提货任务状态
     * 2、分配提货调度任务数据
     * @param scanDto
     * @return
     */
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyBizTaskPickingGoodTransactionManager.startPickingGoodTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean startPickingGoodTask(JyPickingGoodScanDto scanDto) {
//        Integer count = jyPickingSendRecordService.countTaskRealScanItemNum(scanDto.getBizId(), scanDto.getSiteId());
//        if (NumberHelper.gt0(count)) {
//            return true;
//        }
        JyBizTaskPickingGoodEntity entity = jyBizTaskPickingGoodService.findByBizIdWithYn(scanDto.getBizId(), false);
        if(Objects.isNull(entity) || !PickingGoodStatusEnum.TO_PICKING.getCode().equals(entity.getStatus())) {
            return true;
        }
        logInfo("提货任务{}首次扫描逻辑开始.{}", JsonHelper.toJson(scanDto));

        JyBizTaskPickingGoodEntityCondition updateEntity = new JyBizTaskPickingGoodEntityCondition();
        updateEntity.setNextSiteId(scanDto.getSiteId());
        updateEntity.setBizId(scanDto.getBizId());
        updateEntity.setStatus(PickingGoodStatusEnum.PICKING.getCode());
        Long startTime = scanDto.getOperatorTime() - 2000l;//开始时间设置在第一单前几秒，区分边界
        updateEntity.setPickingStartTime(new Date());
        updateEntity.setUpdateTime(new Date(startTime));
        updateEntity.setUpdateUserErp(scanDto.getUser().getUserErp());
        updateEntity.setUpdateUserName(scanDto.getUser().getUserName());
        jyBizTaskPickingGoodService.updateTaskByBizIdWithCondition(updateEntity);
        logInfo("提货任务{}状态改为开始提货中", scanDto.getBizId(), startTime);
        //分配调度任务
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(scanDto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.PICKING.getCode());
        req.setDistributionType(JyScheduleTaskDistributionTypeEnum.GROUP.getCode());
        req.setDistributionTarget(scanDto.getGroupCode());
        req.setDistributionTime(new Date(startTime));
        req.setOpeUser(scanDto.getUser().getUserErp());
        req.setOpeUserName(scanDto.getUser().getUserName());
        req.setOpeTime(req.getDistributionTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.distributeAndStartScheduleTask(req);
        if(Objects.isNull(jyScheduleTaskResp)){
            log.error("提货岗分配调度任务失败返回null，req={}.scanDto={}", JsonUtils.toJSONString(req), JsonUtils.toJSONString(scanDto));
            throw new JyBizException(String.format("分配调度任务失败！bizId:%s",scanDto.getBizId()));
        }
        return true;
    }

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchInsertPickingGoodTask(List<JyBizTaskPickingGoodEntity> taskEntityList, List<JyBizTaskPickingGoodSubsidiaryEntity> subsidiaryEntityList) {
        //todo zcf  批量插入任务



        //调度任务初始化
        createUnSealScheduleTask(taskEntityList);

    }
    private boolean createUnSealScheduleTask(List<JyBizTaskPickingGoodEntity> entityList){
        entityList.forEach(entity -> {
            JyScheduleTaskReq req = new JyScheduleTaskReq();
            req.setBizId(entity.getBizId());
            req.setTaskType(JyScheduleTaskTypeEnum.PICKING.getCode());
            req.setOpeUser(entity.getCreateUserErp());
            req.setOpeUserName(entity.getCreateUserName());
            req.setOpeTime(entity.getCreateTime());
            JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.createScheduleTask(req);
            if(Objects.isNull(jyScheduleTaskResp)) {
                log.error("提货岗创建调度任务返回null, 参数={}，entity={}", JsonUtils.toJSONString(req), JsonUtils.toJSONString(entity));
                throw new JyBizException("创建调度任务异常返回null:" + entity.getBizId());
            }
        });
        return true;
    }

    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByBusinessNumber(PickingGoodTaskInitDto initDto) {
        List<JyBizTaskPickingGoodEntity> allTask = jyBizTaskPickingGoodService.findAllTaskByBusinessNumber(initDto.getBusinessNumber());
        if(CollectionUtils.isEmpty(allTask)) {
            return;
        }
        jyBizTaskPickingGoodService.deleteByBusinessNumber(initDto.getBusinessNumber());
        logInfo("空铁提货计划消费生成提货任务,删除批次【{}】已有任务成功，当前下发任务信息：{}", JsonHelper.toJson(initDto));
        // 关闭调度任务
        this.closeScheduleTask(allTask);
    }
    /**
     * 关闭调度任务
     * @param entityList
     * @return
     */
    private boolean closeScheduleTask(List<JyBizTaskPickingGoodEntity> entityList){
        entityList.forEach(entity -> {
            JyScheduleTaskReq req = new JyScheduleTaskReq();
            req.setBizId(entity.getBizId());
            req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.PICKING.getCode()));
            req.setOpeUser(entity.getUpdateUserErp());
            req.setOpeUserName(entity.getUpdateUserName());
            req.setOpeTime(new Date());
            JyScheduleTaskResp res = jyScheduleTaskManager.closeScheduleTask(req);
            if(Objects.isNull(res)) {
                log.error("提货岗关闭调度任务返回null, 参数={}，entity={}", JsonUtils.toJSONString(req), JsonUtils.toJSONString(entity));
                throw new JyBizException("关闭调度任务异常返回null:" + entity.getBizId());
            }
        });
        return true;
    }
}
