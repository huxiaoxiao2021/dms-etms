package com.jd.bluedragon.distribution.worker.receive.redis;

import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by dudong on 2014/9/1.
 */
public class ReceivePushArteryMQRedisTask extends RedisSingleScheduler{

    @Autowired
    private DepartureService departureService;

    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try{
            this.log.info("开始执行推送财务干线计费信息 Redis Task id = {}" , task.getId());
            return departureService.pushMQ2ArteryBillingSysByTask(task);
        }catch(Exception e){
            this.log.error("推送财务干线计费信息 Redis Task 失败 Task id ={}" , task.getId() , e);
            return false;
        }
    }
}
