package com.jd.bluedragon.distribution.consumer.jy;

import com.jd.bluedragon.distribution.consumer.jy.material.JyRecycleMaterialAutoInspectionBoxConsumer;
import com.jd.bluedragon.distribution.consumer.jy.material.RecycleMaterialOperateRecordConsume;
import com.jd.bluedragon.distribution.jy.dto.material.RecycleMaterialOperateRecordDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class RecycleMaterialOperateRecordConsumeTest {

    @Autowired
    private RecycleMaterialOperateRecordConsume recycleMaterialOperateRecordConsume;

    @Autowired
    private JyRecycleMaterialAutoInspectionBoxConsumer jyRecycleMaterialAutoInspectionBoxConsumer;
    @Test
    public void testConsume() {
        String materialCode = "AD17873821781273";

        RecycleMaterialOperateRecordDto body = new RecycleMaterialOperateRecordDto();
        body.setMaterialCode(materialCode);
        body.setOperateTime(System.currentTimeMillis());
        body.setSendTime(System.currentTimeMillis());
        body.setBizSource("DMS");
        body.setOperateType(1);
        body.setOperateSiteId("910");

        int i = 100;
        while(i++ < 200) {
            try{
                Message message = new Message();
                message.setText(JsonHelper.toJson(body));
                message.setBusinessId(body.getMaterialCode());
                recycleMaterialOperateRecordConsume.consume(message);
                 System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Test
    public void testConsume1() {
        String json = "{\n" +
                "  \"boxCode\" : \"BC1001240305250000100109\",\n" +
                "  \"materialCode\" : \"AD17873821781273\",\n" +
                "  \"operateSiteId\" : 910,\n" +
                "  \"operateSiteName\" : \"北京马驹桥分拣中心\",\n" +
                "  \"operateTime\" : 1709644197604,\n" +
                "  \"sendTime\" : 1709644832389\n" +
                "}";

        int i = 100;
        while(i++ < 200) {
            try{
                Message message = new Message();
                message.setText(json);
                message.setBusinessId("BC1001240305250000100109");
                jyRecycleMaterialAutoInspectionBoxConsumer.consume(message);
                System.out.println("success");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
