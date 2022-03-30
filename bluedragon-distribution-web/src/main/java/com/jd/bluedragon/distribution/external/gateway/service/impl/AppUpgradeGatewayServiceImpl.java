package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.request.AppUpgradeRequest;
import com.jd.bluedragon.distribution.api.response.AppUpgradeResponse;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.external.gateway.service.AppUpgradeGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName AppUpgradeGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/14 13:58
 **/
public class AppUpgradeGatewayServiceImpl implements AppUpgradeGatewayService {

    @Autowired
    private UserService userService;

    @Override
    public JdCResponse<AppUpgradeResponse> appUpgradeCheck(AppUpgradeRequest request) {
        JdResult<AppUpgradeResponse> result = userService.checkAppVersion(request);
        return new JdCResponse<>(result.getCode(), result.getMessage(), result.getData());
    }
}
