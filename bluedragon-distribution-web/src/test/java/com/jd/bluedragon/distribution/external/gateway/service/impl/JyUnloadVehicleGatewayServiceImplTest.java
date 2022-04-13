package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadVehicleTaskResponse;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}
