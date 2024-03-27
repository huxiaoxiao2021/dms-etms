package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.vehicle.JyArriveCarPackageRetryAutoInspectionConsumer;
import com.jd.bluedragon.distribution.consumer.jy.vehicle.TmsSendArriveAndBookConsumer;
import com.jd.bluedragon.distribution.jy.dto.unload.trust.TmsSendArriveAndBookMqBody;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class TmsSendArriveAndBookConsumerTest {

    @Autowired
    private TmsSendArriveAndBookConsumer tmsSendArriveAndBookConsumer;

    @Autowired
    private JyArriveCarPackageRetryAutoInspectionConsumer jyArriveCarPackageRetryAutoInspectionConsumer;

    @Test
    public void testConsume() {
        String packageCode = "JD0003423555556-31-50-";
        String arriveSiteCode = "010F002";//910

        TmsSendArriveAndBookMqBody body = new TmsSendArriveAndBookMqBody();
        body.setTransBookCode(System.currentTimeMillis() + "");
        body.setPackageCode(packageCode);
        body.setTransWorkCode("TW23122100986759");
        body.setTransWorkItemCode("TW23122100986759-001");
        body.setEndNodeCode(arriveSiteCode);
        body.setOperateTime(new Date());
        body.setCreateTime(new Date());

        int i = 100;
        while(i++ < 200) {
            try{
                Message message = new Message();
                message.setText(JsonHelper.toJson(body));
                message.setBusinessId(body.getPackageCode());
                tmsSendArriveAndBookConsumer.consume(message);
                 System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    @Test
    public void testExceptionRetryConsume() {
        String json = "{\n" +
                "  \"packageCode\" : \"JDX000282619787-3-100-\",\n" +
                "  \"waybillCode\" : \"JD0003423555556\",\n" +
                "  \"transBookCode\" : \"1709536678152\",\n" +
                "  \"transWorkCode\" : \"TW23122100986759\",\n" +
                "  \"transWorkItemCode\" : \"TW23122100986759-001\",\n" +
                "  \"arriveSiteCode\" : \"010F002\",\n" +
                "  \"arriveSiteName\" : \"北京马驹桥分拣中心\",\n" +
                "  \"arriveSiteId\" : 910,\n" +
                "  \"operateTime\" : 1709536678152,\n" +
                "  \"createTime\" : 1709536678152,\n" +
                "  \"firstConsumerTime\" : 1709539260809\n" +
                "}";
        int i = 100;
        while(i++ < 200) {
            try{
                Message message = new Message();
                message.setText(json);
                message.setBusinessId("JDX000282619787-3-100-");
                jyArriveCarPackageRetryAutoInspectionConsumer.consume(message);
                System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
