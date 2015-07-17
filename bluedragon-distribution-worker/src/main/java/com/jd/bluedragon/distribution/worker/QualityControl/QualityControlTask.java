package com.jd.bluedragon.distribution.worker.QualityControl;

import com.jd.bluedragon.distribution.framework.AbstractDBSingleScheduler;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/12/1.
 */
public class QualityControlTask extends AbstractDBSingleScheduler {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private QualityControlService qualityControlService;

    @Override
    protected TaskResult executeExtendSingleTask(Task task, String ownSign) throws Exception {
        TaskResult taskResult = TaskResult.FAILED;
        try {
            this.logger.info("task id is " + task.getId());
            taskResult = qualityControlService.dealQualityControlTask(task);
        } catch (Exception e) {
            this.logger.error("task id is" + task.getId());
            this.logger.error("处理异常质控任务发生异常，异常信息为：" + e.getMessage(), e);
            return TaskResult.FAILED;
        }
        return taskResult;
    }
}
