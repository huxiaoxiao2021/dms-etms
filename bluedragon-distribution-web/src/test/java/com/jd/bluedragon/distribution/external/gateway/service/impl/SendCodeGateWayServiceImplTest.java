package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.external.gateway.service.SendCodeGateWayService;
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
public class SendCodeGateWayServiceImplTest {

    @Autowired
    private SendCodeGateWayService sendCodeGateWayService;

    @Test
    public void testcheckSendCodeAndAlliance(){
        sendCodeGateWayService.checkSendCodeAndAlliance("910-14762-20190729150527014");
    }
}
