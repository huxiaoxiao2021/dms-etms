package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.send.request.SinglePackageSendRequest;

/**
 * @author : xumigen
 * @date : 2019/6/27
 */
public interface DmsDeliveryGatewayService {
    /**
     * 新发货：按箱号发货校验
     *
     * @param request
     * @return
     */
    JdVerifyResponse packageSendVerifyForBox(DeliveryVerifyRequest request);

    JdVerifyResponse newPackageSendGoods(SinglePackageSendRequest request);
}
