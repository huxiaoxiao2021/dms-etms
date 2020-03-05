package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.abnormal.request.AbnormalOrdRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;

/**
 * 异常处理
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/9/10
 */
public interface AbnormalOrderGatewayService {

    JdCResponse<Void> pushAbnormalOrder(AbnormalOrdRequest abnormalOrdRequest);

    JdCResponse<Void> queryAbnormalorder(String orderId,Integer type);
}
