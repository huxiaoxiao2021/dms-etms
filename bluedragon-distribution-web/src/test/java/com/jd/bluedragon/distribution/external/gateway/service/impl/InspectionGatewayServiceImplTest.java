package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/6/17 14:14
 */
@RunWith(MockitoJUnitRunner.class)
public class InspectionGatewayServiceImplTest {

    @InjectMocks
    private InspectionGatewayServiceImpl inspectionGatewayService;

    @Mock
    private WaybillConsumableRecordService waybillConsumableRecordService;

    private WaybillConsumableRecord record;

    @Before
    public void before() {
        record = new WaybillConsumableRecord();
        record.setConfirmStatus(0);
        record.setWaybillCode("JD0018391871085");
        record.setDmsId(910);
        record.setDmsName("马驹桥分拣中心");
        when(waybillConsumableRecordService.queryOneByWaybillCode(anyString())).thenReturn(record);
    }

    @Test
    public void testIsExistConsumableRecord(){
        String waybillCode = "JD0018391871085";
        inspectionGatewayService.isExistConsumableRecord(waybillCode);
    }
}
