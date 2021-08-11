package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendRequest;
import com.jd.bluedragon.distribution.api.request.ClientRequest;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/8/11
 * @Description:
 */
public interface ClientLogGatewayService {

    /**
     * 批量一车一单发货
     */
    JdCResponse clientLog(ClientRequest request);
}
