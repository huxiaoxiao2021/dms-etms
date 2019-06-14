package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.bluedragon.distribution.external.gateway.dto.request.RecycleMaterialRequest;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * 循环物资实操装态更新 发布物流网关
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface RecycleMaterialGatewayService {

    JdResponse<String> updateStatus(RecycleMaterialRequest request);
}
