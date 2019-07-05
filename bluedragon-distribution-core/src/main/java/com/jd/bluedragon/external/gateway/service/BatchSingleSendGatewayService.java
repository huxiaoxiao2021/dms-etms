package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendCheckRequest;
import com.jd.bluedragon.common.dto.send.request.BatchSingleSendRequest;
import com.jd.bluedragon.common.dto.send.response.BatchSingleSendCheckDto;

/**
 * BatchSingleSendGatewayService
 * 批量一车一单发货JSF接口
 * @author jiaowenqiang
 * @date 2019/6/11
 */
public interface BatchSingleSendGatewayService {


    /**
     * 批量一车一单发货参数校验接口
     */
    JdCResponse<BatchSingleSendCheckDto> batchSingleSendCheck(BatchSingleSendCheckRequest request);

    /**
     * 批量一车一单发货
     */
    JdCResponse batchSingleSend(BatchSingleSendRequest request);
}
