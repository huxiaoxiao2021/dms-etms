package com.jd.bluedragon.distribution.jdq4.impl;


import com.alibaba.fastjson.JSONObject;
import com.jd.bdp.jdq.JDQConfigUtil;
import com.jd.bdp.jdq.JDQ_CLIENT_INFO_ENV;
import com.jd.bluedragon.distribution.jdq4.JDQProducer;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.Properties;


/**
 * JDQ4 kafka 2.5.x
 */
public class JDQ4ProducerImpl implements JDQProducer, InitializingBean, DisposableBean {

    private static final Log logger = LogFactory.getLog(JDQ4ProducerImpl.class);

    @Setter
    private String username;
    @Setter
    private String domain;
    @Setter
    private String password;

    @Setter
    private Properties userDefinedProperties;

    @Setter
    String topic;

    @Value("${JDQ_TEST_ENV:true}")
    boolean testEnv;

    @Value("${JDQ_DISABLE:false}")
    boolean jdqDisable;

    KafkaProducer<String, String> producer;

    @Override
    public void destroy() throws Exception {

        try {
            producer.flush();
            producer.close();
        } catch (Exception e) {
            logger.error("JDQ关闭失败,username" + username, e);
        }
    }

    //http://bdp.jd.com/helpCenter/front/showDocumentList.html?docId=467

    @Override
    public void afterPropertiesSet() throws Exception {
        if (jdqDisable){
            return;
        }

        checkParam();
        logger.info("JDQ 4 Producer init,username:" + username + ",password:" + password + ",domain:" + domain + ",topic:" + topic);

        if (testEnv) {
            JDQ_CLIENT_INFO_ENV.authClientInfoENV("t.bdp.jd.com",80);
            JDQ_CLIENT_INFO_ENV.setClusterAddress("t.bdp.jd.com");
        }

        Properties properties = new Properties();
        Map<String, String> clientConfigs = JDQConfigUtil.getClientConfigs(username, domain, password);

        properties.putAll(clientConfigs);

        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, "524288");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 500);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        //其他自定义属性
        if (userDefinedProperties != null) {
            properties.putAll(userDefinedProperties);
        }

        producer = new KafkaProducer<>(properties, new StringSerializer(), new StringSerializer());

    }



    private void checkParam() {
        if (StringUtils.isEmpty(domain) || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("JDQ参数错误");
        }
    }

    @Override
    public void produce(String key, Object value) {
        if (jdqDisable){
            return;
        }

        producer.send(new ProducerRecord<>(topic, key, JSONObject.toJSONString(value)), (metadata, exception) -> {
            if (exception != null) {
                logger.error("JDQ 发送失败,topic:" + topic, exception);
            }
        });
    }

    @Override
    public void produce(String key, Object value, Callback callback) {
        if (jdqDisable){
            return;
        }

        if (callback == null) {
            produce(key, value);
            return;
        }
        producer.send(new ProducerRecord<>(topic, key, JSONObject.toJSONString(value)), callback);
    }

}
