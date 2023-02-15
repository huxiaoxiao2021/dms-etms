package com.jd.bluedragon.distribution.jdq4;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.jd.bdp.jdq.JDQ_CLIENT_INFO_ENV;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
@Data
public abstract class JDQConsumer implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JDQConsumer.class);

    private JDQConfig jdqConfig;

    @Value("${JDQ_TEST_ENV:true}")
    private boolean testEnv;
    KafkaConsumer<String, String> consumer;

    @Override
    public void destroy() throws Exception {

        try {
            consumer.close();
        } catch (Exception e) {
            logger.error("JDQ关闭失败", e);
        }
    }

    //http://bdp.jd.com/helpCenter/front/showDocumentList.html?docId=467
    private void checkParam(JDQConfig jdqConfig) {
        if (StringUtils.isEmpty(jdqConfig.getDomain()) ||
                StringUtils.isEmpty(jdqConfig.getUsername()) ||
                StringUtils.isEmpty(jdqConfig.getPassword()) ||
                StringUtils.isEmpty(jdqConfig.getGroupId()) ||
                StringUtils.isEmpty(jdqConfig.getTopic())) {
            throw new IllegalArgumentException("JDQ参数错误");
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {

        JDQConfig jdqConfig = createJDQConfig();
        checkParam(jdqConfig);
        logger.info("JDQ 4 Consumer init,username:" + jdqConfig.getUsername() + ",password:" + jdqConfig.getPassword() + ",domain:" + jdqConfig.getDomain() );

        if (testEnv) {
            JDQ_CLIENT_INFO_ENV.authClientInfoENV("t.bdp.jd.com",80);
            JDQ_CLIENT_INFO_ENV.setClusterAddress("t.bdp.jd.com");
        }

        Properties props = new Properties();
        props.put("bootstrap.servers", jdqConfig.getDomain());
        props.put("group.id", jdqConfig.getGroupId());
        props.put("enable.auto.commit", "true");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        //下面三个参数是关于认证的核心参数
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + jdqConfig.getUsername() + "\" password=\"" + jdqConfig.getPassword() + "\";");
        props.put("sasl.mechanism", "SCRAM-SHA-256");
        props.put("security.protocol", "SASL_PLAINTEXT");

        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(jdqConfig.getTopic()));
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                for (ConsumerRecord<String, String> record : records) {

                    /**
                     * your business code...
                     */
                    onMessage(record);
                    logger.info("value = {}, offset = {}", record.value(), record.offset());
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
        consumer.close();
    }

    public abstract void onMessage(ConsumerRecord<String, String> message);

    public abstract JDQConfig createJDQConfig();
}
