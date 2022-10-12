package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.JyUnloadCarAggsConsumer;
import com.jd.bluedragon.distribution.consumer.send.CancelSendDetailPrintHandoverConsumerTest;
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
 * @Date: 2022/10/11 18:53
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyUnloadCarAggsConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(JyUnloadCarAggsConsumerTest.class);

    @Autowired
    private JyUnloadCarAggsConsumer jyUnloadCarAggsConsumer;

    @Test
    public void consume() {
        try {
            //language=JSON
            String body = "{\n" +
                    "  \"actualScanCount\": 100,\n" +
                    "  \"bizId\": \"TEST001\",\n" +
                    "  \"createTime\": 1665282671046,\n" +
                    "  \"interceptActualScanCount\": 100,\n" +
                    "  \"interceptNotScanCount\": 100,\n" +
                    "  \"interceptShouldScanCount\": 100,\n" +
                    "  \"moreScanLocalCount\": 100,\n" +
                    "  \"moreScanOutCount\": 100,\n" +
                    "  \"moreScanTotalCount\": 100,\n" +
                    "  \"operateSiteId\": 100,\n" +
                    "  \"productType\": \"TEST001\",\n" +
                    "  \"sealCarCode\": \"TEST001\",\n" +
                    "  \"shouldScanCount\": 100,\n" +
                    "  \"totalLocalWithMoreScanCount\": 100,\n" +
                    "  \"totalScannedPackageCount\": 100,\n" +
                    "  \"totalSealPackageCount\": 100,\n" +
                    "  \"totalWithMoreScanCount\": 100,\n" +
                    "  \"ts\": 1665282671046,\n" +
                    "  \"yn\": 1\n" +
                    "}";
            Message message = new Message();
            message.setText(body);
            jyUnloadCarAggsConsumer.consume(message);
        }catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
