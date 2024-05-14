package com.jd.bluedragon.distribution.external.gateway.service.impl;


import com.jd.bluedragon.common.dto.agv.AGVPDARequest;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.agv.AGVService;
import com.jd.bluedragon.distribution.sdk.common.domain.InvokeResult;
import com.jd.bluedragon.external.gateway.service.AGVGatewayService;
import org.springframework.beans.factory.annotation.Autowired;


public class AGVGatewayServiceImpl implements AGVGatewayService {

    @Autowired
    private AGVService agvService;

    @Override
    public JdCResponse<Boolean> sortByAGV(AGVPDARequest request) {
        final InvokeResult<Boolean> invokeResult = agvService.sortByAGV(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
