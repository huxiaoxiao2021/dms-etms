package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectBatchUpdateStatusConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectDataInitSplitConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectDataSplitBatchInitConsumer;
import com.jd.bluedragon.distribution.consumer.jy.collect.JyCollectStatusBatchUpdateWaybillSplitConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/13 14:06
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyTysUnloadCollectTest {

    private static final Logger logger = LoggerFactory.getLogger(JyTysUnloadCollectTest.class);

    @Autowired
    private JyCollectBatchUpdateStatusConsumer jyCollectBatchUpdateStatusConsumer;
    @Autowired
    private JyCollectDataInitSplitConsumer jyCollectDataInitSplitConsumer;
    @Autowired
    private JyCollectDataSplitBatchInitConsumer jyCollectDataSplitBatchInitConsumer;
    @Autowired
    private JyCollectStatusBatchUpdateWaybillSplitConsumer jyCollectStatusBatchUpdateWaybillSplitConsumer;

    @Test
    public void consume1() {
        try {
            String body1 = "{\n" +
                    "  \"bizId\": \"TEST002\",\n" +
                    "  \"createTime\": 1665283255773,\n" +
                    "  \"id\": 1000,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"receiveSiteId\": 100,\n" +
                    "  \"productType\": \"NONE\",\n" +
                    "  \"shouldScanCount\": 100010101,\n" +
                    "  \"sendVehicleBizId\": \"TEST002\",\n" +
                    "  \"version\": 10,\n" +
                    "  \"yn\": 1\n" +
                    "}\n";

            Message message1 = new Message();
            message1.setText(body1);
            jyCollectBatchUpdateStatusConsumer.consume(message1);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void consume2() {
        try {
            String body1 = "{\n" +
                    "  \"bizId\": \"TEST002\",\n" +
                    "  \"createTime\": 1665283255773,\n" +
                    "  \"id\": 1000,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"receiveSiteId\": 100,\n" +
                    "  \"productType\": \"NONE\",\n" +
                    "  \"shouldScanCount\": 100010101,\n" +
                    "  \"sendVehicleBizId\": \"TEST002\",\n" +
                    "  \"version\": 10,\n" +
                    "  \"yn\": 1\n" +
                    "}\n";

            Message message1 = new Message();
            message1.setText(body1);
            jyCollectDataInitSplitConsumer.consume(message1);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void consume3() {
        try {
            String body1 = "{\n" +
                    "  \"bizId\": \"TEST002\",\n" +
                    "  \"createTime\": 1665283255773,\n" +
                    "  \"id\": 1000,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"receiveSiteId\": 100,\n" +
                    "  \"productType\": \"NONE\",\n" +
                    "  \"shouldScanCount\": 100010101,\n" +
                    "  \"sendVehicleBizId\": \"TEST002\",\n" +
                    "  \"version\": 10,\n" +
                    "  \"yn\": 1\n" +
                    "}\n";

            Message message1 = new Message();
            message1.setText(body1);
            jyCollectDataSplitBatchInitConsumer.consume(message1);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }

    @Test
    public void consume4() {
        try {
            String body1 = "{\n" +
                    "  \"bizId\": \"TEST002\",\n" +
                    "  \"createTime\": 1665283255773,\n" +
                    "  \"id\": 1000,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"receiveSiteId\": 100,\n" +
                    "  \"productType\": \"NONE\",\n" +
                    "  \"shouldScanCount\": 100010101,\n" +
                    "  \"sendVehicleBizId\": \"TEST002\",\n" +
                    "  \"version\": 10,\n" +
                    "  \"yn\": 1\n" +
                    "}\n";

            Message message1 = new Message();
            message1.setText(body1);
            jyCollectStatusBatchUpdateWaybillSplitConsumer.consume(message1);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
