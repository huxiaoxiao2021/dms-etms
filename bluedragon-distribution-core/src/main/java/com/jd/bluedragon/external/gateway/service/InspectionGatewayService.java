package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface InspectionGatewayService {

    JdCResponse<InspectionResultDto> getStorageCode(String packageBarOrWaybillCode, Integer siteCode);
}
