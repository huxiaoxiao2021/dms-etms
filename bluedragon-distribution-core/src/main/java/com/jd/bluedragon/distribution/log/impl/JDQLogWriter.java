package com.jd.bluedragon.distribution.log.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSON;
import com.jd.dms.logger.external.LogWriter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Properties;

public class JDQLogWriter implements LogWriter {

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    private static Logger logger = Logger.getLogger(LogWriter.class);

    KafkaProducer<String, String> producer = null;
    private static final Object lock = new Object();

    String topic;//topic

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public JDQLogWriter() {
    }

    public JDQLogWriter(String clientId, String brokerlist, String groupId) {
        Properties props = getProperties(brokerlist, clientId, groupId);
        try {
            this.producer = new KafkaProducer<String, String>(props);
        }catch (KafkaException e){
            logger.error("初始化Kafka失败，brokerlist: " + brokerlist, e);
        }
    }


    private static Properties getProperties(String brokerlist, String clientId, String groupId) {
        Properties props = new Properties();
        //注意: 线下测试环境和不认证集群访问必须显示指定PLAINTEXT
        props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerlist);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");//序列化方式是string
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");//序列化方式是string
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");//lz4/gzip/snappy
        props.put(ProducerConfig.LINGER_MS_CONFIG, "300");//sdk里面默认linger.ms是100,如果在单条同步发送条件下此值请用户设置成0

        return props;
    }


    @Override
    @JProfiler(jKey = "DMSWEB.JDQLogWriter.addLog", mState = {JProEnum.TP})
    public void addLog(BusinessLogProfiler businessLogProfiler) {
        //ucc开关，是否启用
        if (uccPropertyConfiguration.isLogToBusinessLogByKafka() == false) {
            return;
        }

        //和logbook相同的日志格式，日志放到msg中
        JSONObject logs = new JSONObject();
        logs.put("time", new Date().getTime());
        logs.put("msg", JSON.toJSONString(businessLogProfiler));

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, "businesslogkey", JSON.toJSONString(logs));

        try {
            producer.send(record);
        } catch (Exception e) {
            logger.error("Kafka日志发送失败", e);
        }
    }

    /**
     *  关闭
     */
    public void close(){
        producer.flush();
        producer.close();
        logger.info("关闭Kafka连接");
    }
}
