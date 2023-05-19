package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.MixScanTaskDefaultNameQueryReq;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-19 11:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyWarehouseSendGatewayServiceTest {
    
    @Autowired
    private JyWarehouseSendGatewayService jyWarehouseSendGatewayService;
    
    private static final CurrentOperate CURRENT_OPERATE = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    private static final User USER = new User(111,"李文吉");
    static {
        USER.setUserErp("liwenji3");
    }
    
    @Test
    public void getMixScanTaskDefaultNameTest() {
        MixScanTaskDefaultNameQueryReq req = new MixScanTaskDefaultNameQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER);
        JdCResponse<String> response = jyWarehouseSendGatewayService.getMixScanTaskDefaultName(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
}
