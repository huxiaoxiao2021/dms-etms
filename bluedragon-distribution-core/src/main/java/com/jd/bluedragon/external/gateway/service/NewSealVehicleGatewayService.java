package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;

/**
 * 封车发布物流网关
 * @author : xumigen
 * @date : 2019/7/8
 */
public interface NewSealVehicleGatewayService {
    JdCResponse cancelSeal(CancelSealRequest gatewayRequest);
}
