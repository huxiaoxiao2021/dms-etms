package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.distribution.box.domain.GenerateBoxReq;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxResp;
import com.jd.bluedragon.distribution.box.domain.StoreInfo;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.external.gateway.store.TpCollectPackageGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;


/**
 * @author liwenji
 * @date 2022-11-16 17:23
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class TpCollectPackageGatewayServiceTest {

    @Autowired
    private TpCollectPackageGatewayService tpCollectPackageGatewayService;




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
    public void testUpdateBo() throws Exception {

        UpdateBoxReq req =new UpdateBoxReq();
        req.setBoxCode("BW1004240117250000100109");

        StoreInfo storeInfo =new StoreInfo();
        storeInfo.setStoreType("wms");
        storeInfo.setCky2(1);
        storeInfo.setStoreId(2);
        req.setStoreInfo(storeInfo);


        req.setReceiveSiteCode(39);

        req.setMixBoxType(1);
        req.setTransportType(2);

        req.setUserCode(1);
        req.setUserName("wuyoude");

        req.setOpeateTime(new Date());


        JdResponse resp = tpCollectPackageGatewayService.updateBox(req);

        System.out.println(JsonHelper.toJson(resp));
    }


}
