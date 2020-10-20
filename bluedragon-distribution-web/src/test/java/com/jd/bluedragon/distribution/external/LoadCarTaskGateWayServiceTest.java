package com.jd.bluedragon.distribution.external;

import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.LoadCarTaskCreateReq;
import com.jd.bluedragon.external.gateway.service.LoadCarTaskGateWayService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @program: bluedragon-distribution
 * @description: 测试
 * @author: wuming
 * @create: 2020-10-20 14:20
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:distribution-web-context.xml"})
public class LoadCarTaskGateWayServiceTest {

    @Autowired
    private LoadCarTaskGateWayService loadCarTaskGateWayService;

    @Test
    public void test(){

        Assert.assertTrue(loadCarTaskGateWayService.checkLicenseNumber("010A99999")
                .equals("京A99999"));

    }

    @Test
    public void createTest(){
        LoadCarTaskCreateReq loadCarTaskCreateReq=new LoadCarTaskCreateReq();
        loadCarTaskCreateReq.setCreateSiteCode(910L);
        loadCarTaskCreateReq.setCreateSiteName("北京马驹桥分拣中心");
        loadCarTaskCreateReq.setCreateUserErp("bjxings");
        loadCarTaskCreateReq.setCreateUserName("邢松");
        loadCarTaskCreateReq.setEndSiteCode(364605L);
        loadCarTaskCreateReq.setEndSiteName("北京通州分拣中心");
        loadCarTaskCreateReq.setLicenseNumber("豫A78868");
        loadCarTaskGateWayService.loadCarTaskCreate(loadCarTaskCreateReq);
    }





}
