package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.BoardQueryReq;
import com.jd.bluedragon.common.dto.comboard.response.BoardQueryResp;
import com.jd.bluedragon.external.gateway.service.JyComboardSealGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author liwenji
 * @date 2022-12-23 10:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyComboardSealGatewayServiceImplTest {
    
    @Autowired
    private JyComboardSealGatewayService jyComboardSealGatewayService;
    
    @Test
    public void listComboardBySendFlowTest() {
        BoardQueryReq boardQueryReq = new BoardQueryReq();
        boardQueryReq.setEndSiteId(39);
        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(910);
        boardQueryReq.setCurrentOperate(currentOperate);
        JdCResponse<BoardQueryResp> jdCResponse = jyComboardSealGatewayService.listComboardBySendFlow(boardQueryReq);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }
}
