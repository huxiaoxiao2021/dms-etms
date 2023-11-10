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
            String body = "{\"bizSource\":24,\"boxCode\":\"JDAZ00001587575-1-1-\",\"createSiteCode\":910,\"createSiteType\":64,\"createSortSubType\":123511,\"createSortThirdType\":1235111,\"createSortType\":12351,\"createSubType\":64,\"createTime\":1699606206778,\"createUser\":\"胡志浩\",\"createUserCode\":18234,\"operateTime\":1699606207000,\"packageBarcode\":\"JDAZ00001587575-1-1-\",\"receiveSiteCode\":25162,\"receiveSiteType\":16,\"receiveSubType\":16,\"receiveThirdType\":16001,\"sendCode\":\"910-25162-20231110161304240\",\"source\":\"DMS\"}";
            Message message = new Message();
            message.setText(body);
            dmsWorkSendDetailConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}