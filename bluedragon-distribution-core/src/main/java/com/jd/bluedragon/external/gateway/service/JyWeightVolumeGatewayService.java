package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.weight.request.WeightVolumeRequest;

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
    JdVerifyResponse<Boolean> weightVolumeCheckAndDeal(WeightVolumeRequest request);
}
