package com.jd.bluedragon.distribution.jy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.api.request.LoginRequest;
import com.jd.bluedragon.distribution.api.request.client.DeviceInfo;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationInfo;
import com.jd.bluedragon.distribution.api.response.LoginUserResponse;
import com.jd.bluedragon.distribution.external.service.DmsBaseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/20 16:20
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class DADALoginTest {

    @Autowired
    private DmsBaseService dmsBaseService;

    @Test
    public void clientLoginNewTest(){
        LoginRequest request = new LoginRequest();
        request.setBaseVersionCode("01-20010101");
        request.setCheckVersion(true);
        request.setErpAccount("372924199109182116");
        request.setLoginVersion(new Byte("1"));
//        request.setPassword("4a734174a6f79b7cc1b94200c4a9c11f");
        request.setPositionCode("GW00184002");
        request.setClientInfo( "{\"deviceId\":\"3F05C77D5F4EC\",\"ipv4\":null,\"ipv6\":null,\"macAdress\":null,\"programType\":\"60\",\"versionCode\":\"3.3.39\",\"versionName\":\"PDA_Android\"}");

        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("flame");
        deviceInfo.setDeviceSn("");
        deviceInfo.setDeviceType("Pixel 4");
        deviceInfo.setProgramType("android");
        deviceInfo.setRunningEnv("test");
        deviceInfo.setVersionCode("3.3.39");
        request.setDeviceInfo(deviceInfo);

        DeviceLocationInfo deviceLocationInfo = new DeviceLocationInfo();
        deviceLocationInfo.setLatitude(new BigDecimal(39.7852461));
        deviceLocationInfo.setLongitude(new BigDecimal(116.554481));
        request.setDeviceLocationInfo(deviceLocationInfo);

        LoginUserResponse loginUserResponse = dmsBaseService.clientLoginNew(request);
        System.out.println("登录结果-------------------");
        System.out.println("登录结果"+ JSON.toJSONString(loginUserResponse));
    }

}
