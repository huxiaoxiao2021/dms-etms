package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;

/**
 * 加盟商
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/27
 */
public interface AllianceBusiGatewayService {
    JdVerifyResponse checkAllianceMoney(String waybillCode);
}
