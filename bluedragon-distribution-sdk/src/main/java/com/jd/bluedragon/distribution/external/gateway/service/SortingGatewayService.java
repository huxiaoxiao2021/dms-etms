package com.jd.bluedragon.distribution.external.gateway.service;

import com.jd.bluedragon.distribution.external.gateway.dto.request.SortingCancelRequest;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * 分拣相关物流网关调用
 * @author : xumigen
 * @date : 2019/6/12
 */
public interface SortingGatewayService {

    JdResponse sortingCancel(SortingCancelRequest request);
}
