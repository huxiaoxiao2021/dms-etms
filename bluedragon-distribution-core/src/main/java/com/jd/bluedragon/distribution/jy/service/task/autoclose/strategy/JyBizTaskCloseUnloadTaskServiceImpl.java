package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadPreviewData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskContextDto;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.service.unload.UnloadVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 解封车关闭任务实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 19:46:56 周二
 */
@Slf4j
@Service("jyBizTaskCloseUnloadTaskService")
public class JyBizTaskCloseUnloadTaskServiceImpl extends JYBizTaskCloseAbstractService {

    @Autowired
    private JyBizTaskUnloadVehicleService taskUnloadVehicleService;

    @Autowired
    private IJyUnloadVehicleService jyUnloadVehicleService;

    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;

    @Autowired
    private UnloadVehicleTransactionManager transactionManager;

    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private TaskService taskService;

    /**
     * 关闭任务接口
     *
     * @param autoCloseTaskPo 关闭入参
     * @return 处理结果
     * @author fanggang7
     * @time 2023-01-31 17:00:46 周二
     */
    @Override
    public Result<Void> closeTask(AutoCloseTaskPo autoCloseTaskPo) {
        log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask param {}", JSON.toJSONString(autoCloseTaskPo));
        Result<Void> result = Result.success();
        try {
            // 查询主任务
            final JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist = taskUnloadVehicleService.findByBizId(autoCloseTaskPo.getBizId());
            if(taskUnloadVehicleExist == null){
                log.warn("JyBizTaskCloseUnloadTaskServiceImpl.closeTask task not exist param {}", JSON.toJSONString(autoCloseTaskPo));
                return result;
            }
            List<Integer> needHandleStatus = new ArrayList<>(Arrays.asList(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode(), JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), JyBizTaskUnloadStatusEnum.UN_LOADING.getCode()));
            if (!needHandleStatus.contains(taskUnloadVehicleExist.getVehicleStatus())) {
                log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask task status has changed already {}, {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                return result;
            }

            // 如果【待卸状态中未卸车完成】，且状态不是待卸，可以视为任务不用执行
            if (Objects.equals(JyAutoCloseTaskBusinessTypeEnum.WAIT_UNLOAD_NOT_FINISH.getCode(), autoCloseTaskPo.getTaskBusinessType())) {
                if(!Objects.equals(taskUnloadVehicleExist.getVehicleStatus(), JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode())){
                    log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask task no need handle param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                    return result;
                }
            }
            // 如果【卸车后未卸车完成】
            if (Objects.equals(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode(), autoCloseTaskPo.getTaskBusinessType())) {
                // 如果状态不是卸车中，可以视为任务不用执行
                if(!Objects.equals(taskUnloadVehicleExist.getVehicleStatus(), JyBizTaskUnloadStatusEnum.UN_LOADING.getCode())){
                    log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask task no need handle param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                    return result;
                }
                // 还在卸车中，判断最后的卸车扫描时间是否更新，如果已经更新，则将本任务的执行时间延后
                else {
                    final String unloadBizLastScanTimeKey = this.getUnloadBizLastScanTimeKey(taskUnloadVehicleExist.getBizId());
                    final String unloadBizLastScanTimeValStr = redisClientOfJy.get(unloadBizLastScanTimeKey);
                    if (unloadBizLastScanTimeValStr != null) {
                        long unloadBizLastScanTimeVal = Long.parseLong(unloadBizLastScanTimeValStr);
                        AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = uccPropertyConfiguration.getAutoCloseJyBizTaskConfig();
                        if(autoCloseJyBizTaskConfig == null){
                            autoCloseJyBizTaskConfig = new AutoCloseJyBizTaskConfig();
                            autoCloseJyBizTaskConfig.setUnloadingNotFinishLazyTime(4);
                        }
                        final long executeTimeMillSeconds = unloadBizLastScanTimeVal + autoCloseJyBizTaskConfig.getUnloadingNotFinishLazyTime() * 60 * 60 * 60 * 1000L;
                        Task taskUpdate = new Task();
                        taskUpdate.setExecuteTime(new Date(executeTimeMillSeconds));
                        taskUpdate.setId(autoCloseTaskPo.getTask().getId());
                        taskService.updateBySelective(taskUpdate);
                        log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask lazy execute param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                        // 延迟执行，直接返回失败
                        return result.toFail();
                    }
                }
            }

            final AutoCloseTaskContextDto autoCloseTaskContextDto = new AutoCloseTaskContextDto();
            BeanCopyUtil.copy(autoCloseTaskPo, autoCloseTaskContextDto);
            autoCloseTaskContextDto.setOperateUserErp(this.sysOperateUser);
            autoCloseTaskContextDto.setOperateUserName(this.sysOperateUserName);
            autoCloseTaskContextDto.setBizId(taskUnloadVehicleExist.getBizId());

            // 关闭主任务 + 调度任务
            this.completeTask(autoCloseTaskContextDto);
            // 更新任务业务数据
            this.saveOrUpdateOfBusinessInfo(autoCloseTaskContextDto, taskUnloadVehicleExist);
            // 签退任务关联的签到记录
            this.finishUnloadTaskGroup(autoCloseTaskContextDto);
        } catch (Exception e) {
            log.error("JyBizTaskCloseUnloadTaskServiceImpl.closeTask exception {}", JSON.toJSONString(autoCloseTaskPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 组装更新主任务状态实体
     */
    private boolean saveOrUpdateOfBusinessInfo(AutoCloseTaskContextDto autoCloseTaskContextDto, JyBizTaskUnloadVehicleEntity bizTaskUnloadVehicleExist) {
        JyBizTaskUnloadVehicleEntity updateData = new JyBizTaskUnloadVehicleEntity();
        updateData.setBizId(bizTaskUnloadVehicleExist.getBizId());
        updateData.setUnloadFinishTime(new Date(autoCloseTaskContextDto.getOperateTime()));

        // 查询是否异常
        List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(bizTaskUnloadVehicleExist.getBizId()));
        if (CollectionUtils.isEmpty(unloadAggList)) {
            log.warn("判断卸车任务是否完成查询AGG为空.{}", JsonHelper.toJson(bizTaskUnloadVehicleExist));
            // return false;
        }
        UnloadPreviewData previewData = new UnloadPreviewData();
        final boolean unloadTaskNormalFlag = jyUnloadVehicleService.judgeUnloadTaskNormal(previewData, unloadAggList);

        updateData.setAbnormalFlag(unloadTaskNormalFlag ? Constants.NUMBER_ZERO : Constants.CONSTANT_NUMBER_ONE);
        updateData.setUpdateUserErp(this.sysOperateUser);
        updateData.setUpdateUserName(this.sysOperateUser);
        updateData.setUpdateTime(new Date(autoCloseTaskContextDto.getOperateTime()));
        updateData.setMoreCount(previewData.getMoreScanLocalCount() + previewData.getMoreScanOutCount());
        updateData.setLessCount(previewData.getToScanCount());

        return taskUnloadVehicleService.saveOrUpdateOfBusinessInfo(updateData);
    }

    /**
     * 关闭主任务
     */
    private boolean completeTask(AutoCloseTaskContextDto autoCloseTaskContextDto) {
        JyBizTaskUnloadDto completeDto = new JyBizTaskUnloadDto();
        completeDto.setBizId(autoCloseTaskContextDto.getBizId());
        completeDto.setOperateUserErp(autoCloseTaskContextDto.getOperateUserErp());
        completeDto.setOperateUserName(autoCloseTaskContextDto.getOperateUserName());
        completeDto.setOperateTime(new Date());
        return transactionManager.completeUnloadTask(completeDto);
    }

    /**
     * 签退任务关联的签到记录
     */
    private void finishUnloadTaskGroup(AutoCloseTaskContextDto autoCloseTaskContextDto) {
        JyTaskGroupMemberEntity endData = new JyTaskGroupMemberEntity();
        endData.setRefTaskId(autoCloseTaskContextDto.getBizId());
        endData.setUpdateUser(autoCloseTaskContextDto.getOperateUserErp());
        endData.setUpdateUserName(autoCloseTaskContextDto.getOperateUserName());
        endData.setUpdateTime(new Date(autoCloseTaskContextDto.getOperateTime()));
        com.jd.bluedragon.distribution.api.response.base.Result<Boolean> result = taskGroupMemberService.endTask(endData);

        if (log.isInfoEnabled()) {
            log.info("卸车完成关闭小组任务. data:{}, result:{}", JsonHelper.toJson(endData), JsonHelper.toJson(result));
        }
    }

    private String getUnloadBizLastScanTimeKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_TASK_LAST_SCAN_TIME_KEY, bizId);
    }
}
