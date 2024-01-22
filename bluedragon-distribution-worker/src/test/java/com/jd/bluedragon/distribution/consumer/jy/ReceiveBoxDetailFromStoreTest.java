package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.box.BoxDetailFromStoreConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class ReceiveBoxDetailFromStoreTest {

    @Autowired
    private BoxDetailFromStoreConsumer boxDetailFromStoreConsumer;
    @Test
    public void testReceiveBoxDetailFromStore() throws Exception {

        String body ="{\n" +
                "  \"boxCode\": \"BW1004240117260000100210\",\n" +
                "  \"storeInfo\": {\n" +
                "    \"storeType\": \"wms\",\n" +
                "    \"cky2\": 6,\n" +
                "    \"storeId\": 98\n" +
                "  },\n" +
                "  \"receiveSiteCode\": 39,\n" +
                "  \"packageList\": [\n" +
                "    {\n" +
                "      \"waybillCode\": \"JD0003423608307\",\n" +
                "      \"packageCode\": \"JD0003423608307-1-20-\",\n" +
                "      \"userCode\": 1,\n" +
                "      \"userName\": \"wuyoude\",\n" +
                "      \"opeateTime\": \"2024-01-19 16:39:28\",\n" +
                "      \"opreateType\": 10\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        Message message = new Message();
        message.setTopic("turnbox_info_back");
        message.setText(body);

        boxDetailFromStoreConsumer.consume(message);
    }

}
