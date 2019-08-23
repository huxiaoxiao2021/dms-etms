package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;

/**
 * @author : xumigen
 * @date : 2019/7/27
 */
public interface AllianceBusiGatewayService {
    JdVerifyResponse checkAllianceMoney(String waybillCode);
}
