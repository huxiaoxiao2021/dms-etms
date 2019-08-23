package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.common.dto.send.request.DeliveryVerifyRequest;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;

/**
 * @author lixin39
 * @ClassName DeliveryVerifyService
 * @date 2019/6/13
 */
public interface DeliveryVerifyService {

    /**
     * 按箱发货前条码校验
     *
     * @return
     */
    JdVerifyResponse packageSendVerifyForBoxCode(DeliveryVerifyRequest request);

}