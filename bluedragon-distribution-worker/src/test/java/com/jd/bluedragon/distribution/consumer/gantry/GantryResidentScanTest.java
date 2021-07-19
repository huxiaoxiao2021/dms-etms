package com.jd.bluedragon.distribution.consumer.gantry;

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
 *
 * @Auther: lqingl
 * @Date : 2021-02-23
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class GantryResidentScanTest {

    private static final Logger logger = LoggerFactory.getLogger(GantryResidentScanTest.class);

    @Autowired
    private GantryResidentScanConsumer gantryResidentScanConsumer;

    @Test
    public void consume() {
        try {
            Message message = new Message();
            String text = "{\n" +
                    "  \"barCode\" : \"JD0047730237406-1-1-\",\n" +
                    "  \"boxCode\" : \"BC1001201121120029350629\",\n" +
                    "  \"consignGood\" : \"文件证照\",\n" +
                    "  \"height\" : 4.0,\n" +
                    "  \"width\" : 3.0,\n" +
                    "  \"length\" : 2.0,\n" +
                    "  \"weight\" : 1.0,\n" +
                    "  \"volume\" : 24.0,\n" +
                    "  \"operatorId\" : 10053,\n" +
                    "  \"operatorName\" : \"刑松\",\n" +
                    "  \"operatorErp\" : \"bjxings\",\n" +
                    "  \"operateTime\" : \"2021-07-13 16:00:00\",\n" +
                    "  \"operateSiteCode\" : 39,\n" +
                    "  \"operateSiteName\" : \"石景山站\"\n" +
                    "}";
            message.setText(text);
            gantryResidentScanConsumer.consume(message);

        }catch (Exception e){
            logger.error("服务异常!",e);
            Assert.fail();
        }
    }

}