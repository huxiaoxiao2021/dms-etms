package com.jd.bluedragon.distribution.consumer.user;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.station.entity.AttendDetailChangeTopicData;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;

/**
 * 类的描述
 *
 * @author wuyoude
 * @date 2023/6/26 2:24 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class AttendDetailChangeTopicConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(AttendDetailChangeTopicConsumerTest.class);

    @Autowired
    private AttendDetailChangeTopicConsumer consumer;

    @Test
    public void consumer() {
        try {
            Message message = new Message();
            
            AttendDetailChangeTopicData mqData = new AttendDetailChangeTopicData();
            mqData.setActualOffTime(new Date());
            mqData.setUserErp("bjxings");
            mqData.setOpType(AttendDetailChangeTopicData.OP_TYPE_UPDATE);
            mqData.setDcNo("910");
            message.setText(JsonHelper.toJson(mqData));
            consumer.consume(message);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("滞留容器内扫描件处理mq处理异常!", e);
            Assert.fail();
        }
    }
}
