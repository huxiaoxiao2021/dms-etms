package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.distribution.jy.enums.CustomerNotifyStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/3/16 2:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsExCustomerNotifyConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsExCustomerNotifyConsumerTest.class);
    
    @Autowired
    private DmsExCustomerNotifyConsumer dmsExCustomerNotifyConsumer;
    
    @Test
    public void consume() {
        
        try {
            Message message = new Message();
            JyExCustomerNotifyMQ jyExCustomerNotifyMQ = new JyExCustomerNotifyMQ();
            jyExCustomerNotifyMQ.setBusinessId("SANWU_SW1111111135");
            jyExCustomerNotifyMQ.setNotifyStatus(CustomerNotifyStatusEnum.CANCEL_ORDER.getCode());
            message.setText(JsonHelper.toJson(jyExCustomerNotifyMQ));
            dmsExCustomerNotifyConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}