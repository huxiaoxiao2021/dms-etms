package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumable.domain.ReceivePackingConsumableDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.consumer.packingConsumable.CPackingConsumableConsumer;
import com.jd.bluedragon.distribution.consumer.packingConsumable.PackingConsumableConsumer;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/6/23 10:44
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class PackingConsumabelConsumerTest {

    @Autowired
    private com.jd.bluedragon.distribution.consumer.packingConsumable.PackingConsumableConsumer packingConsumableConsumer;

    @Autowired
    private CPackingConsumableConsumer cPackingConsumableConsumer;

    @Autowired
    WaybillConsumableRecordService waybillConsumableRecordService;

    @Autowired
    WaybillConsumableRelationService waybillConsumableRelationService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    private WaybillConsumableRecord oldRecord;

    private BaseStaffSiteOrgDto dto;

//    @Before
//    public void before(){
//        oldRecord = new WaybillConsumableRecord();
//        dto = new BaseStaffSiteOrgDto();
//        dto.setSiteCode(910);
//        dto.setSiteName("马驹桥分拣中心");
//        when(waybillConsumableRecordService.queryOneByWaybillCode(anyString())).thenReturn(oldRecord);
//        when(baseMajorManager.getBaseSiteBySiteId(anyInt())).thenReturn(dto);
//
//    }

    @Test
    public void test() throws Exception {
        Message message = new Message();
        String text = "{\n" +
                "\t\"waybillCode\": \"JDV000464947872\",\n" +
                "\t\"messageType\": 1,\n" +
                "\t\"dmsCode\": 910,\n" +
                "\t\"operateUserErp\": \"tms3f\",\n" +
                "\t\"operateUserName\": \"tms3f\",\n" +
                "\t\"operateTime\": \"2020-06-23 10:05:03\",\n" +
                "\t\"packingChargeList\": [{\n" +
                "\t\t\"packingCode\": \"HC003\",\n" +
                "\t\t\"packingName\": \"3号\",\n" +
                "\t\t\"packingType\": \"TY001\",\n" +
                "\t\t\"packingTypeName\": \"纸箱\",\n" +
                "\t\t\"packingVolume\": 1.2,\n" +
                "\t\t\"volumeCoefficient\": 1.0,\n" +
                "\t\t\"packingSpecification\": \"46*28*33\",\n" +
                "\t\t\"packingUnit\": \"个\",\n" +
                "\t\t\"packingNumber\": 2.0,\n" +
                "\t\t\"packingCharge\": 3.2,\n" +
                "\t\t\"initPackingNumber\": 2\n" +
                "\t}]\n" +
                "}";
        message.setText(text);
        packingConsumableConsumer.consume(message);
    }

    @Test
    public void consumer() throws Exception {
        String text = "{\"boxChargeDetails\":[{\"barCode\":\"HC003\",\"boxCount\":0,\"boxMoney\":10,\"boxName\":\"卷筒纸3号\",\"boxNumber\":1,\"boxType\":0,\"materialAmount\":15,\"materialCode\":\"HC003\",\"materialName\":\"3号\",\"materialNumber\":1,\"materialSpecification\":\"46*28*33\",\"materialTypeCode\":\"TY001\",\"materialTypeName\":\"纸箱\",\"materialUnit\":\"个\",\"materialVolume\":0.042504,\"price\":0.0,\"volumeCoefficient\":1.0},{\"barCode\":\"HC004\",\"boxCount\":0,\"boxMoney\":10,\"boxName\":\"纸箱1号old\",\"boxNumber\":1,\"boxType\":0,\"materialAmount\":15,\"materialCode\":\"HC004\",\"materialName\":\"4号\",\"materialNumber\":1,\"materialSpecification\":\"30*23*23\",\"materialTypeCode\":\"TY001\",\"materialTypeName\":\"纸箱\",\"materialUnit\":\"个\",\"materialVolume\":0.01587,\"price\":0.0,\"volumeCoefficient\":1.0},{\"barCode\":\"HC008\",\"boxCount\":0,\"boxMoney\":10,\"boxName\":\"联排膜9\",\"boxNumber\":3,\"boxType\":0,\"materialAmount\":30,\"materialCode\":\"HC008\",\"materialName\":\"木箱\",\"materialNumber\":3,\"materialSpecification\":\"200*100*300\",\"materialTypeCode\":\"TY004\",\"materialTypeName\":\"木箱\",\"materialUnit\":\"方\",\"materialVolume\":6.0,\"price\":0.0,\"volumeCoefficient\":1.0},{\"barCode\":\"HC009\",\"boxCount\":0,\"boxMoney\":10,\"boxName\":\"联排膜3\",\"boxNumber\":2,\"boxType\":0,\"materialAmount\":40,\"materialCode\":\"HC009\",\"materialName\":\"木架1号\",\"materialNumber\":2,\"materialSpecification\":\"10*20*30\",\"materialTypeCode\":\"TY003\",\"materialTypeName\":\"木架\",\"materialUnit\":\"方\",\"materialVolume\":0.006,\"price\":0.0,\"volumeCoefficient\":2.0}],\"boxTotalPrice\":0.0,\"cancelReason\":\"\",\"commandId\":\"JDVF00001641037\",\"consignerCredentialsNumber\":\"210803198809100021\",\"consignerCredentialsType\":20183191,\"dmsCode\":910,\"entryId\":18019,\"entryName\":\"王玉坤\",\"goods\":\"\",\"isCancel\":0,\"isExistenceTask\":1,\"orgId\":6,\"packCount\":1,\"packageCode\":\"JDVF00001641037-5-1-\",\"pdaTime\":1646989217481,\"siteId\":39,\"siteName\":\"石景山营业部\",\"source\":1,\"vloum_height\":1,\"vloum_length\":2.0,\"vloum_width\":3,\"volume\":6,\"waybillCode\":\"JDVF00001641037\",\"waybillSign\":\"30001000310800020000000030002000000100020002000000002002010001000000001100000012000101000000200900000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"weighingType\":0,\"weight\":1.0}";

        System.out.println(JsonHelper.toJson(JsonHelper.fromJson(text, ReceivePackingConsumableDto.class)));
        cPackingConsumableConsumer.consume(new Message("2",text, "12"));

    }

}
