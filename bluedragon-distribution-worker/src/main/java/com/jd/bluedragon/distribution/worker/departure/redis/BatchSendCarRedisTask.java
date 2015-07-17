package com.jd.bluedragon.distribution.worker.departure.redis;

import com.jd.bluedragon.distribution.auto.service.SortingPrepareService;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yanghongqiang on 2015/1/15.
 */
public class BatchSendCarRedisTask extends RedisSingleScheduler {

    private static final Log logger= LogFactory.getLog(BatchSendCarRedisTask.class);

    @Autowired
    private DepartureService  departureService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            logger.info("task id is " + task.getId());
            result = departureService.dealDepartureTmpToSend(task);
        } catch (Exception e) {
            logger.error("task id is" + task.getId());
            logger.error("自动分拣准备任务，异常信息为：" + e.getMessage(), e);
            return Boolean.FALSE;
        }
        return result;
    }
}
