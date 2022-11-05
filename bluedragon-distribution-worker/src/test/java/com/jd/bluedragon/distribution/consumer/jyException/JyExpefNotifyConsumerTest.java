package com.jd.bluedragon.distribution.consumer.jyException;

import com.jd.bluedragon.distribution.consumer.jy.exception.PackageCodePrintConsumer;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class JyExpefNotifyConsumerTest {
    @Autowired
    PackageCodePrintConsumer packageCodePrintConsumer;


    @Test
    public void onMessagesTest() throws Exception{
        List<Message> messages = new ArrayList<Message>();
        Message message = new Message();
        message.setTopic("package_replenish_print_compete");
        message.setText("{\n" +
                "\"operateType\" : 100103,\n" +
                "\"waybillCode\" : \"JDV007937389840\",\n" +
                "\"packageCode\" : \"JDV007937389840-1-1-\",\n" +
                "\"templateGroupCode\" : \"C\",\n" +
                "\"templateName\" : \"dms-unite1010-m\",\n" +
                "\"templateVersion\" : 0,\n" +
                "\"userCode\" : 17331,\n" +
                "\"userName\" : \"吴有德\",\n" +
                "\"userErp\" : \"wuyoude\",\n" +
                "\"operateTime\" : 1585264183038,\n" +
                "\"siteCode\" : 910,\n" +
                "\"siteName\" : \"大连玉浓营业部\"\n" +
                "}");
        messages.add(message);
        packageCodePrintConsumer.onMessages(messages);
    }
}
