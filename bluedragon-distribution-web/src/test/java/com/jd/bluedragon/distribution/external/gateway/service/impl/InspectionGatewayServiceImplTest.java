package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.api.request.HintCheckRequest;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.external.service.DmsPackingConsumableService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.rest.inspection.InspectionResource;
import com.jd.ql.dms.common.domain.JdResponse;
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
 * @date 2020/6/17 14:14
 */
@RunWith(MockitoJUnitRunner.class)
public class InspectionGatewayServiceImplTest {

    @InjectMocks
    private InspectionGatewayServiceImpl inspectionGatewayService;

    @Mock
    private WaybillConsumableRecordService waybillConsumableRecordService;

    @Mock
    private InspectionResource inspectionResource;

    @Mock
    private DmsPackingConsumableService dmsPackingConsumableService;

    private WaybillConsumableRecord record;

    private HintCheckRequest request;

    private JdResponse<InspectionResult> result;

    private JdResponse<Boolean> response;

    @Before
    public void before() {
        record = new WaybillConsumableRecord();
        record.setConfirmStatus(0);
        record.setWaybillCode("JD0018391871085");
        record.setDmsId(910);
        record.setDmsName("马驹桥分拣中心");
        when(waybillConsumableRecordService.queryOneByWaybillCode(anyString())).thenReturn(record);

        result = new JdResponse<InspectionResult>();
        InspectionResult inspectionResult = new InspectionResult("A999");
        inspectionResult.setHintMessage("请放至库位！");
        when(inspectionResource.getStorageCode(anyString(),anyInt())).thenReturn(result);

        response = new JdResponse<>();
        response.setData(false);
        response.setMessage("包装耗材提示");
        when(dmsPackingConsumableService.getConfirmStatusByWaybillCode(anyString())).thenReturn(response);
    }

    @Test
    public void testIsExistConsumableRecord(){
        String waybillCode = "JD0018391871085";
        inspectionGatewayService.isExistConsumableRecord(waybillCode);
    }

    @Test
    public void testHintCheck () {
        request = new HintCheckRequest();
        request.setPackageCode("JD0018391871085");
        request.setCreateSiteCode(910);

        inspectionGatewayService.hintCheck(request);
    }
}
