package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.whitelist.request.WhiteList;


/**
 * 白名单相关
 * 发布到物流网关 由安卓调用
 * @author : biyubo
 * @date : 2020/4/1
 */
public interface WhiteListGatewayService {
    JdCResponse<Boolean> inspectionAuthority(WhiteList whiteListrequest);
}
