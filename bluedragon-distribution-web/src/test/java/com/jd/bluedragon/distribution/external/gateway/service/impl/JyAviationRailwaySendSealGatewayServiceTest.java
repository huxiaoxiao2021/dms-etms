package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.ShuttleQuerySourceEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationBarCodeDetailResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendScanResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendVehicleProgressResp;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        AviationSendTaskListReq request = new AviationSendTaskListReq();

        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setStatusCode(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode());

//        request.setKeyword();
//        request.setFilterConditionDto();

        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.fetchAviationToSendAndSendingList(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testPageFetchAviationTaskByNextSite() {
        AviationSendTaskQueryReq request = new AviationSendTaskQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setStatusCode(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode());
        request.setPageNo(1);
        request.setPageSize(30);
        request.setNextSiteId(40240);

        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.pageFetchAviationTaskByNextSite(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testPageFetchAviationToSealAndSealedList() {
        AviationSendTaskSealListReq request = new AviationSendTaskSealListReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
//        request.setStatusCode(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_N.getCode());
        request.setStatusCode(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode());
        request.setPageNo(1);
        request.setPageSize(30);

        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.pageFetchAviationToSealAndSealedList(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testPageFetchAviationSealedList() {
//        AviationSealedListReq request = new AviationSealedListReq();
//        request.setCurrentOperate(SITE_910);
//        request.setUser(USER_wuyoude);
//        request.setGroupCode(GROUP_CODE);
//        request.setPost(POST);
//        request.setPageNo(1);
//        request.setPageSize(30);
//
//        request.setSendVehicleBizId("SST23082500000056");
//        request.setSendVehicleDetailBizId("TW23082500975599-001");
//
//        aviationRailwaySendSealGatewayService.pageFetchAviationSealedList(request);

    }

    @Test
    public void testPageFetchFilterCondition() {
        FilterConditionQueryReq request = new FilterConditionQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.pageFetchFilterCondition(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testpageFetchShuttleSendTaskList() {
        ShuttleSendTaskReq request = new ShuttleSendTaskReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setShuttleQuerySource(ShuttleQuerySourceEnum.SEAL_N.getCode());
        request.setPageNo(1);
        request.setPageSize(30);

        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.pageFetchShuttleSendTaskList(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

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
        SendTaskBindReq request = new SendTaskBindReq();

        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");

        List<SendTaskBindDto> sendTaskBindDtoList = new ArrayList<>();
        request.setSendTaskBindDtoList(sendTaskBindDtoList);

        SendTaskBindDto bindDto = new SendTaskBindDto();
        bindDto.setBizId("SST23082400000132");
        bindDto.setDetailBizId("DCH15520230824152643");
        bindDto.setFlightNumber("CA-1001");
        sendTaskBindDtoList.add(bindDto);

        SendTaskBindDto bindDto2 = new SendTaskBindDto();
        bindDto2.setBizId("SST23082400000123");
        bindDto2.setDetailBizId("DCH14620230824152633");
        bindDto2.setFlightNumber("CA-1002");
        sendTaskBindDtoList.add(bindDto2);

        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.sendTaskBinding(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testsendTaskUnbinding() {
        SendTaskUnbindReq request = new SendTaskUnbindReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");
        request.setUnbindBizId("SST23082400000132");
        request.setUnbindDetailBizId("DCH15520230824152643");
        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.sendTaskUnbinding(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testfetchSendTaskBindingData() {
        SendTaskBindQueryReq request = new SendTaskBindQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");
        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.fetchSendTaskBindingData(request);
                System.out.println("succ");
                request.setShuttleQuerySource(ShuttleQuerySourceEnum.SEAL_Y.getCode());
                Object obj1 = aviationRailwaySendSealGatewayService.fetchSendTaskBindingData(request);
                System.out.println("succ");

            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @Test
    public void testfetchShuttleTaskSealCarInfo() {
        ShuttleTaskSealCarQueryReq request = new ShuttleTaskSealCarQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");
        int i = 0;
        while(i++<100) {
            try{
                Object obj = aviationRailwaySendSealGatewayService.fetchShuttleTaskSealCarInfo(request);
                System.out.println("succ");
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

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
        AviationSendScanReq request = new AviationSendScanReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setSendVehicleBizId("SST23082400000051");
        request.setSendVehicleDetailBizId("DCH20230824144926");
        request.setBarCodeType(2);
        request.setBarCode("JD0003421571498-1-1-");
        JdVerifyResponse<AviationSendScanResp> jdVerifyResponse = aviationRailwaySendSealGatewayService.scan(request);
        System.out.println(JsonHelper.toJson(jdVerifyResponse));
    }
    @Test
    public void testgetAviationSendVehicleProgress() {
        AviationSendVehicleProgressReq request = new AviationSendVehicleProgressReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setBizId("SST23082400000051");
        JdCResponse<AviationSendVehicleProgressResp> progress = 
                aviationRailwaySendSealGatewayService.getAviationSendVehicleProgress(request);
        System.out.println(JsonHelper.toJson(progress));
    }
    @Test
    public void testabnormalBarCodeDetail() {
        AviationSendAbnormalPackReq req = new AviationSendAbnormalPackReq();
        req.setBizId("");
        aviationRailwaySendSealGatewayService.abnormalBarCodeDetail(req);

    }
    @Test
    public void testsendBarCodeDetail() {
        AviationBarCodeDetailReq request = new AviationBarCodeDetailReq();
        request.setBizId("SST22062700000004");
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setScanedType(2);
        request.setPageNumber(1);
        request.setPageSize(15);
        JdCResponse<AviationBarCodeDetailResp> response = aviationRailwaySendSealGatewayService.sendBarCodeDetail(request);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void aviationSendCompleteTest() {
        AviationSendCompleteReq request =  new AviationSendCompleteReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setSendVehicleBizId("SST23082400000051");        
        aviationRailwaySendSealGatewayService.aviationSendComplete(request);
    }
}
