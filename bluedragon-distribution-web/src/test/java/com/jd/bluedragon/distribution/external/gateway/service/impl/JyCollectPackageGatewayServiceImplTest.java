package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.collectpackage.request.CancelCollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.request.CollectPackageReq;
import com.jd.bluedragon.common.dto.collectpackage.response.CancelCollectPackageResp;
import com.jd.bluedragon.common.dto.collectpackage.response.CollectPackageResp;
import com.jd.bluedragon.common.dto.comboard.request.CrossDataReq;
import com.jd.bluedragon.common.dto.comboard.response.CrossDataResp;
import com.jd.bluedragon.external.gateway.service.JyCollectPackageGatewayService;
import com.jd.bluedragon.external.gateway.service.JyComboardGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyCollectPackageGatewayServiceImplTest {

    @Autowired
    private JyCollectPackageGatewayService jyCollectPackageGatewayService;


    //todo  @liwenji 补全
    @Test
    public void printBoxAndCreateCollectPackageTask() {
        //生成箱号-生成集包任务
    }


    @Test
    public void collectPackage() {
        CollectPackageReq collectPackageReq =new CollectPackageReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");

        collectPackageReq.setCurrentOperate(currentOperate);
        collectPackageReq.setUser(user);


        collectPackageReq.setBizId("");
        collectPackageReq.setBoxCode("");
        collectPackageReq.setBarCode("");



        JdCResponse<CollectPackageResp> collectPackageRespJdCResponse = jyCollectPackageGatewayService.collectScan(collectPackageReq);
        System.out.println(JsonHelper.toJson(collectPackageRespJdCResponse));
    }

    @Test
    public void cancelCollectPackage() {
        CancelCollectPackageReq cancelCollectPackageReq =new CancelCollectPackageReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        User user =new User();
        user.setUserErp("wuyoude");
        user.setUserName("吴有德");

        cancelCollectPackageReq.setCurrentOperate(currentOperate);
        cancelCollectPackageReq.setUser(user);




        JdCResponse<CancelCollectPackageResp> cancelCollectPackageRespJdCResponse = jyCollectPackageGatewayService.cancelCollectPackage(cancelCollectPackageReq);
        System.out.println(JsonHelper.toJson(cancelCollectPackageRespJdCResponse));
    }
}
