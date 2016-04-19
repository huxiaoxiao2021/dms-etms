package com.jd.bluedragon.core.jmq.producer;

import java.util.Map;

/**
 * Created by wangtingwei on 2016/3/28.
 */
public interface AsyncMessageExecutor {

    void execute(String topic,String businessId,String body) throws Throwable;

    DefaultJMQProducer getQueueByTopic(String topic);
}
