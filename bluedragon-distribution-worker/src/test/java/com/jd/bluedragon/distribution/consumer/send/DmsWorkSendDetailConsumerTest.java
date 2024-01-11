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
            String body = "{\"bizSource\":24,\"boxCode\":\"JDAZ00001591060-1-2-\",\"createSiteCode\":40240,\"createSiteType\":64,\"createSortSubType\":123511,\"createSortThirdType\":1235111,\"createSortType\":12351,\"createSubType\":64,\"createTime\":1699843972180,\"createUser\":\"王飞\",\"createUserCode\":18157,\"operateTime\":1699843972000,\"packageBarcode\":\"JDAZ00001591060-1-2-\",\"receiveSiteCode\":25162,\"receiveSiteType\":16,\"receiveSubType\":16,\"receiveThirdType\":16001,\"sendCode\":\"40240-25162-20231113101333654\",\"source\":\"DMS\"}";
            Message message = new Message();
            message.setText(body);
            dmsWorkSendDetailConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}