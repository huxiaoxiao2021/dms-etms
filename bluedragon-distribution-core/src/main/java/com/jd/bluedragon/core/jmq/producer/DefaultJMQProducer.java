package com.jd.bluedragon.core.jmq.producer;

import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by wangtingwei on 2015/12/22.
 * JMQ消息发送
 */
public class DefaultJMQProducer {
    private static final Log logger= LogFactory.getLog(DefaultJMQProducer.class);

    @Autowired
    @Qualifier("jmqProducer")
    private com.jd.jmq.client.producer.MessageProducer jmqProducer;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * JMQ消息发送
     * @param businessId    业务ID号
     * @param body          消息体
     * @throws JMQException
     */
    public void send(String businessId,String body) throws JMQException{
        Message message = new Message(this.topic, body, businessId);
        jmqProducer.send(message,this.timeout);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
