package com.jd.bluedragon.distribution.worker.sorting.redis;

import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.auto.service.SortingPrepareService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自动分拣机分拣数据准备任务
 * Created by wangtingwei on 2014/10/16.
 */
public class SortingPrepareRedisTask extends RedisSingleScheduler {

    private static final Log logger= LogFactory.getLog(SortingPrepareRedisTask.class);

    @Autowired
    private SortingPrepareService sortingPrepareService;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("task id is " + task.getId());
            result = this.sortingPrepareService.handler(task);
        } catch (Exception e) {
            logger.error("task id is" + task.getId());
            logger.error("自动分拣准备任务，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
