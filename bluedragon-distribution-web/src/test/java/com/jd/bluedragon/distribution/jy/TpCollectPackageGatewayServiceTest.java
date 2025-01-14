package com.jd.bluedragon.distribution.jy;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.CancelCollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CancelCollectPackageResp;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.common.dto.comboard.response.ComboardScanResp;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxReq;
import com.jd.bluedragon.distribution.box.domain.GenerateBoxResp;
import com.jd.bluedragon.distribution.box.domain.StoreInfo;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
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
    @Autowired
    JyCollectPackageGatewayService jyCollectPackageGatewayService;
    @Autowired
    JyComboardGatewayService jyComboardGatewayService;





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

    @Test
    public void collectLoading() throws Exception {

        CollectPackageReq collectPackageReq =new CollectPackageReq();
        collectPackageReq.setBizId("JCP24030500000001");
        collectPackageReq.setBoxCode("LL1001240305290000100109");
        collectPackageReq.setBarCode("BC1001240305250000100109");

        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(40240);
        currentOperate.setSiteName("通武分拣中心");

        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);



        JdCResponse<CollectPackageResp> resp = jyCollectPackageGatewayService.collectScan(collectPackageReq);

        System.out.println(JsonHelper.toJson(resp));
    }

    @Test
    public void cancelCollectLoading() throws Exception {

        CancelCollectPackageReq collectPackageReq =new CancelCollectPackageReq();
        collectPackageReq.setBizId("JCP24030400000001");
        collectPackageReq.setBoxCode("LL1001240304250000100109");
        collectPackageReq.setBarCode("BC1001240304240000100210");

        CurrentOperate currentOperate =new CurrentOperate();
        currentOperate.setSiteCode(910);
        currentOperate.setSiteName("马驹桥分拣中心");

        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);



        JdCResponse<CancelCollectPackageResp> resp = jyCollectPackageGatewayService.cancelCollectPackage(collectPackageReq);

        System.out.println(JsonHelper.toJson(resp));
    }

    @Test
    public void comboardScan() throws Exception {
        ComboardScanReq scanReq =new ComboardScanReq();
        scanReq.setTemplateCode("CTT22120500000007");
        scanReq.setGroupCode("G00000047004");
        scanReq.setBarCode("LL1001240305290000100109");
        scanReq.setScanType(0);
        scanReq.setEndSiteId(38);
        scanReq.setSupportMutilSendFlow(true);
        //scanReq.setNeedSkipSendFlowCheck(true);
        CurrentOperate operate = new CurrentOperate();
        operate.setSiteCode(910);
        operate.setSiteName("北京马驹桥分拣中心");
        operate.setOperateTime(new Date());
        scanReq.setCurrentOperate(operate);
        User user = new User();
        user.setUserName("李文吉");
        user.setUserErp("liwenji3");
        user.setUserCode(123456);
        scanReq.setUser(user);
        JdVerifyResponse<ComboardScanResp> re = jyComboardGatewayService.comboardScan(scanReq);
        System.out.println(JsonHelper.toJson(re));
    }





}
