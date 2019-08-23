package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.external.gateway.service.AllianceBusiGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author : xumigen
 * @date : 2019/7/29
 */
@ContextConfiguration( {"classpath:distribution-web-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AllianceBusiGatewayServiceImplTest {

    @Autowired
    private AllianceBusiGatewayService allianceBusiGatewayService;

    @Test
    public void testcheckAllianceMoney(){
        allianceBusiGatewayService.checkAllianceMoney("JDVA00023884001-1-2-");
        allianceBusiGatewayService.checkAllianceMoney("JDVA00023884037-1-2-");
        allianceBusiGatewayService.checkAllianceMoney("JDVA00023884028-1-2-");
    }
}
