package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.bluedragon.distribution.external.gateway.dto.request.BatchSingleSendCheckRequest;
import com.jd.bluedragon.distribution.external.gateway.dto.request.BatchSingleSendRequest;
import com.jd.bluedragon.distribution.external.gateway.dto.response.BatchSingleSendCheckVO;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * 批量一车一单发货JSF接口
 * @author jiaowenqiang
 * @date 2019/6/11
 */
public interface BatchSingleSendGatewayService {


    /**
     * 批量一车一单发货参数校验接口
     */
    JdResponse<BatchSingleSendCheckVO> batchSingleSendCheck(BatchSingleSendCheckRequest request);

    /**
     * 批量一车一单发货
     */
    JdResponse<SendResult> batchSingleSend(BatchSingleSendRequest request);
}
