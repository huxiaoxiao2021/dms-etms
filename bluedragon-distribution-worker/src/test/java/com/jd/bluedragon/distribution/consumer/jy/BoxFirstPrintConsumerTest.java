package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.consumer.box.BoxFirstPrintConsumer;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Description
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
@Slf4j
public class BoxFirstPrintConsumerTest {
    
    @Autowired
    private BoxFirstPrintConsumer boxFirstPrintConsumer;
    
    @Test
    public void consumerTest() {
        Message message = new Message();
        Box box = new Box();
        box.setCode("BC1001231019260000403101");
        box.setType("BC");
        box.setYn(1);
        box.setMixBoxType(0);
        box.setCreateSiteCode(910);
        box.setCreateSiteName("马驹桥");
        box.setReceiveSiteCode(40240);
        box.setCreateTime(new Date());
        box.setCreateUserCode(17331);
        box.setCreateUser("wuyoude");
        box.setTransportType(2);
        message.setText(JsonHelper.toJson(box));
        try {
            boxFirstPrintConsumer.consume(message);
        } catch (Exception e) {
            log.info("消费异常",e);
        }
    }
}
