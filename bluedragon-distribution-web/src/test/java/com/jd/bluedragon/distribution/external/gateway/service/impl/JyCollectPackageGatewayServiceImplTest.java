package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.CrossDataReq;
import com.jd.bluedragon.common.dto.comboard.response.CrossDataResp;
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
    private JyComboardGatewayService jyComboardGatewayService;

    @Test
    public void listCrossDataTest() {
        CrossDataReq crossDataReq = new CrossDataReq();
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        crossDataReq.setPageNo(1);
        crossDataReq.setPageSize(4);
        crossDataReq.setCurrentOperate(currentOperate);
        JdCResponse<CrossDataResp> crossDataRespJdCResponse = jyComboardGatewayService.listCrossData(crossDataReq);
        System.out.println(JsonHelper.toJson(crossDataRespJdCResponse));
    }
}
