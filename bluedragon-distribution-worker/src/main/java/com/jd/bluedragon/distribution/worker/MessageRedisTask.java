package com.jd.bluedragon.distribution.worker;

import com.jd.bluedragon.core.jmq.producer.AsyncMessageExecutor;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;

/**
 * Created by wangtingwei on 2016/3/28.
 */
public class MessageRedisTask extends RedisSingleScheduler {
    private static final Log logger= LogFactory.getLog(MessageRedisTask.class);
    @Autowired
    private AsyncMessageExecutor asyncMessageExecutor;
    @Override
    protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
        try {
            asyncMessageExecutor.execute(task.getKeyword1(),task.getBoxCode() , task.getBody());
        }catch (Throwable throwable){
            logger.error(MessageFormat.format("消息队列处理topic:{0};body:{1}", task.getKeyword1(), task.getBody()),throwable);
            return false;
        }
        return true;
    }
}
