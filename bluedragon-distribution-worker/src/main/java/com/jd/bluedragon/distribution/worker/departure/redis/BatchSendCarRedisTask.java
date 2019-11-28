package com.jd.bluedragon.distribution.worker.departure.redis;

import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by yanghongqiang on 2015/1/15.
 */
public class BatchSendCarRedisTask extends RedisSingleScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchSendCarRedisTask.class);

    @Autowired
    private DepartureService  departureService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        boolean result=false;

        try {
            log.info("task id is {}" , task.getId());
            result = departureService.dealDepartureTmpToSend(task);
        } catch (Exception e) {
            log.error("自动分拣准备任务异常。task id is {}" , task.getId(),e);
            return Boolean.FALSE;
        }
        return result;
    }
}
