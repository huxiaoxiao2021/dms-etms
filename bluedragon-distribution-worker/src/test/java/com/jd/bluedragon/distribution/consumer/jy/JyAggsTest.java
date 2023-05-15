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
public class JyAggsTest {

    private static final Logger logger = LoggerFactory.getLogger(JyAggsTest.class);


    @Autowired
    private JyAggsConsumer jyAggsConsumer;
    @Test
    public void consume() {
        try {
            //language=JSON
            String body = "{\n"
                + "\t\"bizId\": \"-1\",\n"
                + "\t\"boardCode\": \"-888888888\",\n"
                + "\t\"boardCount\": 6,\n"
                + "\t\"createTime\": 1683534182476,\n"
                + "\t\"interceptCount\": 1,\n"
                + "\t\"jyAggsTypeEnum\": \"JY_COMBOARD_AGGS\",\n"
                + "\t\"key\": \"1386070-4301--1--1--1--1\",\n"
                + "\t\"operateSiteId\": \"1386070\",\n"
                + "\t\"packageTotalScannedCount\": 328,\n"
                + "\t\"productType\": \"-1\",\n"
                + "\t\"receiveSiteId\": \"4301\",\n"
                + "\t\"scanType\": \"-1\",\n"
                + "\t\"scannedCount\": 335,\n"
                + "\t\"sendFlow\": \"1386070-4301\",\n"
                + "\t\"volume\": 0.0,\n"
                + "\t\"waitScanCount\": 560,\n"
                + "\t\"weight\": 0.0\n"
                + "}";


            Message message = new Message();
            message.setTopic("jy_aggs");
            message.setText(body);




            jyAggsConsumer.consume(message);
        } catch (Exception e) {
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
