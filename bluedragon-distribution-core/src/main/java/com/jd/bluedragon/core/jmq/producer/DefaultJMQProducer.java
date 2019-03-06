package com.jd.bluedragon.core.jmq.producer;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangtingwei on 2015/12/22.
 * JMQ消息发送
 */
public class DefaultJMQProducer {

    private static final Log logger = LogFactory.getLog(DefaultJMQProducer.class);

    @Autowired
    @Qualifier("jmqProducer")
    private com.jd.jmq.client.producer.MessageProducer jmqProducer;

    @Autowired
    private TaskService taskService;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 超时时间-默认值1000ms
     */
    private int timeout = 1000;

    /**
     * 批量提交 一次提交数量
     */
    private int batchSize = 1000;

    /**
     * JMQ消息发送,抛出异常，需要自行处理
     *
     * @param businessId 业务ID号
     * @param body       消息体
     * @throws JMQException
     */
    public void send(String businessId, String body) throws JMQException {
        if (logger.isInfoEnabled()) {
            logger.info(MessageFormat.format("推送MQ数据为topic:{0}->body:{1}", this.topic, body));
        }
        Message message = new Message(this.topic, body, businessId);
        jmqProducer.send(message, this.timeout);
    }

    public void sendOnFailPersistent(String businessId, String body) {
        try {
            send(businessId, body);
        } catch (Throwable ex) {
            logger.error("MQ发送失败，将进行消息队列持久化", ex);
            persistent(businessId, body);
        }
    }

    private void persistent(String businessId, String body) {
        try {
            Task task = new Task();
            task.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setOperateTime(new Date());
            task.setBoxCode(businessId);
            task.setKeyword1(this.topic);
            task.setType(Task.TASK_TYPE_MESSAGE);
            task.setBody(body);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setCreateSiteCode(Integer.valueOf(0));
            taskService.add(task, true);
        } catch (Throwable throwable) {
            logger.error("消息队列持久化", throwable);
        }
    }

    /**
     * 批量JMQ消息发送
     *
     * @param messages
     * @throws JMQException
     */
    public void batchSend(List<Message> messages) throws JMQException {
        if (messages != null && !messages.isEmpty()) {
            int size = messages.size();
            if (size <= this.batchSize) {
                jmqProducer.send(messages, this.timeout);
            } else {
                int mod = size % this.batchSize;
                int times = size / this.batchSize;
                if (mod > 0) {
                    times++;
                }
                for (int i = 0; i < times; i++) {
                    int start = i * this.batchSize;
                    int end = start + this.batchSize;
                    if (end > size) {
                        end = size;
                    }
                    jmqProducer.send(messages.subList(start, end), this.timeout);
                }
            }
        }
    }

    /**
     * 批量JMQ消息发送，发送时出现异常直接落库
     *
     * @param messages
     */
    public void batchSendOnFailPersistent(List<Message> messages) {
        try {
            this.batchSend(messages);
        } catch (Throwable ex) {
            logger.error("批量MQ发送失败，将进行消息队列持久化", ex);
            this.batchPersistent(messages);
        }
    }

    private void batchPersistent(List<Message> messages) {
        try {
            List<Task> tasks = new ArrayList<Task>(messages.size());
            for (Message message : messages) {
                Task task = new Task();
                task.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setOperateTime(new Date());
                task.setBoxCode(message.getBusinessId());
                task.setKeyword1(this.topic);
                task.setType(Task.TASK_TYPE_MESSAGE);
                task.setBody(message.getText());
                task.setTableName(Task.getTableName(task.getType()));
                task.setSequenceName(Task.getSequenceName(task.getTableName()));
                task.setCreateSiteCode(Integer.valueOf(0));
            }
            taskService.addBatch(tasks, true);
        } catch (Throwable throwable) {
            logger.error("消息队列持久化", throwable);
        }
    }

    /**
     * 获取JMQ消息体
     *
     * @param businessId
     * @param body
     * @return
     */
    public Message getMessage(String businessId, String body) {
        return new Message(this.topic, body, businessId);
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

    /**
     * @return the jmqProducer
     */
    public com.jd.jmq.client.producer.MessageProducer getJmqProducer() {
        return jmqProducer;
    }

    /**
     * @param jmqProducer the jmqProducer to set
     */
    public void setJmqProducer(
            com.jd.jmq.client.producer.MessageProducer jmqProducer) {
        this.jmqProducer = jmqProducer;
    }
}
