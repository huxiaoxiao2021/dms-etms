package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.distribution.box.domain.GenerateBoxReq;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxResp;
import com.jd.bluedragon.distribution.box.domain.StoreInfo;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.external.gateway.store.TpCollectPackageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;


/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class TpCollectPackageGatewayServiceTest {

    @Autowired
    private TpCollectPackageGatewayService tpCollectPackageGatewayService;
    @Autowired
    DeliveryService deliveryService;




    @Test
    public void testGenerateBoxCode() throws Exception {

        GenerateBoxReq generateBoxReq =new GenerateBoxReq();

        generateBoxReq.setBoxType("BW");
        generateBoxReq.setBoxSubType("BWPT");
        generateBoxReq.setCount(5);
        generateBoxReq.setSource("04");
        JdResponse<GenerateBoxResp> resp = tpCollectPackageGatewayService.generateBoxCode(generateBoxReq);

        System.out.println(JsonHelper.toJson(resp));
    }


    @Test
    public void testUpdateBox() throws Exception {

        UpdateBoxReq req =new UpdateBoxReq();
        req.setBoxCode("BW1004240123290000100109");

        StoreInfo storeInfo =new StoreInfo();
        storeInfo.setStoreType("wms");
        storeInfo.setCky2(633);
        storeInfo.setStoreId(282);
        req.setStoreInfo(storeInfo);


        req.setReceiveSiteCode(39);

        req.setMixBoxType(0);
        req.setTransportType(2);

        req.setUserErp("wuxx");
        req.setUserName("吴有德");

        req.setOpeateTime(new Date());


        JdResponse resp = tpCollectPackageGatewayService.updateBox(req);

        System.out.println(JsonHelper.toJson(resp));
    }


    @Test
    public void getCancelSendByBox() throws Exception {

        List<SendDetail> sendDetailList = deliveryService.getCancelSendByBox("BW1004240123290000100109");

        System.out.println(JsonHelper.toJson(sendDetailList));
    }


}
