package com.jd.bluedragon.distribution.capability.send.service;

import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.api.request.PackageSendRequest;
import com.jd.bluedragon.distribution.api.request.SendRequest;
import com.jd.bluedragon.distribution.send.domain.SendResult;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/9/7
 * @Description:
 */
public interface ISendOfCapabilityAreaService {


    /**
     * 发货
     * @param request
     * @return
     */
    JdVerifyResponse<SendResult> doSend(SendRequest request);

}
