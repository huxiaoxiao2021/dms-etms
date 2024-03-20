package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarRequest;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.ShuttleQuerySourceEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationBarCodeDetailResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendScanResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendVehicleProgressResp;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.TransportDataDto;
import com.jd.bluedragon.common.dto.seal.request.ShuttleTaskSealCarReq;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.station.gateway.impl.UserSignGatewayServiceImpl;
import com.jd.bluedragon.external.gateway.service.JyAviationRailwaySendSealGatewayService;
import com.jd.bluedragon.external.gateway.service.NewSealVehicleGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @Author zhengchengfa
 * @Date 2023/8/22 16:50
 * @Description
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyAviationRailwaySendSealGatewayServiceTest {


    private static final CurrentOperate SITE_910 = new CurrentOperate(65396,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final User USER_wuyoude = new User(65396,"吴有德");

    public static final String GROUP_CODE = "G00000130001";
    public static final String POST = JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode();

    static {
        USER_wuyoude.setUserErp("wuyoude");
    }

    @Autowired
    private JyAviationRailwaySendSealGatewayService aviationRailwaySendSealGatewayService;

    @Autowired
    private NewSealVehicleGatewayService newSealVehicleGatewayService;

    @Autowired
    private UserSignGatewayServiceImpl userSignGatewayService;



    @Test
    public void newCheckTranCodeAndBatchCode(){
        String json = "{\n" +
                "    \"createSiteCode\": 65396,\n" +
                "    \"queryWeightVolumeFlag\": true,\n" +
                "    \"sealCarSource\": 20,\n" +
                "    \"sealCarType\": 10,\n" +
                "    \"sendCode\": \"65396-121674-20231026160464275\",\n" +
                "    \"transportCode\": \"T230924003955\"\n" +
                "}";
        SealCarPreRequest jsonParam = JSONObject.parseObject(json, SealCarPreRequest.class);
        int i = 0;
        while(i++<100) {
            Object obj = newSealVehicleGatewayService.newCheckTranCodeAndBatchCode(jsonParam);
            System.out.println("success");
        }
    }

    @Test
    public void testScanTypeOptions() {
        Object obj = aviationRailwaySendSealGatewayService.scanTypeOptions();
        System.out.println("succ");
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
            Object obj = aviationRailwaySendSealGatewayService.fetchAviationToSendAndSendingList(request);
            System.out.println("succ");
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
            Object obj = aviationRailwaySendSealGatewayService.pageFetchAviationTaskByNextSite(request);
            System.out.println("succ");
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

        String json = "{\n" +
                "    \"currentOperate\": {\n" +
                "        \"dmsCode\": \"010F002\",\n" +
                "        \"operateTime\": 1694506313778,\n" +
                "        \"operatorId\": \"58476\",\n" +
                "        \"operatorTypeCode\": 1,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"华北\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"北京马驹桥分拣中心\"\n" +
                "    },\n" +
                "    \"filterConditionDto\": {},\n" +
                "    \"groupCode\": \"G00000124001\",\n" +
                "    \"keyword\": \"\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"post\": \"AVIATION_RAILWAY_SEND_SEAL_POSITION\",\n" +
                "    \"requestId\": \"b3cad29537dd43d68249dc260efc8815\",\n" +
                "    \"statusCode\": 21,\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    }\n" +
                "}";
        AviationSendTaskSealListReq jsonParam = JSONObject.parseObject(json, AviationSendTaskSealListReq.class);
        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.pageFetchAviationToSealAndSealedList(jsonParam);
            System.out.println("succ");
        }

    }

    @Test
    public void testPageFetchAviationSealedList() {
        AviationSealedListReq request = new AviationSealedListReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setPageNo(1);
        request.setPageSize(30);
        request.setSendVehicleBizId("SST23082500000056");
        request.setSendVehicleDetailBizId("TW23082500975599-001");

        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.pageFetchAviationSealedList(request);
            System.out.println("succ");
        }
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
            Object obj = aviationRailwaySendSealGatewayService.pageFetchFilterCondition(request);
            System.out.println("succ");
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
            Object obj = aviationRailwaySendSealGatewayService.pageFetchShuttleSendTaskList(request);
            System.out.println("succ");
        }
    }

    @Test
    public void testfetchTransportCodeList() {
        TransportCodeQueryReq request = new TransportCodeQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setBizId("SST23082300000007");
        request.setDetailBizId("DCH202308230002");

        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.fetchTransportCodeList(request);
            System.out.println("succ");
        }

    }

    @Test
    public void testScanAndCheckTransportInfo() {
        ScanAndCheckTransportInfoReq request = new ScanAndCheckTransportInfoReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setTransportCode("T230831003803");
        request.setNextSiteId(3011);
        request.setTaskType(SendTaskTypeEnum.AVIATION.getCode());

        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.scanAndCheckTransportInfo(request);
            System.out.println("succ");
        }
    }

    @Test
    public void testSendTaskBinding() {
        String json = "{\n" +
                "    \"bizId\": \"SST23092700000013\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"dmsCode\": \"010L001\",\n" +
                "        \"operateTime\": 1695783556941,\n" +
                "        \"operatorId\": \"60061\",\n" +
                "        \"operatorTypeCode\": 1,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"华北\",\n" +
                "        \"siteCode\": 65396,\n" +
                "        \"siteName\": \"JD北京顺义分拣中心\"\n" +
                "    },\n" +
                "    \"detailBizId\": \"TW23092700979954-001\",\n" +
                "    \"groupCode\": \"G00000130001\",\n" +
                "    \"post\": \"AVIATION_RAILWAY_SEND_SEAL_POSITION\",\n" +
                "    \"requestId\": \"abd148ae84d34bbfa6a13b6767a8e781\",\n" +
                "    \"sendTaskBindDtoList\": [\n" +
                "        {\n" +
                "            \"bizId\": \"SST23092500000014\",\n" +
                "            \"detailBizId\": \"DC23092100200513\",\n" +
                "            \"flightNumber\": \"CA0925\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 10053,\n" +
                "        \"userErp\": \"bjxings\",\n" +
                "        \"userName\": \"刑松\"\n" +
                "    }\n" +
                "}";
        int i = 0;
        while(i++<100) {
            SendTaskBindReq jsonParam = JSONObject.parseObject(json, SendTaskBindReq.class);
            Object obj = aviationRailwaySendSealGatewayService.sendTaskBinding(jsonParam);
            System.out.println("succ");
        }
    }

    @Test
    public void testSendTaskUnbinding() {
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
            Object obj = aviationRailwaySendSealGatewayService.sendTaskUnbinding(request);
            System.out.println("succ");
        }
    }

    @Test
    public void testFetchSendTaskBindingData() {
        SendTaskBindQueryReq request = new SendTaskBindQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");

        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.fetchSendTaskBindingData(request);
            System.out.println("succ");
            request.setShuttleQuerySource(ShuttleQuerySourceEnum.SEAL_Y.getCode());
            Object obj1 = aviationRailwaySendSealGatewayService.fetchSendTaskBindingData(request);
            System.out.println("succ");
        }
    }

    @Test
    public void testFetchShuttleTaskSealCarInfo() {
        ShuttleTaskSealCarQueryReq request = new ShuttleTaskSealCarQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");
        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.fetchShuttleTaskSealCarInfo(request);
            System.out.println("succ");
        }
    }

    @Test
    public void testShuttleTaskSealCar() {

        String transportCode = "T200312000816";
        Integer endSiteId = 40240;
        String bizId = "SST23090700000025";
        String detailBizId = "TW23090700977922-001";

        ScanAndCheckTransportInfoReq request = new ScanAndCheckTransportInfoReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setTransportCode(transportCode);
        request.setNextSiteId(endSiteId);
        request.setTaskType(SendTaskTypeEnum.VEHICLE.getCode());

        ShuttleTaskSealCarReq sealReq = new ShuttleTaskSealCarReq();
        sealReq.setCurrentOperate(SITE_910);
        sealReq.setUser(USER_wuyoude);
        sealReq.setGroupCode(GROUP_CODE);
        sealReq.setPost(POST);
        sealReq.setWeight(11d);
        sealReq.setVolume(12d);
        sealReq.setItemNum(100);
        sealReq.setPalletCount(10);
        sealReq.setBizId(bizId);
        sealReq.setDetailBizId(detailBizId);
        sealReq.setTransportCode(transportCode);

        List<String> batchCodes = Arrays.asList("910-40240-20230425114453733");
        sealReq.setScanBatchCodes(batchCodes);
        List<String> sealCodes = Arrays.asList("1111111111C","2222222222C","3333333333C");
        sealReq.setScanSealCodes(sealCodes);

        String json = "    {\n" +
                "        \"bizId\": \"SST23092500000032\",\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010K001\",\n" +
                "            \"operateTime\": 1695636528602,\n" +
                "            \"operatorId\": \"59942\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 65396,\n" +
                "            \"siteName\": \"JD北京顺义分拣中心\"\n" +
                "        },\n" +
                "        \"departureTimeStr\": \"00:02\",\n" +
                "        \"detailBizId\": \"TW23092500979878-001\",\n" +
                "        \"groupCode\": \"G00000130001\",\n" +
                "        \"itemNum\": 0,\n" +
                "        \"palletCount\": 1,\n" +
                "        \"post\": \"AVIATION_RAILWAY_SEND_SEAL_POSITION\",\n" +
                "        \"requestId\": \"556f2202037945e6ad713dce4e11fc9a\",\n" +
                "        \"scanBatchCodes\": [\n" +
                "            \"65396-66316-20230924119443735\"\n" +
                "        ],\n" +
                "        \"scanSealCodes\": [],\n" +
                "        \"transWay\": 2,\n" +
                "        \"transWayName\": \"公路整车\",\n" +
                "        \"transportCode\": \"T230925003960\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 18225,\n" +
                "            \"userErp\": \"xumigen\",\n" +
                "            \"userName\": \"徐迷根\"\n" +
                "        },\n" +
                "        \"vehicleNumber\": \"京F88882\",\n" +
                "        \"volume\": 100,\n" +
                "        \"weight\": 0\n" +
                "    }";
        int i = 0;
        while (i++ < 100) {
            ShuttleTaskSealCarReq param = JSONObject.parseObject(json, ShuttleTaskSealCarReq.class);
//            JdCResponse<TransportDataDto> transportDataDtoJdCResponse = aviationRailwaySendSealGatewayService.scanAndCheckTransportInfo(param);
//            if(!transportDataDtoJdCResponse.isSucceed() || Objects.isNull(transportDataDtoJdCResponse.getData())) {
//                continue;
//            }
//            TransportDataDto transportDataDto = transportDataDtoJdCResponse.getData();
//
//
//            sealReq.setDepartureTimeStr(transportDataDto.getDepartureTimeStr());
//            sealReq.setTransWay(transportDataDto.getTransWay());
//            sealReq.setTransWayName(transportDataDto.getTransTypeName());
            JdCResponse<Void> res = aviationRailwaySendSealGatewayService.shuttleTaskSealCar(param);
            System.out.println("succ");
        }
    }

    @Test
    public void testAviationTaskSealCar() {
        //测试910-40240的封车
        String transportCode = "T190703000723";
        Integer endSiteId = 40240;
        String bizId = "SST23082400000051";
        String detailBizId = "DCH20230824144926";

        ScanAndCheckTransportInfoReq request = new ScanAndCheckTransportInfoReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);
        request.setTransportCode(transportCode);
        request.setNextSiteId(endSiteId);
        request.setTaskType(SendTaskTypeEnum.AVIATION.getCode());

        AviationTaskSealCarReq sealRequest = new AviationTaskSealCarReq();
        sealRequest.setCurrentOperate(SITE_910);
        sealRequest.setUser(USER_wuyoude);
        sealRequest.setGroupCode(GROUP_CODE);
        sealRequest.setPost(POST);
        sealRequest.setBizId(bizId);
        sealRequest.setBookingCode(detailBizId);
        sealRequest.setTransportCode(transportCode);
        sealRequest.setWeight(11d);
        sealRequest.setVolume(12d);
        sealRequest.setItemNum(100);

        String json = "{\n" +
                "    \"bizId\": \"SST23092500000037\",\n" +
                "    \"bookingCode\": \"DC23092100200517\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"dmsCode\": \"010K001\",\n" +
                "        \"operateTime\": 1695636843366,\n" +
                "        \"operatorId\": \"59942\",\n" +
                "        \"operatorTypeCode\": 1,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"华北\",\n" +
                "        \"siteCode\": 65396,\n" +
                "        \"siteName\": \"JD北京顺义分拣中心\"\n" +
                "    },\n" +
                "    \"departureTimeStr\": \"02:00:00\",\n" +
                "    \"groupCode\": \"G00000130001\",\n" +
                "    \"itemNum\": 0,\n" +
                "    \"post\": \"AVIATION_RAILWAY_SEND_SEAL_POSITION\",\n" +
                "    \"requestId\": \"85683a9cbb5047f5bc1de7be865af590\",\n" +
                "    \"transportCode\": \"R2203172797800\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 18225,\n" +
                "        \"userErp\": \"xumigen\",\n" +
                "        \"userName\": \"徐迷根\"\n" +
                "    },\n" +
                "    \"volume\": 0.1,\n" +
                "    \"weight\": 1\n" +
                "}";

        int i = 0;
        while (i++ < 100) {
            AviationTaskSealCarReq param = JSONObject.parseObject(json, AviationTaskSealCarReq.class);

//            JdCResponse<TransportDataDto> transportDataDtoJdCResponse = aviationRailwaySendSealGatewayService.scanAndCheckTransportInfo(request);
//            if(!transportDataDtoJdCResponse.isSucceed() || Objects.isNull(transportDataDtoJdCResponse.getData())) {
//                continue;
//            }
//            TransportDataDto transportDataDto = transportDataDtoJdCResponse.getData();
//
//
//            sealRequest.setDepartureTimeStr(transportDataDto.getDepartureTimeStr());
//            sealRequest.setTransWay(transportDataDto.getTransWay());
//            sealRequest.setTransWayName(transportDataDto.getTransTypeName());

            Object obj = aviationRailwaySendSealGatewayService.aviationTaskSealCar(param);
            System.out.println("succ");
        }
    }

    @Test
    public void testScan() {
        AviationSendScanReq request = new AviationSendScanReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setSendVehicleBizId("SST23092500000020");
        request.setSendVehicleDetailBizId("DC23092100200516");
        request.setBarCodeType(1);
        request.setBarCode("JD0003422003922-1-1-");
        request.setConfirmSendDestId(121674L);
        request.setForceSubmit(true);
        JdVerifyResponse<AviationSendScanResp> jdVerifyResponse = aviationRailwaySendSealGatewayService.scan(request);
        System.out.println(JsonHelper.toJson(jdVerifyResponse));
    }

    @Test
    public void testGetAviationSendVehicleProgress() {
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
    public void testAbnormalBarCodeDetail() {
        AviationSendAbnormalPackReq req = new AviationSendAbnormalPackReq();
        req.setBizId("");
        aviationRailwaySendSealGatewayService.abnormalBarCodeDetail(req);
    }
    @Test
    public void testSendBarCodeDetail() {
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
        JdCResponse<Void> response = aviationRailwaySendSealGatewayService.aviationSendComplete(request);
        System.out.println(JsonHelper.toJson(response));
    }


    @Test
    public void testValidateTranCodeAndSendCode() {
        String transportCode = "T200820001041";
        String sendCode = "910-40240-20230831148514042";
        ScanSendCodeValidReq request = new ScanSendCodeValidReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setSendCode(sendCode);
        request.setTransportCode(transportCode);
        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.validateTranCodeAndSendCode(request);
            System.out.println("succ");
        }
    }


    @Test
    public void testfetchToSealShuttleTaskDetail() {
        String transportCode = "T200820001041";
        String sendCode = "910-40240-20230831148514042";
        ShuttleTaskSealCarQueryReq request = new ShuttleTaskSealCarQueryReq();
        request.setCurrentOperate(SITE_910);
        request.setUser(USER_wuyoude);
        request.setGroupCode(GROUP_CODE);
        request.setPost(POST);

        request.setDetailBizId("TW23082500975485-001");
        request.setBizId("SST23082500000051");
        int i = 0;
        while(i++<100) {
            Object obj = aviationRailwaySendSealGatewayService.fetchToSealShuttleTaskDetail(request);
            System.out.println("succ");
        }
    }



    @Test
    public void doSealCarWithVehicleJobTest() {
        String json = "  {\n" +
                "        \"sealCarDtoList\": [\n" +
                "            {\n" +
                "                \"batchCodes\": [\n" +
                "                    \"65396-66316-20240118111383831\",\n" +
                "                    \"65396-66316-20240118111384155\",\n" +
                "                    \"65396-66316-20240118111384166\",\n" +
                "                    \"65396-66316-20240118111383842\",\n" +
                "                    \"65396-66316-20240118181384531\"\n" +
                "                ],\n" +
                "                \"palletCount\": \"1\",\n" +
                "                \"sealCarTime\": \"2024-01-31 16:50:02\",\n" +
                "                \"sealCarType\": 10,\n" +
                "                \"sealCodes\": [],\n" +
                "                \"sealSiteId\": 65396,\n" +
                "                \"sealSiteName\": \"JD北京顺义分拣中心\",\n" +
                "                \"sealUserCode\": \"wuyoude\",\n" +
                "                \"sealUserName\": \"吴有德\",\n" +
                "                \"transportCode\": \"T230924003955\",\n" +
                "                \"vehicleNumber\": \"京F88882\",\n" +
                "                \"volume\": 41,\n" +
                "                \"weight\": 53\n" +
                "            }\n" +
                "        ]\n" +
                "    }";
        SealCarRequest sealCarRequest = JSONObject.parseObject(json, SealCarRequest.class);
        for(int i = 0; i<100; i++) {
            try{
                sealCarRequest.setPost(JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode());
                JdCResponse res = newSealVehicleGatewayService.doSealCarWithVehicleJob(sealCarRequest);
            }catch (Exception e) {
                System.out.println("err:" + e.getMessage());
            }
        }
        System.out.println("succ");
    }
    @Test
    public void test() {
        JdCResponse<List<JyJobType>> listJdCResponse = userSignGatewayService.queryAllJyJobType();
        System.out.println(com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(listJdCResponse));
    }
}
