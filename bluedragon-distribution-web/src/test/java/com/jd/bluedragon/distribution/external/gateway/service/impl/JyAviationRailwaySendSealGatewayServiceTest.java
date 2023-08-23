package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/8/22 16:50
 * @Description
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyAviationRailwaySendSealGatewayServiceTest {


    private static final CurrentOperate SITE_910 = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final User USER_wuyoude = new User(17331,"吴有德");

    public static final String GROUP_CODE = "G00000059567";
    public static final String POST = JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode();

    static {
        USER_wuyoude.setUserErp("wuyoude");
    }

    @Autowired
    private JyAviationRailwaySendSealGatewayService aviationRailwaySendSealGatewayService;


    @Test
    public void testScanTypeOptions() {
        try{
            Object obj = aviationRailwaySendSealGatewayService.scanTypeOptions();
            System.out.println("succ");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testFetchAviationToSendAndSendingList() {
        aviationRailwaySendSealGatewayService.fetchAviationToSendAndSendingList(null);

    }

    @Test
    public void testPageFetchAviationTaskByNextSite() {
        aviationRailwaySendSealGatewayService.pageFetchAviationTaskByNextSite(null);

    }

    @Test
    public void testpageFetchAviationToSealAndSealedList() {
        aviationRailwaySendSealGatewayService.pageFetchAviationToSealAndSealedList(null);

    }

    @Test
    public void testpageFetchAviationSealedList() {
        aviationRailwaySendSealGatewayService.pageFetchAviationSealedList(null);

    }

    @Test
    public void testpageFetchFilterCondition() {
        aviationRailwaySendSealGatewayService.pageFetchFilterCondition(null);

    }

    @Test
    public void testpageFetchShuttleSendTaskList() {
        aviationRailwaySendSealGatewayService.pageFetchShuttleSendTaskList(null);

    }

    @Test
    public void testfetchTransportCodeList() {
        aviationRailwaySendSealGatewayService.fetchTransportCodeList(null);

    }

    @Test
    public void testscanAndCheckTransportInfo() {
        aviationRailwaySendSealGatewayService.scanAndCheckTransportInfo(null);

    }

    @Test
    public void testsendTaskBinding() {
        aviationRailwaySendSealGatewayService.sendTaskBinding(null);

    }

    @Test
    public void testsendTaskUnbinding() {
        aviationRailwaySendSealGatewayService.sendTaskUnbinding(null);

    }

    @Test
    public void testfetchSendTaskBindingData() {
        aviationRailwaySendSealGatewayService.fetchSendTaskBindingData(null);

    }

    @Test
    public void testfetchShuttleTaskSealCarInfo() {
        aviationRailwaySendSealGatewayService.fetchShuttleTaskSealCarInfo(null);

    }

    @Test
    public void testshuttleTaskSealCar() {
        aviationRailwaySendSealGatewayService.shuttleTaskSealCar(null);

    }

    @Test
    public void testaviationTaskSealCar() {
        aviationRailwaySendSealGatewayService.aviationTaskSealCar(null);

    }

    @Test
    public void testscan() {
        aviationRailwaySendSealGatewayService.scan(null);

    }
    @Test
    public void testgetAviationSendVehicleProgress() {
        aviationRailwaySendSealGatewayService.getAviationSendVehicleProgress(null);

    }
    @Test
    public void testabnormalBarCodeDetail() {
        aviationRailwaySendSealGatewayService.abnormalBarCodeDetail(null);

    }
    @Test
    public void testsendBarCodeDetail() {
        aviationRailwaySendSealGatewayService.sendBarCodeDetail(null);
    }
}
