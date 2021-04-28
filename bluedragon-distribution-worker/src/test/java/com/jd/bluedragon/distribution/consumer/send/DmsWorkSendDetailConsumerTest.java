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
 * @date 2021/4/26 6:00 下午
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsWorkSendDetailConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsWorkSendDetailConsumerTest.class);

    @Autowired
    private DmsWorkSendDetailConsumer dmsWorkSendDetailConsumer;
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void consume() {
        try {
            String body = "{\n" +
                    "    \"bizSource\":16,\n" +
                    "    \"boxCode\":\"BC010F002010Y10000210001\",\n" +
                    "    \"createSiteCode\":910,\n" +
                    "    \"createTime\":1619345055133,\n" +
                    "    \"createUser\":\"邢松\",\n" +
                    "    \"createUserCode\":10053,\n" +
                    "    \"operateTime\":1619345055133,\n" +
                    "    \"packageBarcode\":\"JDV000516800799-1-1-\",\n" +
                    "    \"receiveSiteCode\":39,\n" +
                    "    \"sendCode\":\"910-39-20210426182120496\",\n" +
                    "    \"source\":\"DMS\"\n" +
                    "}";
            Message message = new Message();
            message.setText(body);
            dmsWorkSendDetailConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}