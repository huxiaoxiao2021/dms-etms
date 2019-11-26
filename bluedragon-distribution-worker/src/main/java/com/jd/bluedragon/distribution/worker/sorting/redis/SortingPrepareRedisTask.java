package com.jd.bluedragon.distribution.worker.sorting.redis;

import com.jd.bluedragon.distribution.auto.service.SortingPrepareService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自动分拣机分拣数据准备任务
 * Created by wangtingwei on 2014/10/16.
 */
public class SortingPrepareRedisTask extends RedisSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(SortingPrepareRedisTask.class);

    @Autowired
    private SortingPrepareService sortingPrepareService;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            log.info("task id is {}" , task.getId());
            result = this.sortingPrepareService.handler(task);
        } catch (Exception e) {
            log.error("自动分拣准备任务异常，task id is {}" , task.getId(),e);
            return Boolean.FALSE;
        }
        return result;
    }
}
