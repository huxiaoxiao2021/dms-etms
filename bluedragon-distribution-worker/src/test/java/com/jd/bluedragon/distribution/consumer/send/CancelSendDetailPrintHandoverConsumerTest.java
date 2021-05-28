package com.jd.bluedragon.distribution.consumer.send;

import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Before;
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
 * @date 2021/4/26 6:16 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class CancelSendDetailPrintHandoverConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(CancelSendDetailPrintHandoverConsumerTest.class);

    @Autowired
    private CancelSendDetailPrintHandoverConsumer cancelSendDetailPrintHandoverConsumer;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void consume() {
        try {
            String body = "{\n" +
                    "    \"packageBarcode\":\"JDV000516800799-1-1-\",\n" +
                    "    \"waybillCode\":\"JDV000516800799\",\n" +
                    "    \"sendCode\":\"910-39-20210426182120496\",\n" +
                    "    \"operateTime\":1619345331199,\n" +
                    "    \"operatorErp\":\"liguiling1\"\n" +
                    "}";
            Message message = new Message();
            message.setText(body);
            cancelSendDetailPrintHandoverConsumer.consume(message);
        }catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}