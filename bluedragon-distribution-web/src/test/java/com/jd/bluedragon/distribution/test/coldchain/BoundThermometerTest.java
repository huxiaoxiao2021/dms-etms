package com.jd.bluedragon.distribution.test.coldchain;

import com.jd.bluedragon.core.base.WayBillThermometerApiManager;
import com.jd.bluedragon.core.base.WayBillThermometerApiManagerImpl;
import com.jd.bluedragon.distribution.api.response.ColdChainOperationResponse;
import com.jd.bluedragon.distribution.coldchain.dto.ThermometerRequest;
import com.jd.bluedragon.distribution.coldchain.service.ColdChainOperationServiceImpl;
import com.jd.ccmp.ctm.api.WayBillThermometerApi;
import com.jd.ccmp.ctm.dto.CommonDto;
import com.jd.ccmp.ctm.dto.WaybillRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author lijie
 * @date 2020/4/6 10:24
 */
@RunWith(MockitoJUnitRunner.class)
public class BoundThermometerTest {

    @InjectMocks
    public ColdChainOperationServiceImpl coldChainOperationService;

    @Mock
    public WayBillThermometerApi wayBillThermometerApi;

    @InjectMocks
    public WayBillThermometerApiManagerImpl wayBillThermometerApiManager;

    private ThermometerRequest request;

    private WaybillRequest waybillRequest;

    @Before
    public void initMocks() throws Exception{
        request = new ThermometerRequest();
        request.setPackageCode("JDV000051629805-1-1-");
        request.setThermometerCode("WESDXC12345678");
        request.setCabinetCode("WEWEWEASD12345678");

        waybillRequest = new WaybillRequest();
        waybillRequest.setWaybillCode("JDV000051629805");
        waybillRequest.setPackageCode("JDV000051629805-1-1-");
        waybillRequest.setTemperDeviceID("WESDXC12345678");

        when(wayBillThermometerApi.bindWaybillPackage(any(WaybillRequest.class))).thenReturn(new CommonDto<String>());
    }

    @Test
    public void testBoundThermometer(){
        ColdChainOperationResponse response = coldChainOperationService.boundThermometer(request);
        Assert.assertEquals(null,response);
    }

    @Test
    public void testBindWaybillPackage(){
        ColdChainOperationResponse response = wayBillThermometerApiManager.bindWaybillPackage(waybillRequest);
        Assert.assertEquals(new Integer(0),response.getCode());
    }
}
