package com.jd.bluedragon.distribution.external.batch;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.api.batch.ISendCodeApi;
import com.jd.bluedragon.distribution.constants.SysSourceEnum;
import com.jd.bluedragon.distribution.dto.batch.SendCodeReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @ClassName SendCodeApiTest
 * @Description
 * @Author wyh
 * @Date 2021/1/5 11:02
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class SendCodeApiTest {

    @Autowired
    private ISendCodeApi sendCodeApi;

    @Test
    public void genSendCodeTest() {
        SendCodeReq sendCodeReq = new SendCodeReq();
        sendCodeReq.setBeginningSiteCode(910);
        sendCodeReq.setDestSiteCode(39);
        sendCodeReq.setCreateUserErp("bjxings");
        sendCodeReq.setSysSource(SysSourceEnum.SYS_REVERSE.getSysCode());
        System.out.println(JSON.toJSONString(sendCodeApi.genSendCode(sendCodeReq)));
    }
}
