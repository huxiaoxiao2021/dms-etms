package com.jd.bluedragon.distribution.worker.sorting.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.sortexception.service.SortExceptionLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 处理异常日志 任务
 * Created by guoyongzhi on 2014/11/18.
 */
public class SortingExceptionRedisTask extends RedisSingleScheduler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SortExceptionLogService sortExceptionLogService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result = false;
        try {
            log.info("task id is {}" , task.getId());
            result = this.sortExceptionLogService.addExpectionLog(task);

        } catch (Exception e) {
            log.error("处理分拣任务发生异常,task id is {}" , task.getId(),e);
            return Boolean.FALSE;
        }
        return result;
    }
}
