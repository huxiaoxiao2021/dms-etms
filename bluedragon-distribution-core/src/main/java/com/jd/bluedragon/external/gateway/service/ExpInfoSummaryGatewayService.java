package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.three.no.ExpInfoSummaryDto;

/**
 * 三无信息相关
 * 发布到物流网关 安装调用
 * @author jinjingcheng
 * @date 2020/5/18
 */
public interface ExpInfoSummaryGatewayService {
    JdCResponse<Boolean> addEpfExpInfo(ExpInfoSummaryDto expInfoSummaryDto);
}
