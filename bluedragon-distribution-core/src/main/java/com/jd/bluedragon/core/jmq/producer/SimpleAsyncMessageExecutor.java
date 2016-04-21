package com.jd.bluedragon.core.jmq.producer;

import com.jd.bluedragon.utils.SpringHelper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangtingwei on 2016/3/28.
 */
@Service("asyncMessageExecutor")
public class SimpleAsyncMessageExecutor implements AsyncMessageExecutor {

    private Map<String,DefaultJMQProducer> topicMap;

    @Override
    public void execute(String topic, String businessId, String body) throws Throwable {
        getQueueByTopic(topic).send(businessId,body);
    }

    /**
     * 获取发货MQ对象
     * @param topic 对象主题
     * @return
     */
    @Override
    public DefaultJMQProducer getQueueByTopic(String topic) {

        if(null==topicMap) {
            synchronized (SimpleAsyncMessageExecutor.class) {
                if(null==topicMap) {

                    Map<String,DefaultJMQProducer> queueMap=null;
                    queueMap=SpringHelper.getBeans(DefaultJMQProducer.class);
                    Collection<DefaultJMQProducer> list=queueMap.values();
                    topicMap=new HashMap<String, DefaultJMQProducer>(list.size());
                    for(DefaultJMQProducer item:list){
                        topicMap.put(item.getTopic(),item);
                    }
                }
            }
        }
        return topicMap.get(topic);
    }
}
