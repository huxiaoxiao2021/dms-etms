package com.jd.bluedragon.distribution.jy.service.task.autoclose;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseJyBizTaskConfig;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskMq;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.enums.JyAutoCloseTaskBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy.JYBizTaskCloseServiceStrategy;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 任务自动关闭服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-01-31 15:45:27 周二
 */
@Service("jyBizTaskAutoCloseService")
@Slf4j
public class JyBizTaskAutoCloseServiceImpl implements JyBizTaskAutoCloseService {

    @Autowired
    private JYBizTaskCloseServiceStrategy jyBizTaskCloseServiceStrategy;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    private TaskService taskService;

    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    /**
     * 消费自动关闭消息
     *
     * @param autoCloseTaskMq mq报文
     * @return 消费结果
     * @author fanggang7
     * @time 2023-03-28 15:50:49 周二
     */
    @Override
    public Result<Boolean> consumeJyBizTaskAutoCloseMq(AutoCloseTaskMq autoCloseTaskMq) {
        log.info("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq param {}", JSON.toJSONString(autoCloseTaskMq));
        Result<Boolean> result = Result.success();
        try {
            final JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist = jyBizTaskUnloadVehicleService.findByBizId(autoCloseTaskMq.getBizId());
            if (jyBizTaskUnloadVehicleExist == null) {
                log.warn("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq task not exist param {}", JSON.toJSONString(autoCloseTaskMq));
                return result;
            }
            if (Objects.equals(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), autoCloseTaskMq.getChangeStatus())) {
                this.pushBizTaskAutoCloseTask4WaitUnloadNotFinish(autoCloseTaskMq, jyBizTaskUnloadVehicleExist);
            }
            if (Objects.equals(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode(), autoCloseTaskMq.getChangeStatus())) {
                this.pushBizTaskAutoCloseTask4UnloadingNotFinish(autoCloseTaskMq, jyBizTaskUnloadVehicleExist);
            }
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.consumeJyBizTaskAutoCloseMq exception {}", JSON.toJSONString(autoCloseTaskMq), e);
            result.toFail("系统异常");
        }
        return result;
    }

    /**
     * 待解变待卸，推自动关闭卸车任务
     */
    private boolean pushBizTaskAutoCloseTask4WaitUnloadNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(jyBizTaskUnloadVehicleExist.getBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.WAIT_UNLOAD_NOT_FINISH.getCode());

            Task tTask = new Task();
            tTask.setCreateSiteCode(jyBizTaskUnloadVehicleExist.getEndSiteId().intValue());
            tTask.setKeyword1(String.valueOf(jyBizTaskUnloadVehicleExist.getBizId()));
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), jyBizTaskUnloadVehicleExist.getEndSiteId())));
            final AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = uccPropertyConfiguration.getAutoCloseJyBizTaskConfig();
            if (autoCloseJyBizTaskConfig == null) {
                log.info("JyUnloadVehicleServiceImpl.pushBizTaskAutoCloseTask no config, will not push auto close task");
                return true;
            }
            // 以解封车时间为准
            // final Date operateTime = new Date();
            final long executeTimeMillSeconds = autoCloseTaskMq.getOperateTime() + autoCloseJyBizTaskConfig.getWaitUnloadNotFinishLazyTime() * 60 * 1000L;
            tTask.setExecuteTime(new Date(executeTimeMillSeconds));

            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("pushBizTaskAutoCloseTask4WaitUnloadNotFinish 作业工作台自动关闭任务 bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("pushBizTaskAutoCloseTask exception ", e);
        }
        return true;
    }

    /**
     * 待卸变卸车中，推自动关闭卸车任务
     */
    private boolean pushBizTaskAutoCloseTask4UnloadingNotFinish(AutoCloseTaskMq autoCloseTaskMq, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleExist) {
        try {
            AutoCloseTaskPo autoCloseTaskPo = new AutoCloseTaskPo();
            autoCloseTaskPo.setBizId(jyBizTaskUnloadVehicleExist.getBizId());
            autoCloseTaskPo.setTaskBusinessType(JyAutoCloseTaskBusinessTypeEnum.UNLOADING_NOT_FINISH.getCode());

            Task tTask = new Task();
            tTask.setCreateSiteCode(jyBizTaskUnloadVehicleExist.getEndSiteId().intValue());
            tTask.setKeyword1(String.valueOf(jyBizTaskUnloadVehicleExist.getBizId()));
            tTask.setKeyword2(autoCloseTaskPo.getTaskBusinessType().toString());

            tTask.setType(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE);
            tTask.setTableName(Task.getTableName(Task.TASK_TYPE_JY_WORK_TASK_AUTO_CLOSE));
            String ownSign = BusinessHelper.getOwnSign();
            tTask.setOwnSign(ownSign);
            tTask.setFingerprint(Md5Helper.encode(String.format("%s_%s_%s", tTask.getKeyword1(), tTask.getKeyword2(), jyBizTaskUnloadVehicleExist.getEndSiteId())));
            final AutoCloseJyBizTaskConfig autoCloseJyBizTaskConfig = uccPropertyConfiguration.getAutoCloseJyBizTaskConfig();
            if (autoCloseJyBizTaskConfig == null) {
                log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask no config, will not push auto close task");
                return true;
            }
            // 计算卸车时间推后的执行时间
            // final Date operateTime = new Date(autoCloseTaskMq.getOperateTime());
            final long executeTimeMillSeconds = autoCloseTaskMq.getOperateTime() + autoCloseJyBizTaskConfig.getUnloadingNotFinishLazyTime() * 60 * 1000L;
            tTask.setExecuteTime(new Date(executeTimeMillSeconds));
            tTask.setBody(JsonHelper.toJson(autoCloseTaskPo));
            log.info("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask4UnloadingNotFinish 作业工作台自动关闭任务 bizId={}", autoCloseTaskPo.getBizId());
            taskService.doAddTask(tTask, false);
        } catch (Exception e) {
            log.error("JyBizTaskAutoCloseServiceImpl.pushBizTaskAutoCloseTask exception ", e);
        }
        return true;
    }

    /**
     * 自动关闭任务
     *
     * @param task 任务数据
     * @return 处理结果
     */
    @Override
    public boolean handleTimingCloseTask(Task task) {
        AutoCloseTaskPo autoCloseTaskPo = JsonHelper.fromJson(task.getBody(), AutoCloseTaskPo.class);
        if(autoCloseTaskPo == null){
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskBody is null");
            return false;
        }
        if (StringUtils.isBlank(autoCloseTaskPo.getBizId())) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail bizId is null");
            return false;
        }
        if (autoCloseTaskPo.getTaskBusinessType() == null) {
            log.error("JyBizTaskAutoCloseServiceImpl.handleTimingCloseTask--fail taskType is null");
            return false;
        }
        final Result<Void> result = jyBizTaskCloseServiceStrategy.closeTask(autoCloseTaskPo);
        return result.isSuccess();
    }
}
