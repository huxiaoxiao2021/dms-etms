package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.DeliveryVerifyRequest;
import com.jd.ql.dms.common.domain.JdVerifyResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName DeliveryVerifyService
 * @date 2019/6/13
 */
public interface DeliveryVerifyService {

    /**
     * 按箱发货前条码校验
     *
     * @return
     */
    JdVerifyResponse beforeSendByBoxCodeVerify(DeliveryVerifyRequest request);

}
