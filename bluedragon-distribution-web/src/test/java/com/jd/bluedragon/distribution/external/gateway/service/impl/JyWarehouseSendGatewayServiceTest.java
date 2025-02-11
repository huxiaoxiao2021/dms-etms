package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.external.gateway.service.DeviceGatewayService;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-05-19 11:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyWarehouseSendGatewayServiceTest {
    
    @Autowired
    private JyWarehouseSendGatewayService jyWarehouseSendGatewayService;

    @Autowired
    private DeviceGatewayService deviceGatewayService;

    @Autowired
    private JyNoTaskSendGatewayService jyNoTaskSendGatewayService;

    private static final CurrentOperate CURRENT_OPERATE = new CurrentOperate(910,"马驹桥分拣中心",new Date());
    public static final CurrentOperate SITE_40240 = new CurrentOperate(40240, "北京通州分拣中心", new Date());

    public static final CurrentOperate SITE_223094 = new CurrentOperate(223094, "北京马驹桥接货仓", new Date());

    public static final String POST = "GW00150001";

    private static final User USER = new User(111,"李文吉");
    public static final User USER_wuyoude = new User(17331,"吴有德");
    public static final String GROUP_CODE = "G00000059567";
    static {
        USER.setUserErp("liwenji3");
        USER_wuyoude.setUserErp("wuyoude");
    }


    @Test
    public void listVehicleTaskTest(){
        String json = "{\n" +
                "        \"bizId\": \"NSST23101100000001\",\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010K001\",\n" +
                "            \"operateTime\": 1697008637986,\n" +
                "            \"operatorId\": \"60614\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 0,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 40240,\n" +
                "            \"siteName\": \"北京通州分拣中心\"\n" +
                "        },\n" +
                "        \"pageNumber\": 1,\n" +
                "        \"pageSize\": 15,\n" +
                "        \"requestId\": \"1debe885af6b476d9771ea2f52e930a2\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 18225,\n" +
                "            \"userErp\": \"xumigen\",\n" +
                "            \"userName\": \"徐迷根\"\n" +
                "        }\n" +
                "    }";

        VehicleTaskReq param = JSONObject.parseObject(json, VehicleTaskReq.class);

        while (true) {

            JdCResponse response = jyNoTaskSendGatewayService.listVehicleTask(param);

            System.out.println(response.getCode());
        }
    }
    @Test
    public void getMixScanTaskDefaultNameTest() {
        MixScanTaskDefaultNameQueryReq req = new MixScanTaskDefaultNameQueryReq();
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setUser(USER);
        JdCResponse<String> response = jyWarehouseSendGatewayService.getMixScanTaskDefaultName(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void createMixScanTaskTest() {
        CreateMixScanTaskReq createMixScanTaskReq = new CreateMixScanTaskReq();
        createMixScanTaskReq.setTemplateName("接货仓混扫任务01");
        createMixScanTaskReq.setCurrentOperate(CURRENT_OPERATE);
        createMixScanTaskReq.setUser(USER);
        createMixScanTaskReq.setGroupCode("G00000059567");
        List<MixScanTaskDetailDto> list = new ArrayList<>();
        MixScanTaskDetailDto mixScanTaskDetailDto = new MixScanTaskDetailDto();
        mixScanTaskDetailDto.setEndSiteId(30L);
        mixScanTaskDetailDto.setEndSiteName("目的站点");
        mixScanTaskDetailDto.setSendVehicleDetailBizId("SST22070100000004");
        mixScanTaskDetailDto.setCrossCode("567");
        mixScanTaskDetailDto.setTabletrolleyCode("gyeydge");
        MixScanTaskDetailDto mixScanTaskDetailDto2 = new MixScanTaskDetailDto();
        mixScanTaskDetailDto2.setEndSiteId(33L);
        mixScanTaskDetailDto2.setEndSiteName("目的站点2");
        mixScanTaskDetailDto2.setSendVehicleDetailBizId("SST22070100000344");
        mixScanTaskDetailDto2.setCrossCode("324");
        mixScanTaskDetailDto2.setTabletrolleyCode("rfbrf");
        list.add(mixScanTaskDetailDto);
        list.add(mixScanTaskDetailDto2);
        createMixScanTaskReq.setSendFlowList(list);


        String json = "    {\n" +
                "        \"currentOperate\": {\n" +
                "            \"operateTime\": 1693192779127,\n" +
                "            \"operatorId\": \"104120845\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 4,\n" +
                "            \"orgName\": \"西南\",\n" +
                "            \"siteCode\": 910,\n" +
                "            \"siteName\": \"910ces\"\n" +
                "        },\n" +
                "        \"groupCode\": \"G00003810008\",\n" +
                "        \"requestId\": \"8665cd3366cf4742b81f45d3d988dffc\",\n" +
                "        \"sendFlowList\": [\n" +
                "            {\n" +
                "                \"crossCode\": \"\",\n" +
                "                \"endSiteId\": 821602,\n" +
                "                \"endSiteName\": \"成都新都分拣中心\",\n" +
                "                \"sendVehicleDetailBizId\": \"TW23082876701210-001\",\n" +
                "                \"tabletrolleyCode\": \"\",\n" +
                "                \"templateName\": \"1\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"templateName\": \"1\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 70643,\n" +
                "            \"userErp\": \"xumigen\",\n" +
                "            \"userName\": \"曾云建\"\n" +
                "        }\n" +
                "    }";
        CreateMixScanTaskReq param = JSONObject.parseObject(json, CreateMixScanTaskReq.class);
        int i = 0;
        while (i++ < 100) {

            Object obj = jyWarehouseSendGatewayService.createMixScanTask(param);
            System.out.println("success");
        }
    }
    
    @Test
    public void appendMixScanTaskFlowTest() {
        AppendMixScanTaskFlowReq req = new AppendMixScanTaskFlowReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setTemplateCode("CTT23051900000004");
        req.setTemplateName("接货仓混扫任务01");
        req.setGroupCode("G00000059567");
        List<MixScanTaskDetailDto> sendFLow = new ArrayList<>();
        req.setSendFlowList(sendFLow);
        MixScanTaskDetailDto detailDto = new MixScanTaskDetailDto();
        detailDto.setEndSiteId(23L);
        detailDto.setEndSiteName("目的站点2");
        detailDto.setSendVehicleDetailBizId("SST22070100000344");
        detailDto.setCrossCode("324");
        detailDto.setTabletrolleyCode("rfbrf");
        sendFLow.add(detailDto);
        String pdaJson = "     {\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010F002\",\n" +
                "            \"operateTime\": 1686932865296,\n" +
                "            \"operatorId\": \"51435\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 223094,\n" +
                "            \"siteName\": \"北京马驹桥接货仓\"\n" +
                "        },\n" +
                "        \"groupCode\": \"G00000099001\",\n" +
                "        \"requestId\": \"11243bf50bb84d0c81ed649d162d8cc0\",\n" +
                "        \"sendFlowList\": [\n" +
                "            {\n" +
                "                \"endSiteId\": 64812,\n" +
                "                \"endSiteName\": \"红妍DP嘉峪关测试\",\n" +
                "                \"sendVehicleDetailBizId\": \"NTSD23061600000002\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"templateCode\": \"CTT23061600000002\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 17331,\n" +
                "            \"userErp\": \"wuyoude\",\n" +
                "            \"userName\": \"吴有德\"\n" +
                "        }\n" +
                "    }";

        while(true) {
            try{

                AppendMixScanTaskFlowReq paramDto = JsonHelper.fromJson(pdaJson, AppendMixScanTaskFlowReq.class);

                System.out.println(JsonHelper.toJson(jyWarehouseSendGatewayService.appendMixScanTaskFlow(paramDto)));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Test
    public void deleteMixScanTaskTest() {
        DeleteMixScanTaskReq req = new DeleteMixScanTaskReq();
        req.setTemplateCode("CTT23051900000004");
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        JdCResponse<String> response = jyWarehouseSendGatewayService.deleteMixScanTask(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void removeMixScanTaskFlowTest() {
        RemoveMixScanTaskFlowReq req = new RemoveMixScanTaskFlowReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        req.setEndSiteId(33);
        req.setSendVehicleDetailBizId("SST22070100000344");
        jyWarehouseSendGatewayService.removeMixScanTaskFlow(req);
    }
    
    @Test
    public void mixScanTaskFocusTest() {
        MixScanTaskFocusReq req = new MixScanTaskFocusReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        req.setEndSiteId(33);
        req.setSendVehicleDetailBizId("SST22070100000344");
        req.setFocus(0);
        JdCResponse<Integer> response = jyWarehouseSendGatewayService.mixScanTaskFocus(req);
        System.out.println(JsonHelper.toJson(response));
    }
    
    @Test
    public void getMixScanTaskListPageTest() {
        MixScanTaskListQueryReq req = new MixScanTaskListQueryReq();
        req.setCurrentOperate(SITE_40240);
        req.setUser(USER);
        req.setGroupCode("G00000089001");
        req.setPageNo(1);
        req.setPageSize(10);
        JdCResponse<MixScanTaskQueryRes> mixScanTaskListPage = jyWarehouseSendGatewayService.getMixScanTaskListPage(req);
        System.out.println(JsonHelper.toJson(mixScanTaskListPage));
    }
    
    @Test
    public void getMixScanTaskFlowDetailListTest() {
        MixScanTaskFlowDetailReq req = new MixScanTaskFlowDetailReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        JdCResponse<MixScanTaskFlowDetailRes> l = jyWarehouseSendGatewayService.getMixScanTaskFlowDetailList(req);
        System.out.println(JsonHelper.toJson(l));
    }
    
    @Test
    public void selectMixScanTaskSealDestTest() {
        SelectMixScanTaskSealDestReq req = new SelectMixScanTaskSealDestReq();
        req.setUser(USER);
        req.setCurrentOperate(CURRENT_OPERATE);
        req.setGroupCode("G00000059567");
        req.setTemplateCode("CTT23051900000004");
        JdCResponse<MixScanTaskToSealDestAgg> re = jyWarehouseSendGatewayService.selectMixScanTaskSealDest(req);
        System.out.println(JsonHelper.toJson(re));
    }


    @Test
    public void testVehicleStatusOptions(){
        while(true) {
            try{
                JdCResponse<List<SelectOption>> obj = jyWarehouseSendGatewayService.vehicleStatusOptions();
                System.out.println(JsonHelper.toJson(obj));
                JdCResponse<List<SelectOption>> obj1 = jyWarehouseSendGatewayService.scanTypeOptions();
                System.out.println(JsonHelper.toJson(obj1));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testFetchSendVehicleTaskPage(){
        String paramJson = " {\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"752F001\",\n" +
                "            \"operateTime\": 1686192506459,\n" +
                "            \"operatorId\": \"95156362\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 10,\n" +
                "            \"orgName\": \"华南\",\n" +
                "            \"siteCode\": 554230,\n" +
                "            \"siteName\": \"惠州分拣中心\"\n" +
                "        },\n" +
//                "        \"lineType\": 1,\n" +
                "        \"pageNumber\": 1,\n" +
                "        \"pageSize\": 30,\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 21478780,\n" +
                "            \"userErp\": \"yxwangjun6\",\n" +
                "            \"userName\": \"王俊\"\n" +
                "        },\n" +
                "        \"vehicleStatus\": 0\n" +
                "    }";


        String str = "{\n" +
                "    \"currentOperate\": {\n" +
                "        \"dmsCode\": \"BJ00001\",\n" +
                "        \"operateTime\": 1687672561538,\n" +
                "        \"operatorId\": \"51905\",\n" +
                "        \"operatorTypeCode\": 1,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"华北\",\n" +
                "        \"siteCode\": 223094,\n" +
                "        \"siteName\": \"北京马驹桥接货仓\"\n" +
                "    },\n" +
                "    \"keyword\": \"JD0003420687329-1-1-\",\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 30,\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17907,\n" +
                "        \"userErp\": \"shipeilin\",\n" +
                "        \"userName\": \"石培林\"\n" +
                "    },\n" +
                "    \"vehicleStatus\": 0\n" +
                "}";

        SendVehicleTaskRequest paramDto = JsonHelper.fromJson(str, SendVehicleTaskRequest.class);
        paramDto.setCurrentOperate(SITE_223094);
        paramDto.setUser(USER_wuyoude);
        while(true) {
            try{
                    paramDto.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
                    JdCResponse<SendVehicleTaskResponse> obj0 = jyWarehouseSendGatewayService.fetchSendVehicleTaskPage(paramDto);
                    System.out.println(JsonHelper.toJson(obj0));

//                    paramDto.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
//                    JdCResponse<SendVehicleTaskResponse> obj1 = jyWarehouseSendGatewayService.fetchSendVehicleTaskPage(paramDto);
//                    System.out.println(JsonHelper.toJson(obj1));
//
//                    paramDto.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
//                    JdCResponse<SendVehicleTaskResponse> obj2 = jyWarehouseSendGatewayService.fetchSendVehicleTaskPage(paramDto);
//                    System.out.println(JsonHelper.toJson(obj2));
//
//                    paramDto.setVehicleStatus(JyBizTaskSendStatusEnum.SEALED.getCode());
//                    JdCResponse<SendVehicleTaskResponse> obj3 = jyWarehouseSendGatewayService.fetchSendVehicleTaskPage(paramDto);
//                    System.out.println(JsonHelper.toJson(obj3));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testFetchToSendAndSendingTaskPage(){
        String paramJson = " {\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"752F001\",\n" +
                "            \"operateTime\": 1686192506459,\n" +
                "            \"operatorId\": \"95156362\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 10,\n" +
                "            \"orgName\": \"华南\",\n" +
                "            \"siteCode\": 554230,\n" +
                "            \"siteName\": \"惠州分拣中心\"\n" +
                "        },\n" +
//                "        \"lineType\": 1,\n" +
                "        \"pageNo\": 1,\n" +
                "        \"pageSize\": 30,\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 21478780,\n" +
                "            \"userErp\": \"yxwangjun6\",\n" +
                "            \"userName\": \"王俊\"\n" +
                "        },\n" +
                "        \"vehicleStatus\": 0\n" +
                "    }";

        AppendSendVehicleTaskQueryReq paramDto = JsonHelper.fromJson(paramJson, AppendSendVehicleTaskQueryReq.class);
        paramDto.setCurrentOperate(SITE_223094);
        paramDto.setUser(USER_wuyoude);
        paramDto.setMixScanTaskCode("CTT23060600000010");
        paramDto.setGroupCode(GROUP_CODE);

        String testPdaJson = "    {\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010F002\",\n" +
                "            \"operateTime\": 1689144773539,\n" +
                "            \"operatorId\": \"53316\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 223094,\n" +
                "            \"siteName\": \"北京马驹桥接货仓\"\n" +
                "        },\n" +
                "        \"keyword\": \"\",\n" +
                "        \"pageNumber\": 1,\n" +
                "        \"pageSize\": 15,\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 17331,\n" +
                "            \"userErp\": \"wuyoude\",\n" +
                "            \"userName\": \"吴有德\"\n" +
                "        },\n" +
                "        \"vehicleStatus\": 1\n" +
                "    }";

        AppendSendVehicleTaskQueryReq testPdaReq = JsonHelper.fromJson(testPdaJson, AppendSendVehicleTaskQueryReq.class);

        while(true) {
            try{
//                JdCResponse<AppendSendVehicleTaskQueryRes> obj0 = jyWarehouseSendGatewayService.fetchToSendAndSendingTaskPage(paramDto);
                JdCResponse<AppendSendVehicleTaskQueryRes> obj0 = jyWarehouseSendGatewayService.fetchToSendAndSendingTaskPage(testPdaReq);
                System.out.println(JsonHelper.toJson(obj0));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testGetMixScanTaskDetailList(){
        MixScanTaskQueryReq paramDto = new MixScanTaskQueryReq();
        paramDto.setCurrentOperate(SITE_40240);
        paramDto.setUser(USER_wuyoude);
        paramDto.setTemplateCode("CTT23060600000010");
        paramDto.setGroupCode(GROUP_CODE);
        while(true) {
            try{
                JdCResponse<MixScanTaskDetailRes> obj0 = jyWarehouseSendGatewayService.getMixScanTaskDetailList(paramDto);
                System.out.println(JsonHelper.toJson(obj0));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMixScanTaskComplete(){
        MixScanTaskCompleteReq paramDto = new MixScanTaskCompleteReq();
        paramDto.setCurrentOperate(SITE_223094);
        paramDto.setUser(USER_wuyoude);
        paramDto.setTemplateCode("CTT23062800000004");
        paramDto.setGroupCode("G00000099001");
        while(true) {
            try{
                JdCResponse<Void> obj0 = jyWarehouseSendGatewayService.mixScanTaskComplete(paramDto);
                System.out.println(JsonHelper.toJson(obj0));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void testSendScan(){
        String paramJson =
                "    {" +
                "        \"barCode\": \"JDVA20764083174-1-1-\",\n" +
                "        \"barCodeType\": 1,\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"022F002\",\n" +
                "            \"operateTime\": 1686293197849,\n" +
                "            \"operatorId\": \"95309061\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 1534,\n" +
                "            \"siteName\": \"天津分拣中心\"\n" +
                "        },\n" +
                "        \"forceSubmit\": false,\n" +
                "        \"groupCode\": \"G00000634002\",\n" +
                "        \"sendForWholeBoard\": false,\n" +
                "        \"sendVehicleBizId\": \"NSST23060900001110\",\n" +
                "        \"sendVehicleDetailBizId\": \"NTSD23060900001079\",\n" +
                "        \"taskId\": \"230609300021232\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 21665117,\n" +
                "            \"userErp\": \"fengwanguo\",\n" +
                "            \"userName\": \"冯万国\"\n" +
                "        },\n" +
                "        \"vehicleNumber\": \"\"\n" +
                "    }" ;

        String pdaJson = "     {\n" +
                "        \"barCode\": \"JDVA00272331780-4-15-\",\n" +
                "        \"barCodeType\": 0,\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010F002\",\n" +
                "            \"operateTime\": 1687954471192,\n" +
                "            \"operatorId\": \"52377\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 223094,\n" +
                "            \"siteName\": \"北京马驹桥接货仓\"\n" +
                "        },\n" +
                "        \"groupCode\": \"G00000099001\",\n" +
                "        \"machineCode\": \"mjq-lmj-001\",\n" +
                "        \"mixScanTaskCode\": \"CTT23062700000020\",\n" +
                "        \"operateType\": 101,\n" +
                "        \"sendForWholeBoard\": false,\n" +
                "        \"sendVehicleBizId\": \"SST23062100000032\",\n" +
                "        \"sendVehicleDetailBizId\": \"TW23062100962018-001\",\n" +
                "        \"taskId\": \"230621300000051\",\n" +
                "        \"unfocusedFlowForceSend\": false,\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 17331,\n" +
                "            \"userErp\": \"wuyoude\",\n" +
                "            \"userName\": \"吴有德\"\n" +
                "        }\n" +
                "    }";
        while(true) {
            try{

                SendScanReq paramDto = JsonHelper.fromJson(pdaJson, SendScanReq.class);

                testDmsSendScan();
                JdVerifyResponse<SendScanRes> obj0 = jyWarehouseSendGatewayService.sendScan(paramDto);
                System.out.println(JsonHelper.toJson(obj0));

//                paramDto.setBarCode("JD0003420599479-9-20-");
//                JdVerifyResponse<SendScanRes> obj1 = jyWarehouseSendGatewayService.sendScan(paramDto);
//                System.out.println(JsonHelper.toJson(obj0));
//
//
//                paramDto.setBarCode("JD0003420599479-10-20-");
//                JdVerifyResponse<SendScanRes> obj2 = jyWarehouseSendGatewayService.sendScan(paramDto);
//                System.out.println(JsonHelper.toJson(obj0));
//
//
//                paramDto.setBarCode("JD0003420599479-11-20-");
//                JdVerifyResponse<SendScanRes> obj3 = jyWarehouseSendGatewayService.sendScan(paramDto);
//                System.out.println(JsonHelper.toJson(obj0));
//                List<String> packageCodeList = Arrays.asList("JD0003420509892-1-1-",
//                        "JD0003420509903-1-1-",
//                        "JD0003420509911-1-1-",
//                        "JD0003420509920-1-1-",
//                        "JD0003420509939-1-1-");
//                for(String packageCode : packageCodeList) {
////                    String packageCode = "JD0003420475846-1-1-";
//                    String waybillCode = WaybillUtil.getWaybillCode(packageCode);
//                    SendScanReq paramDto = JsonHelper.fromJson(paramJson, SendScanReq.class);
//                    paramDto.setCurrentOperate(SITE_223094);
//                    paramDto.setUser(USER_wuyoude);
//                    paramDto.setMixScanTaskCode("CTT23060600000010");
//                    paramDto.setGroupCode(GROUP_CODE);
//                    paramDto.setBarCode(packageCode);
//
//                    List<DeviceInfoDto> deviceList = this.getDeviceInfoList();
//                    paramDto.setMachineCode(deviceList.get(0).getMachineCode());
//
//
//                    JdVerifyResponse<SendScanRes> obj0 = jyWarehouseSendGatewayService.sendScan(paramDto);
//                    System.out.println(JsonHelper.toJson(obj0));
//                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private IJySendVehicleService jySendVehicleService;

    public void testDmsSendScan()  {
        String body = "  {\n" +
                "        \"barCode\": \"JDVA00272331780-5-15-\",\n" +
                "        \"barCodeType\": 1,\n" +
                "        \"confirmSendDestId\": 910,\n" +
                "        \"currentOperate\": {\n" +
                "            \"dmsCode\": \"010F002\",\n" +
                "            \"operateTime\": 1687954426618,\n" +
                "            \"operatorId\": \"52376\",\n" +
                "            \"operatorTypeCode\": 1,\n" +
                "            \"orgId\": 6,\n" +
                "            \"orgName\": \"华北\",\n" +
                "            \"siteCode\": 40240,\n" +
                "            \"siteName\": \"北京通州分拣中心\"\n" +
                "        },\n" +
                "        \"forceSubmit\": false,\n" +
                "        \"groupCode\": \"G00000047003\",\n" +
                "        \"sendForWholeBoard\": false,\n" +
                "        \"sendVehicleBizId\": \"SST23062800000039\",\n" +
                "        \"sendVehicleDetailBizId\": \"TW23062800963246-001\",\n" +
                "        \"taskId\": \"230628300000039\",\n" +
                "        \"user\": {\n" +
                "            \"userCode\": 17331,\n" +
                "            \"userErp\": \"wuyoude\",\n" +
                "            \"userName\": \"吴有德\"\n" +
                "        },\n" +
                "        \"vehicleNumber\": \"京AN0723\"\n" +
                "    }";

        SendScanRequest request = JsonHelper.fromJson(body, SendScanRequest.class);
        Object obj = jySendVehicleService.sendScan(request);
        System.out.println(JsonHelper.toJson(obj));
    }


    private List<DeviceInfoDto> getDeviceInfoList() {
        DeviceInfoDto paramDto = new DeviceInfoDto();
        paramDto.setDeviceTypeCode("GANTRY");
        paramDto.setIsEnable(1);
//        paramDto.setSiteCode("40240");
        paramDto.setSiteCode("223094");

        JdCResponse<List<DeviceInfoDto>> res = deviceGatewayService.getDeviceInfoList(paramDto);
        if(res.isSucceed() && CollectionUtils.isNotEmpty(res.getData())) {
            return res.getData();
        }
        return null;
    }



    @Test
    public void testFindByQiWaybillPage(){
        while(true) {
            try{
                BuQiWaybillReq paramDto = new BuQiWaybillReq();
                paramDto.setCurrentOperate(SITE_40240);
                paramDto.setUser(USER_wuyoude);
                paramDto.setMixScanTaskCode("CTT23060600000010");
                paramDto.setGroupCode(GROUP_CODE);
                paramDto.setSendVehicleDetailBizId("TW22081600806720-001");
                paramDto.setPageNo(1);
                paramDto.setPageSize(3);
                Object obj = jyWarehouseSendGatewayService.findByQiWaybillPage(paramDto);
                System.out.println(JsonHelper.toJson(obj));

                paramDto.setPageNo(2);
                Object obj2 = jyWarehouseSendGatewayService.findByQiWaybillPage(paramDto);
                System.out.println(JsonHelper.toJson(obj));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testFindByQiPackagePage(){
        while(true) {
            try{
                BuQiWaybillReq paramDto = new BuQiWaybillReq();
                paramDto.setCurrentOperate(SITE_40240);
                paramDto.setUser(USER_wuyoude);
                paramDto.setMixScanTaskCode("CTT23060600000010");
                paramDto.setGroupCode(GROUP_CODE);
                paramDto.setSendVehicleDetailBizId("TW22081600806720-001d");
                paramDto.setSendVehicleBizId("SST22081600000007");
                paramDto.setWaybillCode("JD0003420475846");
                paramDto.setPageNo(1);
                paramDto.setPageSize(10);
                Object obj = jyWarehouseSendGatewayService.findByQiPackagePage(paramDto);
                System.out.println(JsonHelper.toJson(obj));

                paramDto.setPageNo(2);
                Object obj2 = jyWarehouseSendGatewayService.findByQiPackagePage(paramDto);
                System.out.println(JsonHelper.toJson(obj));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Test
    public void testCheckBeforeSealCar() {
        while(true) {
            try{
                SealCarCheckDtoReq paramDto = new SealCarCheckDtoReq();
                paramDto.setCurrentOperate(SITE_40240);
                paramDto.setUser(USER_wuyoude);
                paramDto.setMixScanTaskCode("CTT23060600000010");
                paramDto.setGroupCode(GROUP_CODE);

                Object obj = jyWarehouseSendGatewayService.checkBeforeSealCar(paramDto);
                System.out.println(JsonHelper.toJson(obj));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testBuQiCancelSendScan(){
        while(true) {
            BuQiCancelSendScanReq req = new BuQiCancelSendScanReq();
            req.setCurrentOperate(SITE_223094);
            req.setUser(USER_wuyoude);
            req.setGroupCode(GROUP_CODE);
            req.setMixScanTaskCode("CTT23060600000010");
            req.setSendVehicleBizId("SST22081600000007");
            req.setPost(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());

            jyWarehouseSendGatewayService.buQiCancelSendScan(req);
            List<String> packageCodeList = Arrays.asList("JD0003420509892-1-1-",
                    "JD0003420509903-1-1-",
                    "JD0003420509911-1-1-",
                    "JD0003420509920-1-1-",
                    "JD0003420509939-1-1-");
            req.setPackList(packageCodeList);
            jyWarehouseSendGatewayService.buQiCancelSendScan(req);
        }
    }

}
