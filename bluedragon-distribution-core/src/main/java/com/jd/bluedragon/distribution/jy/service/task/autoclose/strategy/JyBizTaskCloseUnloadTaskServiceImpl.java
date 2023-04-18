package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadCompleteRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadPreviewData;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadCompleteDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadPreviewDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadPreviewRespDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizOpereateSourceEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadTaskTypeEnum;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.JyBizTaskAutoCloseHelperService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskContextDto;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 解封车关闭任务实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 19:46:56 周二
 */
@Slf4j
@Service("jyBizTaskCloseUnloadTaskService")
public class JyBizTaskCloseUnloadTaskServiceImpl extends JyBizTaskCloseAbstractService {

    @Autowired
    private JyBizTaskUnloadVehicleService taskUnloadVehicleService;

    @Autowired
    private JyUnloadVehicleTysService jyUnloadVehicleTysService;

    @Autowired
    private IJyUnloadVehicleService jyUnloadVehicleService;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private JyBizTaskAutoCloseHelperService jyBizTaskAutoCloseHelperService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    @Qualifier("jyGroupService")
    private JyGroupService jyGroupService;

    /**
     * 关闭任务接口
     *
     * @param autoCloseTaskPo 关闭入参
     * @return 处理结果
     * @author fanggang7
     * @time 2023-01-31 17:00:46 周二
     */
    @Override
    @JProfiler(jKey = "DMS.WORKER.JyBizTaskCloseUnloadTaskServiceImpl.closeTask", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
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
                    log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask WAIT_UNLOAD_NOT_FINISH task no need handle param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                    return result;
                }
            }
            // 如果【卸车后未卸车完成】
            if (Objects.equals(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode(), autoCloseTaskPo.getTaskBusinessType())) {
                // 如果状态不是卸车中，可以视为任务不用执行
                if(!Objects.equals(taskUnloadVehicleExist.getVehicleStatus(), JyBizTaskUnloadStatusEnum.UN_LOADING.getCode())){
                    log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask UNLOADING_NOT_FINISH task no need handle param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                    return result;
                }
                // 还在卸车中，判断最后的卸车扫描时间是否更新，如果已经更新，则将本任务的执行时间延后
                else {
                    final String unloadBizLastScanTimeKey = this.getUnloadBizLastScanTimeKey(taskUnloadVehicleExist.getBizId());
                    final String unloadBizLastScanTimeValStr = redisClientOfJy.get(unloadBizLastScanTimeKey);
                    if (unloadBizLastScanTimeValStr != null) {
                        long unloadBizLastScanTimeVal = Long.parseLong(unloadBizLastScanTimeValStr);
                        if(unloadBizLastScanTimeVal > autoCloseTaskPo.getOperateTime()){
                            AutoCloseTaskMq autoCloseTaskMqNew = new AutoCloseTaskMq();
                            autoCloseTaskMqNew.setTaskBusinessType(autoCloseTaskPo.getTaskBusinessType());
                            autoCloseTaskMqNew.setBizId(autoCloseTaskPo.getBizId());
                            autoCloseTaskMqNew.setChangeStatus(autoCloseTaskPo.getChangeStatus());
                            autoCloseTaskMqNew.setOperateTime(unloadBizLastScanTimeVal);
                            // 新下发一个延迟执行任务
                            jyBizTaskAutoCloseHelperService.pushBizTaskAutoCloseTask4UnloadingNotFinish(autoCloseTaskMqNew, taskUnloadVehicleExist);
                            log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask lazy execute param {} bizTask {}", JSON.toJSONString(autoCloseTaskPo), JSON.toJSONString(taskUnloadVehicleExist));
                            return result.toSuccess();
                        }
                    }
                }
            }

            final AutoCloseTaskContextDto autoCloseTaskContextDto = new AutoCloseTaskContextDto();
            BeanCopyUtil.copy(autoCloseTaskPo, autoCloseTaskContextDto);
            autoCloseTaskContextDto.setOperateUserErp(this.sysOperateUser);
            autoCloseTaskContextDto.setOperateTime(System.currentTimeMillis());
            autoCloseTaskContextDto.setOperateUserName(this.sysOperateUserName);
            autoCloseTaskContextDto.setBizId(taskUnloadVehicleExist.getBizId());

            AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = uccPropertyConfiguration.getAutoCloseJyBizTaskConfig();
            if(autoCloseJyBizTaskConfig == null){
                return result;
            }
            if(Objects.equals(true, autoCloseJyBizTaskConfig.getFakeExecute())){
                log.info("JyBizTaskCloseUnloadTaskServiceImpl.closeTask fakeExecute {}", JSON.toJSONString(autoCloseTaskContextDto));
                return result;
            }

            // 关闭主任务 + 调度任务
            if(Objects.equals(JyBizTaskUnloadTaskTypeEnum.UNLOAD_TASK_CATEGORY_DMS.getCode(), taskUnloadVehicleExist.getTaskType())){
                final Result<Boolean> completeResult = this.completeTask(autoCloseTaskContextDto, taskUnloadVehicleExist);
                if(!completeResult.isSuccess()){
                    log.warn("JyBizTaskCloseUnloadTaskServiceImpl.closeTask completeTask fail {}", JSON.toJSONString(completeResult));
                    return result.toFail("关闭任务失败 " + completeResult.getMessage());
                }
            }

            if(Objects.equals(JyBizTaskUnloadTaskTypeEnum.UNLOAD_TASK_CATEGORY_TYS.getCode(), taskUnloadVehicleExist.getTaskType())){
                final Result<Boolean> completeResult = this.completeTysTask(autoCloseTaskContextDto, taskUnloadVehicleExist);
                if(!completeResult.isSuccess()){
                    log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTysTask completeTask fail {}", JSON.toJSONString(completeResult));
                    return result.toFail("关闭任务失败" + completeResult.getMessage());
                }
            }

            final String unloadBizLastScanTimeKey = jyBizTaskAutoCloseHelperService.getUnloadBizLastScanTimeKey(autoCloseTaskContextDto.getBizId());
            redisClientOfJy.del(unloadBizLastScanTimeKey);
        } catch (Exception e) {
            log.error("JyBizTaskCloseUnloadTaskServiceImpl.closeTask exception {}", JSON.toJSONString(autoCloseTaskPo), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 关闭主任务
     */
    private Result<Boolean> completeTask(AutoCloseTaskContextDto autoCloseTaskContextDto, JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist) {
        Result<Boolean> result = Result.success();

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(taskUnloadVehicleExist.getEndSiteId().intValue());
        currentOperate.setSiteName(taskUnloadVehicleExist.getEndSiteName());

        User user = new User();
        user.setUserErp(autoCloseTaskContextDto.getOperateUserErp());
        user.setUserName(autoCloseTaskContextDto.getOperateUserName());

        // 调用预览接口
        UnloadCommonRequest unloadCommonRequest = new UnloadCommonRequest();
        unloadCommonRequest.setCurrentOperate(currentOperate);
        unloadCommonRequest.setUser(user);
        unloadCommonRequest.setBizId(taskUnloadVehicleExist.getBizId());
        unloadCommonRequest.setPageNumber(1);
        unloadCommonRequest.setPageSize(1);
        unloadCommonRequest.setSealCarCode(taskUnloadVehicleExist.getSealCarCode());
        final InvokeResult<UnloadPreviewData> unloadPreviewDataResult = jyUnloadVehicleService.unloadPreviewDashboard(unloadCommonRequest);
        if (unloadPreviewDataResult == null) {
            return result.toFail("查询任务预览数据失败，调用结果为空");
        }
        if (!unloadPreviewDataResult.codeSuccess()) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTask unloadPreviewDataResult {}", JSON.toJSONString(unloadPreviewDataResult));
            return result.toFail("查询任务预览数据失败，返回结果失败");
        }
        final UnloadPreviewData unloadPreviewData = unloadPreviewDataResult.getData();
        if (unloadPreviewData == null) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTask unloadPreviewDataResult {}", JSON.toJSONString(unloadPreviewDataResult));
            return result.toFail("查询任务预览数据失败，返回结果为空");
        }

        final UnloadCompleteRequest unloadCompleteRequest = new UnloadCompleteRequest();
        unloadCompleteRequest.setCurrentOperate(currentOperate);
        unloadCompleteRequest.setUser(user);

        final JyScheduleTaskResp scheduleTask = getJyScheduleTask(autoCloseTaskContextDto.getBizId());
        if(scheduleTask == null){
            log.warn("查询调度任务数据为空. autoCloseTaskContextDto:{}", JsonHelper.toJson(autoCloseTaskContextDto));
            return result.toFail("查询调度任务数据为空");
        }
        unloadCompleteRequest.setTaskId(scheduleTask.getTaskId());
        unloadCompleteRequest.setBizId(autoCloseTaskContextDto.getBizId());
        unloadCompleteRequest.setSealCarCode(taskUnloadVehicleExist.getSealCarCode());
        unloadCompleteRequest.setAbnormalFlag(unloadPreviewData.getAbnormalFlag());
        unloadCompleteRequest.setToScanCount(unloadPreviewData.getToScanCount() != null ? unloadPreviewData.getToScanCount() : 0);
        unloadCompleteRequest.setMoreScanLocalCount(unloadPreviewData.getMoreScanLocalCount() != null ? unloadPreviewData.getMoreScanLocalCount() : 0);
        unloadCompleteRequest.setMoreScanOutCount(unloadPreviewData.getMoreScanOutCount() != null ? unloadPreviewData.getMoreScanOutCount() : 0);

        final InvokeResult<Boolean> completeResult = jyUnloadVehicleService.submitUnloadCompletion(unloadCompleteRequest);
        if (completeResult == null) {
            return result.toFail("关闭任务数据失败，调用结果为空");
        }
        if (!completeResult.codeSuccess()) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTask completeResult {}", JSON.toJSONString(completeResult));
            return result.toFail("关闭任务数据失败，返回结果失败");
        }
        return result;
    }

    /**
     * 关闭主任务
     */
    private Result<Boolean> completeTysTask(AutoCloseTaskContextDto autoCloseTaskContextDto, JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist) {
        Result<Boolean> result = Result.success();

        final JyScheduleTaskResp scheduleTask = getJyScheduleTask(autoCloseTaskContextDto.getBizId());
        if(scheduleTask == null){
            log.warn("查询调度任务数据为空. autoCloseTaskContextDto:{}", JsonHelper.toJson(autoCloseTaskContextDto));
            return result.toFail("查询调度任务数据为空");
        }

        com.jd.bluedragon.distribution.jy.dto.CurrentOperate currentOperate = new com.jd.bluedragon.distribution.jy.dto.CurrentOperate();
        currentOperate.setSiteCode(taskUnloadVehicleExist.getEndSiteId().intValue());
        currentOperate.setSiteName(taskUnloadVehicleExist.getEndSiteName());
        currentOperate.setGroupCode(scheduleTask.getDistributionTarget());
        final String positionCode = this.getJyGroupPositionCode(scheduleTask.getDistributionTarget());
        if(positionCode == null){
            return result.toFail("根据组号查询网格码失败");
        }
        currentOperate.setPositionCode(positionCode);

        com.jd.bluedragon.distribution.jy.dto.User user = new com.jd.bluedragon.distribution.jy.dto.User();
        user.setUserErp(autoCloseTaskContextDto.getOperateUserErp());
        user.setUserName(autoCloseTaskContextDto.getOperateUserName());

        final UnloadPreviewDto unloadPreviewDto = new UnloadPreviewDto();
        unloadPreviewDto.setCurrentOperate(currentOperate);
        unloadPreviewDto.setUser(user);
        unloadPreviewDto.setBizId(taskUnloadVehicleExist.getBizId());
        unloadPreviewDto.setPageNumber(1);
        unloadPreviewDto.setPageSize(1);
        unloadPreviewDto.setTaskId(Long.parseLong(scheduleTask.getTaskId()));
        unloadPreviewDto.setVehicleNumber(taskUnloadVehicleExist.getVehicleNumber());
        unloadPreviewDto.setSealCarCode(taskUnloadVehicleExist.getSealCarCode());
        unloadPreviewDto.setExceptionType(1);
        final InvokeResult<UnloadPreviewRespDto> unloadPreviewDataResult = jyUnloadVehicleTysService.previewBeforeUnloadComplete(unloadPreviewDto);
        if (unloadPreviewDataResult == null) {
            return result.toFail("查询任务预览数据失败，调用结果为空");
        }
        if (!unloadPreviewDataResult.codeSuccess()) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTysTask unloadPreviewDataResult {}", JSON.toJSONString(unloadPreviewDataResult));
            return result.toFail("查询任务预览数据失败，返回结果失败");
        }
        final UnloadPreviewRespDto unloadPreviewData = unloadPreviewDataResult.getData();
        if (unloadPreviewData == null) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTysTask unloadPreviewDataResult {}", JSON.toJSONString(unloadPreviewDataResult));
            return result.toFail("查询任务预览数据失败，返回结果为空");
        }

        UnloadCompleteDto unloadCompleteDto = new UnloadCompleteDto();
        unloadCompleteDto.setCurrentOperate(currentOperate);
        unloadCompleteDto.setUser(user);
        unloadCompleteDto.setAbnormalFlag(unloadPreviewData.getAbnormalFlag());
        unloadCompleteDto.setToScanCount(unloadPreviewData.getToScanCount() != null ? unloadPreviewData.getToScanCount().longValue() : 0);
        unloadCompleteDto.setMoreScanLocalCount(unloadPreviewData.getMoreScanLocalCount() != null ? unloadPreviewData.getMoreScanLocalCount().longValue() : 0);
        unloadCompleteDto.setMoreScanOutCount(unloadPreviewData.getMoreScanOutCount() != null ? unloadPreviewData.getMoreScanOutCount().longValue() : 0);
        unloadCompleteDto.setBizId(autoCloseTaskContextDto.getBizId());
        unloadCompleteDto.setTaskId(scheduleTask.getTaskId());
        unloadCompleteDto.setSealCarCode(taskUnloadVehicleExist.getSealCarCode());
        unloadCompleteDto.setOperateSource(JyBizOpereateSourceEnum.SYSTEM.getCode());
        final InvokeResult<Boolean> completeResult = jyUnloadVehicleTysService.completeUnloadTask(unloadCompleteDto);

        if (completeResult == null) {
            return result.toFail("关闭任务数据失败，调用结果为空");
        }
        if (!completeResult.codeSuccess()) {
            log.warn("JyBizTaskCloseUnloadTaskServiceImpl.completeTysTask completeResult {}", JSON.toJSONString(completeResult));
            return result.toFail("关闭任务数据失败，返回结果失败");
        }

        return result;
    }

    /**
     * 查询调度任务数据
     * @param bizId
     * @return
     */
    private JyScheduleTaskResp getJyScheduleTask(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return scheduleTask;
    }

    /**
     * 根据组号查询网格码
     */
    private String getJyGroupPositionCode(String groupCode) {
        JyGroupEntity groupInfo = jyGroupService.queryGroupByGroupCode(groupCode);
        if (groupInfo == null) {
            return null;
        }
        return groupInfo.getPositionCode();
    }

    private String getUnloadBizLastScanTimeKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_TASK_LAST_SCAN_TIME_KEY, bizId);
    }
}
