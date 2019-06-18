package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.bluedragon.common.dto.response.JdCResponse;
import com.jd.bluedragon.distribution.external.gateway.dto.request.SortingCancelRequest;

/**
 * 分拣相关物流网关调用
 * @author : xumigen
 * @date : 2019/6/12
 */
public interface SortingGatewayService {

    JdCResponse sortingCancel(SortingCancelRequest request);
}
