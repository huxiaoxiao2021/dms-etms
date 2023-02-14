package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.weight.request.WeightVolumeRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @author zs
 * @date 2023/2/14 15:11
 * @description 企配仓称重量方接口
 */
public interface JyWeightVolumeGatewayService {
    /**
     * @param request 请求req
     * @return {@link Boolean}
     * @description 企配仓称重量方
     * @author zs
     * @date 2023/2/14 15:11
     **/
    InvokeResult<Boolean> weightVolumeCheckAndDeal(WeightVolumeRequest request);
}
