package com.jd.bluedragon.distribution.worker.QualityControl;

import com.jd.bluedragon.distribution.framework.AbstractDBSingleScheduler;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/12/1.
 */
public class QualityControlTask extends AbstractDBSingleScheduler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QualityControlService qualityControlService;

    @Override
    protected TaskResult executeExtendSingleTask(Task task, String ownSign) throws Exception {
        TaskResult taskResult = TaskResult.FAILED;
        try {
            this.log.info("task id is {}" , task.getId());
            taskResult = qualityControlService.dealQualityControlTask(task);
        } catch (Exception e) {
            this.log.error("处理异常质控Redis任务发生异常，task id:{}" , task.getId(), e);
            return TaskResult.FAILED;
        }
        return taskResult;
    }
}
