package com.jd.bluedragon.distribution.busineCode.sendCode.service;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/distribution-core-context-test.xml"})
public class SendCodeServiceImplTest {

    @Autowired
    private SendCodeService sendCodeService;

    @Test
    public void createSendCode() {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> param = new HashMap<>();
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, 910);
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, 364605);
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh,true);
        Assert.assertNotNull(sendCodeService.createSendCode(param, BusinessCodeFromSourceEnum.UNKNOWN_SYS,"bjxings"));
    }

    @Test
    public void queryByCode() {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> param = new HashMap<>();
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, 910);
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, 364605);
        param.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh,true);
        String sendCode = sendCodeService.createSendCode(param, BusinessCodeFromSourceEnum.UNKNOWN_SYS,"bjxings");
        Assert.assertNotNull(sendCodeService.queryByCode(sendCode));
    }

    @Test
    public void isFreshSendCode() {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> param1 = new HashMap<>();
        param1.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, 910);
        param1.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, 364605);
        param1.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh,true);
        String sendCode1 = sendCodeService.createSendCode(param1, BusinessCodeFromSourceEnum.UNKNOWN_SYS,"bjxings");
        Assert.assertTrue(sendCodeService.isFreshSendCode(sendCode1));

        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> param2 = new HashMap<>();
        param2.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, 910);
        param2.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, 364605);
        param2.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh,false);
        String sendCode2 = sendCodeService.createSendCode(param2, BusinessCodeFromSourceEnum.UNKNOWN_SYS,"bjxings");
        Assert.assertFalse(sendCodeService.isFreshSendCode(sendCode2));

        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, Object> param3 = new HashMap<>();
        param3.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, 910);
        param3.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, 364605);
        String sendCode3 = sendCodeService.createSendCode(param3, BusinessCodeFromSourceEnum.UNKNOWN_SYS,"bjxings");
        Assert.assertFalse(sendCodeService.isFreshSendCode(sendCode3));
    }
}