package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.core.jmq.producer.AsyncMessageExecutor;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangtingwei on 2016/3/28.
 */
public class MessageRedisTask extends RedisSingleScheduler {
    private static final Logger log = LoggerFactory.getLogger(MessageRedisTask.class);
    @Autowired
    private AsyncMessageExecutor asyncMessageExecutor;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            asyncMessageExecutor.execute(task.getKeyword1(),task.getBoxCode() , task.getBody());
        }catch (Throwable throwable){
            log.error("消息队列处理topic:{};body:{}", task.getKeyword1(), task.getBody(),throwable);
            return false;
        }
        return true;
    }
}
