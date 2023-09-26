package com.jd.bluedragon.distribution.exception;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;
import com.jd.bluedragon.distribution.jy.service.exception.JyDamageExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
@Slf4j
public class JyDamageExceptionServiceTest {

    @Autowired
    private JyDamageExceptionService jyDamageExceptionService;
    private ExpDamageDetailReq req;

    @Before
    public void init() {
        req = new ExpDamageDetailReq();
        req.setBizId("TestFrank001");
        req.setUserErp("bjxings");
        req.setSiteId(910);
    }

    @Test
    public void processTaskOfDamage() {
        jyDamageExceptionService.processTaskOfDamage(req);
    }
}
