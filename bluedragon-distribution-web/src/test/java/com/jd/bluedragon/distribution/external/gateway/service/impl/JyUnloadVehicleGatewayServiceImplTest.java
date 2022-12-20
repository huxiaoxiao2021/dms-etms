package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ToScanDetailByProductType;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanAggByProductType;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadVehicleTaskResponse;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @ClassName JyUnloadVehicleGatewayServiceImplTest
 * @Description
 * @Author wyh
 * @Date 2022/4/11 17:34
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JyUnloadVehicleGatewayServiceImplTest {

    @Autowired
    private JyUnloadVehicleGatewayService unloadVehicleGatewayService;

    @Test
    public void fetchUnloadTaskTest() {
        String body = "{\n" +
                "    \"endSiteCode\": 910,\n" +
                "    \"vehicleStatus\": 4,\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 30\n" +
                "}\n";
        UnloadVehicleTaskRequest request = JsonHelper.fromJson(body, UnloadVehicleTaskRequest.class);
        JdCResponse<UnloadVehicleTaskResponse> jdCResponse = unloadVehicleGatewayService.fetchUnloadTask(request);
        System.out.println(JsonHelper.toJson(jdCResponse));
    }

    @Test
    public void unloadDetailTest() {

        String firstProgressBody = "{\n" +
                "    \"bizId\": \"BJ002\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1649928235019,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"北京马驹桥分拣中心6\"\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"京B457473\"\n" +
                "}";

        UnloadCommonRequest firstProgress = JsonHelper.fromJson(firstProgressBody, UnloadCommonRequest.class);

        unloadVehicleGatewayService.unloadDetail(firstProgress);

        String scanBody = "{\n" +
                "    \"barCode\": \"JDVF00001742088-2-5-\\n\",\n" +
                "    \"bizId\": \"BJ002\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1649928259572,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"北京马驹桥分拣中心6\"\n" +
                "    },\n" +
                "    \"forceSubmit\": false,\n" +
                "    \"groupCode\": \"G00000010006\",\n" +
                "    \"taskId\": \"220414200000013\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17331,\n" +
                "        \"userErp\": \"wuyoude\",\n" +
                "        \"userName\": \"吴有德\"\n" +
                "    }\n" +
                "}";

        UnloadScanRequest scanRequest = JsonHelper.fromJson(scanBody, UnloadScanRequest.class);

        unloadVehicleGatewayService.unloadScan(scanRequest);

        String secondProgressBody = "{\n" +
                "    \"bizId\": \"BJ002\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1649928513959,\n" +
                "        \"orgId\": 10,\n" +
                "        \"orgName\": \"广州分公司\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"北京马驹桥分拣中心6\"\n" +
                "    },\n" +
                "    \"sealCarCode\": \"\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 17849,\n" +
                "        \"userErp\": \"liuduo8\",\n" +
                "        \"userName\": \"刘铎\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"京B457473\"\n" +
                "}";
        UnloadCommonRequest secondProgress = JsonHelper.fromJson(secondProgressBody, UnloadCommonRequest.class);

        unloadVehicleGatewayService.unloadDetail(secondProgress);
    }

    @Test
    public void previewDataTest() {
        String json = "{\n" +
                "    \"bizId\": \"XCZJ22041800000002\",\n" +
                "    \"currentOperate\": {\n" +
                "        \"operateTime\": 1650349015029,\n" +
                "        \"orgId\": 6,\n" +
                "        \"orgName\": \"总公司\",\n" +
                "        \"siteCode\": 910,\n" +
                "        \"siteName\": \"北京马驹桥分拣中心6\"\n" +
                "    },\n" +
                "    \"pageNumber\": 1,\n" +
                "    \"pageSize\": 20,\n" +
                "    \"sealCarCode\": \"\",\n" +
                "    \"user\": {\n" +
                "        \"userCode\": 18225,\n" +
                "        \"userErp\": \"xumigen\",\n" +
                "        \"userName\": \"徐迷根\"\n" +
                "    },\n" +
                "    \"vehicleNumber\": \"京NEFQNEG\"\n" +
                "}";

        unloadVehicleGatewayService.unloadPreviewDashboard(JsonHelper.fromJson(json, UnloadCommonRequest.class));

    }


    @Test
    public void unloadGoodsDetailTest(){
        UnloadGoodsRequest request = new UnloadGoodsRequest();
        request.setBizId("SC00001");
        JdCResponse<List<UnloadScanAggByProductType>> listJdCResponse = unloadVehicleGatewayService.unloadGoodsDetail(request);
        System.out.println(JSON.toJSONString(listJdCResponse));
    }

    @Test
    public void toScanAggByProductTest(){
        UnloadCommonRequest request = new UnloadCommonRequest();
        request.setBizId("SC00001");
        JdCResponse<List<ProductTypeAgg>> listJdCResponse = unloadVehicleGatewayService.toScanAggByProduct(request);
        System.out.println(JSON.toJSONString(listJdCResponse));
    }

    @Test
    public void toScanBarCodeDetailTest(){


//        if (StringUtils.isBlank(request.getBizId())
//                || StringUtils.isBlank(request.getProductType())
//                || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())
//                || !NumberHelper.gt0(request.getPageNumber())
//                || !NumberHelper.gt0(request.getPageSize())) {
//            result.parameterError();
//            return result;


        //       {"pageNumber":1,"bizId":"SC22090700020957","productType":"FRESH","pageSize":10,"currentOperate":{"siteCode":10186}}
//        }
        UnloadProductTypeRequest request = new UnloadProductTypeRequest();
        request.setBizId("SC22090700020957");
        request.setProductType("FRESH");
        request.setPageNumber(1);
        request.setPageSize(20);

        CurrentOperate currentOperate = new CurrentOperate();
        currentOperate.setSiteCode(10186);
        request.setCurrentOperate(currentOperate);
        System.out.println(JSON.toJSONString(request));
        JdCResponse<ToScanDetailByProductType> response = unloadVehicleGatewayService.toScanBarCodeDetail(request);

        System.out.println(JSON.toJSONString(response));

    }
}
