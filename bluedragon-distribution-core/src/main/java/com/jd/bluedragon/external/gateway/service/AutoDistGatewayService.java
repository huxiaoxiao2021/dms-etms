package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;

/**
 * 包裹补码功能，实现扫描包裹号或者运单号将信息推送给WCS
 */
public interface AutoDistGatewayService {

    /**
     * 上海亚一的pda专用的包裹补码功能，实现扫描包裹号或者运单号将信息推送给WCS
     * <doc>
     * 上海亚一的pda调用的接口,如果不输入siteCode的值的话，默认传0
     * </doc>
     */
    JdCResponse<Boolean> supplementSiteCode(String barCode, Integer siteCode, Integer createSiteCode, String operatorErp);
}