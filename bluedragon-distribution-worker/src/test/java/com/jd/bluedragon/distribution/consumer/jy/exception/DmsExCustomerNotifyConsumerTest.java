package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.jd.bluedragon.distribution.jy.enums.CustomerNotifyStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyExCustomerNotifyMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExpCustomerReturnMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExpWaybillDeliveryDto;
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
            jyExCustomerNotifyMQ.setBusinessId("408");
            jyExCustomerNotifyMQ.setEventNo("110");
            jyExCustomerNotifyMQ.setExptId("SCRAPPED_JDVA19408919512");
            jyExCustomerNotifyMQ.setResultType("174");
            jyExCustomerNotifyMQ.setDesc("客服回传");
            jyExCustomerNotifyMQ.setErpCode("chen");
            jyExCustomerNotifyMQ.setSendTime("2023-01-01");
            message.setText(JsonHelper.toJson(jyExCustomerNotifyMQ));
            dmsExCustomerNotifyConsumer.consume(message);
            Assert.assertTrue(true);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Autowired
    private DmsExpCustomerReturnConsumer dmsExpCustomerReturnConsumer;

    @Test
    public void dmsExpCustomerReturnConsumerTest() throws Exception{

        //  {"businessId":"413","desc":"未联系上","erpCode":"jiaowenqiang","eventNo":"800025559","exptId":"413_JD0003421266039_910","id":"413_JD0003421266039_9101691562602779",
        //  "msgId":"413_JD0003421266039_9101691562602779","resultType":"354","ts":1691562602779}

        Message message = new Message();
        JyExpCustomerReturnMQ jyExCustomerNotifyMQ = new JyExpCustomerReturnMQ();
        jyExCustomerNotifyMQ.setId("413_JD0003421266039_9101691562602779");
        jyExCustomerNotifyMQ.setBusinessId("413");
        jyExCustomerNotifyMQ.setDesc("未联系上");
        jyExCustomerNotifyMQ.setErpCode("jiaowenqiang");
        jyExCustomerNotifyMQ.setEventNo("800025559");
        jyExCustomerNotifyMQ.setExptId("413_JD0003421266039_910");
        jyExCustomerNotifyMQ.setResultType("354");
        jyExCustomerNotifyMQ.setSendTime("2023-01-01");
        message.setText(JsonHelper.toJson(jyExCustomerNotifyMQ));

        dmsExpCustomerReturnConsumer.consume(message);
    }

    @Autowired
    private DMSExpWaybillDeliveryConsumer dmsExpWaybillDeliveryConsumer;


    @Test
    public void dmsExpWaybillDeliveryConsumerTest() throws Exception {

        Message message = new Message();
        JyExpWaybillDeliveryDto dto = new JyExpWaybillDeliveryDto();
        dto.setWaybillCode("JD0003421266039");
        dto.setOpeSiteId("-1");
        dto.setOpeSiteName("福州连江县祥兴路营业部lgx");
        dto.setOpeUserId("-1");
        dto.setOpeUserErp("chen");
        message.setText(JsonHelper.toJson(dto));
        dmsExpWaybillDeliveryConsumer.consume(message);

    }

}