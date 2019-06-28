package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface InspectionGatewayService {

    JdCResponse getStorageCode(String packageBarOrWaybillCode, Integer siteCode);
}
