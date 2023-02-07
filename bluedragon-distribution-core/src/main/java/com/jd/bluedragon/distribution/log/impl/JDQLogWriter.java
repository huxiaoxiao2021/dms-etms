package com.jd.bluedragon.distribution.log.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jdq4.JDQProducer;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.fastjson.JSON;
import com.jd.dms.logger.external.LogWriter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Properties;

public class JDQLogWriter implements LogWriter {

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;
    private static Logger logger = Logger.getLogger(LogWriter.class);

    @Autowired
    @Qualifier("opLogJDQProducer")
    JDQProducer jdqProducer;


    @Override
    @JProfiler(jKey = "DMSWEB.JDQLogWriter.addLog", mState = {JProEnum.TP}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public void addLog(BusinessLogProfiler businessLogProfiler) {
        //ucc开关，是否启用
        if (uccPropertyConfiguration.isLogToBusinessLogByKafka() == false) {
            return;
        }

        //和logbook相同的日志格式，日志放到msg中
        JSONObject logs = new JSONObject();
        long time = new Date().getTime();
        logs.put("time", time);
        logs.put("msg", JSON.toJSONString(businessLogProfiler));
        jdqProducer.produce(time + "", JSON.toJSONString(logs), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    logger.error("send log to kafka failed, " + exception.getMessage() + ", log=" + JSON.toJSONString(businessLogProfiler));
                }
            }
        });
    }
}
