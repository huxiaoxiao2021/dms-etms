package com.jd.bluedragon.distribution.jdq4;

import com.jd.bdp.jdq.JDQConfigUtil;
import com.jd.bdp.jdq.JDQ_CLIENT_INFO_ENV;
import com.jd.bdp.jdw.avro.JdwData;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public abstract class JDQConsumer implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(JDQConsumer.class);

    private JDQConfig jdqConfig;

    @Value("${JDQ_TEST_ENV:true}")
    private boolean testEnv;
    KafkaConsumer<String, JdwData> consumer;

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
    /**
     * 添加kafka的配置
     *
     * @param authProp
     * @return
     */
    private static Properties getProperties(Properties authProp) {
        /**
         * kafka配置列表，可参考（版本2.5.0）https://kafka.apache.org/25/documentation.html#producerconfigs
         */
        authProp.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());//byte序列化方式
        authProp.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDataDeserializer.class.getName());
        authProp.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");//不设置默认值是latest（第一次消费或者越界从最新开始消费）；earliest：第一次消费或者越界从最小位点开始消费数据
        authProp.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");//是否自动提交位点,默认是true.默认的自动提交位点的时间间隔是5000ms，false的情况下是需要用户自己调用commit方法自己手动提交位点信息的
        return authProp;
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
        Properties properties = new Properties();
        Map<String, String> clientConfigs = JDQConfigUtil.getClientConfigs(jdqConfig.getUsername(), jdqConfig.getDomain(), jdqConfig.getPassword());
        properties.putAll(clientConfigs);
//        props.put("bootstrap.servers", jdqConfig.getDomain());
//        props.put("group.id", jdqConfig.getGroupId());
//        props.put("enable.auto.commit", "true");
//        props.put("key.deserializer", StringDeserializer.class.getName());
//        props.put("value.deserializer", StringDeserializer.class.getName());
//
//        //下面三个参数是关于认证的核心参数
//        props.put("sasl.jaas.config", "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"" + jdqConfig.getUsername() + "\" password=\"" + jdqConfig.getPassword() + "\";");
//        props.put("sasl.mechanism", "SCRAM-SHA-256");
//        props.put("security.protocol", "SASL_PLAINTEXT");

        consumer = new KafkaConsumer<String, JdwData>(getProperties(properties),new StringDeserializer(), new AvroDataDeserializer());
        consumer.subscribe(Arrays.asList(jdqConfig.getTopic()));
        try{
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ConsumerRecords<String, JdwData> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, JdwData> record : records) {
                        if (record == null){
                            break;
                        }
                        /**
                         * your business code...
                         */
                        onMessage(record);
                        logger.info("value = {}, offset = {}", record.value(), record.offset());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(),e);
                }
            }
            consumer.commitSync();
        }finally {
            consumer.close();
        }
    }

    public abstract void onMessage(ConsumerRecord<String, JdwData> message);

    public abstract JDQConfig createJDQConfig();
}
