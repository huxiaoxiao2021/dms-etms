package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.request.AppUpgradeRequest;
import com.jd.bluedragon.distribution.api.response.AppUpgradeResponse;

/**
 * @ClassName AppUpgradeGatewayService
 * @Description
 * @Author wyh
 * @Date 2022/3/14 13:56
 **/
public interface AppUpgradeGatewayService {

    /**
     * APP升级版本校验
     * @param request
     * @return
     */
    JdCResponse<AppUpgradeResponse> appUpgradeCheck(AppUpgradeRequest request);
}
