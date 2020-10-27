package com.jd.bluedragon.distribution.external.gateway.waybill;

import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.external.gateway.waybill.WaybillGateWayExternalService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/4/22 17:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
public class WaybillGateWayExternalServiceImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    private WaybillGateWayExternalService waybillGateWayExternalService;

    @Test
    public void syncWaybillCodeAndBoxCode() {

        try {
            WaybillSyncRequest request = new WaybillSyncRequest();

            request.setTenantCode("ECONOMIC_NET");
            request.setStartSiteCode("15436");
            request.setEndSiteCode("1612");
            request.setOperatorId("123");
            request.setOperatorName("操作人02");
            request.setOperatorUnitName("ztd");
            request.setOperatorTime(new Date());
            request.setBoxCode("BC1001200422140000000202");
            request.setWaybillCode("JDAB00000005298");
            request.setPackageCode("JDAB00000005298-1-1-");
            request.setOperationType(1);

            GateWayBaseResponse<Void> response = waybillGateWayExternalService.syncWaybillCodeAndBoxCode(request, null);
            response.getResultCode();
        }catch (Exception e){
            e.printStackTrace();
            Assert.assertTrue(false);
        }
        Assert.assertTrue(true);
    }
}