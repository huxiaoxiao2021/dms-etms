package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleToScanPackageDetailRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleToScanPackageDetailResponse;
import com.jd.bluedragon.external.gateway.service.JySendVehicleGatewayService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/11/11 18:24
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:distribution-web-context.xml")
public class JySendVehicleGatewayServiceImplTest {

    @Autowired
    private JySendVehicleGatewayService jySendVehicleGatewayService;

    @Test
    public void getSendVehicleToScanAggByProductTest(){

        SendVehicleCommonRequest request = new SendVehicleCommonRequest();
        request.setSendVehicleBizId("SST22071400000047");

        JdCResponse<List<SendVehicleProductTypeAgg>> sendVehicleToScanAggByProduct = jySendVehicleGatewayService.getSendVehicleToScanAggByProduct(request);
        System.out.println(JSON.toJSONString(sendVehicleToScanAggByProduct));
    }



    /**
     * 按产品类型获取待扫包裹列表
     * @param request
     * @return
     *
     * JdCResponse<List<SendVehicleProductTypeAgg>> getSendVehicleToScanAggByProduct(SendVehicleCommonRequest request);
     *
     * JdCResponse<SendVehicleToScanPackageDetailResponse> getSendVehicleToScanPackageDetail(SendVehicleToScanPackageDetailRequest request);
     */

}
