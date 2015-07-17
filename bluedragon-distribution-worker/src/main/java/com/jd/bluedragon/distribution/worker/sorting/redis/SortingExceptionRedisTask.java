package com.jd.bluedragon.distribution.worker.sorting.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.sortexception.service.SortExceptionLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 处理异常日志 任务
 * Created by guoyongzhi on 2014/11/18.
 */
public class SortingExceptionRedisTask extends RedisSingleScheduler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SortExceptionLogService sortExceptionLogService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result = false;
        try {
            logger.info("task id is " + task.getId());
            result = this.sortExceptionLogService.addExpectionLog(task);

        } catch (Exception e) {
            logger.info("task id is " + task.getId());
            this.logger.error("处理分拣任务发生异常，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
