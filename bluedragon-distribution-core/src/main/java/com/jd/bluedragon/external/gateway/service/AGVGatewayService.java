package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.agv.AGVPDARequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;



public interface AGVGatewayService {

    JdCResponse<Boolean> sortByAGV(AGVPDARequest request);

}
