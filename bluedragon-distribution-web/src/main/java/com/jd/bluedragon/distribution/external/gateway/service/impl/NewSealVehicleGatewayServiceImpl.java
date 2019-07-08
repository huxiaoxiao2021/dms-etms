package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.external.gateway.service.NewSealVehicleGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/7/8
 */
public class NewSealVehicleGatewayServiceImpl implements NewSealVehicleGatewayService {

    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;

    public JdCResponse cancelSeal(CancelSealRequest gatewayRequest){
        cancelSealRequest request = new cancelSealRequest();
        request.setBatchCode(gatewayRequest.getBatchCode());
        request.setOperateTime(gatewayRequest.getOperateTime());
        request.setOperateType(gatewayRequest.getOperateType());
        request.setOperateUserCode(gatewayRequest.getOperateUserCode());
        NewSealVehicleResponse vehicleResponse = newSealVehicleResource.cancelSeal(request);
        JdCResponse jdCResponse = new JdCResponse();
        if(Objects.equals(vehicleResponse.getCode(), JdResponse.CODE_OK)){
            jdCResponse.toSucceed(vehicleResponse.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(vehicleResponse.getMessage());
        return jdCResponse;
    }
}
