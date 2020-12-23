package com.jd.bluedragon.distribution.consumer.storage;

import com.jd.bluedragon.dms.receive.jos.OrderInfoJosService;
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

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/12/14 21:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class KyStorageSendConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(KyStorageSendConsumer.class);

    @Autowired
    private KyStorageSendConsumer kyStorageSendConsumer;
    @Autowired
    private OrderInfoJosService orderInfoJosService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void consume() {
        try {
            Message message = new Message();
            String text = "{\n" +
                    "\t\"bizSource\": 2,\n" +
                    "\t\"boxCode\": \"JDVA00120166352-1-1-\",\n" +
                    "\t\"createSiteCode\": 23822,\n" +
                    "\t\"createTime\": 1607434920957,\n" +
                    "\t\"createUser\": \"刘爱慧\",\n" +
                    "\t\"createUserCode\": 16698,\n" +
                    "\t\"operateTime\": 1607434925154,\n" +
                    "\t\"packageBarcode\": \"JDVA00120166352-1-1-\",\n" +
                    "\t\"receiveSiteCode\": 39,\n" +
                    "\t\"sendCode\": \"23822-39-20201214210953032\",\n" +
                    "\t\"source\": \"DMS\"\n" +
                    "}";
            message.setText(text);
            kyStorageSendConsumer.consume(message);

        }catch (Exception e){
            logger.error("服务异常!",e);
            Assert.assertTrue(false);
        }
    }
}