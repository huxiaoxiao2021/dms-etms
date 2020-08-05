package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.distribution.consumer.packingConsumable.PackingConsumableConsumer;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/6/23 10:44
 */
@RunWith(MockitoJUnitRunner.class)
public class PackingConsumabelConsumerTest {

    @InjectMocks
    private com.jd.bluedragon.distribution.consumer.packingConsumable.PackingConsumableConsumer packingConsumableConsumer;

    @Mock
    WaybillConsumableRecordService waybillConsumableRecordService;

    @Mock
    WaybillConsumableRelationService waybillConsumableRelationService;

    @Mock
    private BaseMajorManager baseMajorManager;

    private WaybillConsumableRecord oldRecord;

    private BaseStaffSiteOrgDto dto;

    @Before
    public void before(){
        oldRecord = new WaybillConsumableRecord();
        dto = new BaseStaffSiteOrgDto();
        dto.setSiteCode(910);
        dto.setSiteName("马驹桥分拣中心");
        when(waybillConsumableRecordService.queryOneByWaybillCode(anyString())).thenReturn(oldRecord);
        when(baseMajorManager.getBaseSiteBySiteId(anyInt())).thenReturn(dto);

    }

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

}
