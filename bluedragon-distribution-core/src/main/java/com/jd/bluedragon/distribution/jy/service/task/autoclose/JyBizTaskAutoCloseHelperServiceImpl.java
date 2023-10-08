package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 任务关闭接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
@Slf4j
@Service
public class JyBizTaskAutoCloseHelperServiceImpl implements JyBizTaskAutoCloseHelperService {

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private TaskService taskService;

    @Override
    public String getUnloadBizLastScanTimeKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_TASK_LAST_SCAN_TIME_KEY, bizId);
    }

    /**
     * @param autoCloseTaskMq
     * @param jyBizTaskUnloadVehicleExist
     * @return
     */
    @Override
    public boolean pushBizTaskAutoCloseTask4WaitUnloadNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(jyBizTaskUnloadVehicleExist.getBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.WAIT_UNLOAD_NOT_FINISH.getCode());
            autoCloseTaskPo.setOperateTime(autoCloseTaskMq.getOperateTime());
            autoCloseTaskPo.setChangeStatus(autoCloseTaskMq.getChangeStatus());

            Task tTask = new Task();
            tTask.setCreateSiteCode(jyBizTaskUnloadVehicleExist.getEndSiteId().intValue());
            tTask.setKeyword1(String.valueOf(jyBizTaskUnloadVehicleExist.getBizId()));
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), jyBizTaskUnloadVehicleExist.getEndSiteId())));
            final AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = dmsConfigManager.getUccPropertyConfig().getAutoCloseJyBizTaskConfigObj();
            if (autoCloseJyBizTaskConfig == null) {
                log.info("JyUnloadVehicleServiceImpl.pushBizTaskAutoCloseTask no config, will not push auto close task");
                return true;
            }
            final long executeTimeMillSeconds = autoCloseTaskMq.getOperateTime() + autoCloseJyBizTaskConfig.getWaitUnloadNotFinishLazyTime() * 60 * 1000L;
            tTask.setExecuteTime(new Date(executeTimeMillSeconds));
            tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
            tTask.setExecuteCount(0);

            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("pushBizTaskAutoCloseTask4WaitUnloadNotFinish 作业工作台自动关闭任务 bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("pushBizTaskAutoCloseTask exception ", e);
        }
        return true;
    }

    /**
     * @param autoCloseTaskMq
     * @param jyBizTaskUnloadVehicleExist
     * @return
     */
    @Override
    public boolean pushBizTaskAutoCloseTask4UnloadingNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(jyBizTaskUnloadVehicleExist.getBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode());
            autoCloseTaskPo.setOperateTime(autoCloseTaskMq.getOperateTime());
            autoCloseTaskPo.setChangeStatus(autoCloseTaskMq.getChangeStatus());

            Task tTask = new Task();
            tTask.setCreateSiteCode(jyBizTaskUnloadVehicleExist.getEndSiteId().intValue());
            tTask.setKeyword1(String.valueOf(jyBizTaskUnloadVehicleExist.getBizId()));
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), jyBizTaskUnloadVehicleExist.getEndSiteId())));
            final AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = dmsConfigManager.getUccPropertyConfig().getAutoCloseJyBizTaskConfigObj();
            if (autoCloseJyBizTaskConfig == null) {
                log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask no config, will not push auto close task");
                return true;
            }
            final long executeTimeMillSeconds = autoCloseTaskMq.getOperateTime() + autoCloseJyBizTaskConfig.getUnloadingNotFinishLazyTime() * 60 * 1000L;
            tTask.setExecuteTime(new Date(executeTimeMillSeconds));
            tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
            tTask.setExecuteCount(0);

            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4UnloadingNotFinish 作业工作台自动关闭任务 bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask exception ", e);
        }
        return true;
    }

    @Override
    public boolean pushBizTaskAutoCloseTask4StrandNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskStrandReportEntity taskEntity) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(taskEntity.getBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.STRAND_NOT_SUBMIT.getCode());
            autoCloseTaskPo.setOperateTime(autoCloseTaskMq.getOperateTime());
            autoCloseTaskPo.setChangeStatus(autoCloseTaskMq.getChangeStatus());

            Task tTask = new Task();
            tTask.setCreateSiteCode(taskEntity.getSiteCode());
            tTask.setKeyword1(taskEntity.getBizId());
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), taskEntity.getNextSiteCode())));
            tTask.setExecuteTime(taskEntity.getExpectCloseTime());
            tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
            tTask.setExecuteCount(0);

            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4StrandNotFinish 作业工作台自动关闭任务 bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4StrandNotFinish exception ", e);
        }
        return true;
    }

    @Override
    public boolean pushBizTaskAutoCloseTask4SendVehicleTask(AutoCloseTaskMq autoCloseTaskMq,JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetail) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(jyBizTaskSendVehicleDetail.getSendVehicleBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.CREATE_SEND_VEHICLE_TASK.getCode());
            autoCloseTaskPo.setOperateTime(autoCloseTaskMq.getOperateTime());
            autoCloseTaskPo.setChangeStatus(autoCloseTaskMq.getChangeStatus());

            Task tTask = new Task();
            tTask.setCreateSiteCode(jyBizTaskSendVehicleDetail.getEndSiteId().intValue());
            tTask.setKeyword1(String.valueOf(jyBizTaskSendVehicleDetail.getBizId()));
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), jyBizTaskSendVehicleDetail.getEndSiteId())));

            //计划发车前十五分钟执行
            Date planDepartTime = jyBizTaskSendVehicleDetail.getPlanDepartTime();
            tTask.setExecuteTime(DateHelper.add(planDepartTime, Calendar.MINUTE,-15));
            tTask.setStatus(Task.TASK_STATUS_UNHANDLED);
            tTask.setExecuteCount(0);

            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4SendVehicleTask  bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4SendVehicleTask exception ", e);
        }
        return true;
    }
}
