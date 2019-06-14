package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.ql.dms.common.domain.JdResponse;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface InspectionGatewayService {

    JdResponse getStorageCode(String packageBarOrWaybillCode, Integer siteCode);
}
