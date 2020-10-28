package com.jd.bluedragon.distribution.external.gateway.waybill;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.ThirdBoxCodeMessageVO;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.external.gateway.waybill.WaybillGateWayExternalService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2020/4/22 17:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( {"classpath:distribution-web-context.xml"})
public class WaybillGateWayExternalServiceImplTest {
    @Autowired
    @Qualifier(value = "thirdBoxCodeProducer")
    private DefaultJMQProducer thirdBoxCodeProducer;
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

    @Test
    public void pushBoxCodeTest() throws Exception {
//        Method pushBoxCode = waybillGateWayExternalService.getClass().getDeclaredMethod("pushBoxCode", Box.class, Integer.class);
//        pushBoxCode.setAccessible(true);

        Box box = new Box();
        box.setCode("boxCode00001");
        box.setCreateSiteCode(910);
        box.setReceiveSiteCode(10456);
        box.setType("BC");

        ThirdBoxCodeMessageVO message = new ThirdBoxCodeMessageVO();
        message.setBoxCode(box.getCode());
        message.setCreateSiteCode(String.valueOf(box.getCreateSiteCode()));
        message.setReceiveSiteCode(String.valueOf(box.getReceiveSiteCode()));
        message.setBoxType(String.valueOf(box.getType()));
        message.setCreateSiteType(String.valueOf(10000));

//       pushBoxCode.invoke(waybillGateWayExternalService, box, 10000);
        thirdBoxCodeProducer.sendOnFailPersistent(box.getCode(), JsonHelper.toJson(message));
        System.out.println("DONE....");
    }
}