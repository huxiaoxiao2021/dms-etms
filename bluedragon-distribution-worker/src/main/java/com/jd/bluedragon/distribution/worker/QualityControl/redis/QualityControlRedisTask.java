package com.jd.bluedragon.distribution.worker.QualityControl.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/12/1.
 */
public class QualityControlRedisTask extends RedisSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private QualityControlService qualityControlService;

    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result = false;
        try {
            this.log.info("task id is {}" , task.getId());
            result = TaskResult.toBoolean(qualityControlService.dealQualityControlTask(task));
        } catch (Exception e) {
            this.log.error("处理异常质控Redis任务发生异常，task id:{}" , task.getId(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
