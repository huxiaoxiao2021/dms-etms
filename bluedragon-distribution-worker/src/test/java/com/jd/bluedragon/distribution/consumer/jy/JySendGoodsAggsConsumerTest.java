package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.agg.JySendGoodsAggsBakConsumer;
import com.jd.bluedragon.distribution.consumer.jy.agg.JySendGoodsAggsMainConsumer;
import com.jd.bluedragon.distribution.consumer.jy.agg.JySendProductAggsBakConsumer;
import com.jd.bluedragon.distribution.consumer.jy.agg.JySendProductAggsMainConsumer;
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
public class JySendGoodsAggsConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(JySendGoodsAggsConsumerTest.class);

    @Autowired
    private JySendGoodsAggsMainConsumer jySendGoodsAggsMainConsumer;
    @Autowired
    private JySendGoodsAggsBakConsumer jySendGoodsAggsBakConsumer;
    @Autowired
    private JySendProductAggsMainConsumer jySendProductAggsMainConsumer;
    @Autowired
    private JySendProductAggsBakConsumer jySendProductAggsBakConsumer;
    @Test
    public void consume() {
        try {
            //language=JSON
            String body = "{\n" +
                    "  \"actualScanBoxCodeCount\": 100,\n" +
                    "  \"actualScanCount\": 100,\n" +
                    "  \"actualScanPackageCodeCount\": 100,\n" +
                    "  \"actualScanVolume\": 0.0,\n" +
                    "  \"actualScanWeight\": 0.0,\n" +
                    "  \"bizCreateTime\": 0,\n" +
                    "  \"bizId\": \"TEST002\",\n" +
                    "  \"createTime\": 1665283255773,\n" +
                    "  \"flowKey\": \"100|100\",\n" +
                    "  \"forceSendCount\": 100,\n" +
                    "  \"id\": 1000,\n" +
                    "  \"interceptScanCount\": 100,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"receiveSiteId\": 100,\n" +
                    "  \"sendCode\": \"TEST001\",\n" +
                    "  \"sendCodeRemoveFlag\": false,\n" +
                    "  \"sendVehicleBizId\": \"TEST002\",\n" +
                    "  \"shouldScanCount\": 100,\n" +
                    "  \"totalForceSendCount\": 100,\n" +
                    "  \"totalInterceptCount\": 100,\n" +
                    "  \"totalScannedBoxCodeCount\": 100,\n" +
                    "  \"totalScannedCount\": 100,\n" +
                    "  \"totalScannedPackageCodeCount\": 100,\n" +
                    "  \"totalScannedVolume\": 0.0,\n" +
                    "  \"totalScannedWeight\": 0.0,\n" +
                    "  \"totalShouldScanCount\": 100,\n" +
                    "  \"transWorkItemCode\": \"TEST001\",\n" +
                    "  \"ts\": 1665283255773,\n" +
                    "  \"vehicleNumber\": \"TEST001111\",\n" +
                    "  \"vehicleStatus\": 100,\n" +
                    "  \"vehicleVolume\": 0.0,\n" +
                    "  \"vehicleWeight\": 0.0,\n" +
                    "  \"version\": 8,\n" +
                    "  \"yn\": 1\n" +
                    "}\n";

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
            Message message = new Message();
            message.setText(body);

            Message message1 = new Message();
            message1.setText(body1);
            jySendGoodsAggsMainConsumer.consume(message);
            jySendGoodsAggsBakConsumer.consume(message);
            jySendProductAggsMainConsumer.consume(message1);
            jySendProductAggsBakConsumer.consume(message1);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
