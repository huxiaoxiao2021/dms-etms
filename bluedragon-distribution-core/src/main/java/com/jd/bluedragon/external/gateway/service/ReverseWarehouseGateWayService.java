package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.reverse.request.ReverseWarehouseReq;

/**
 *
 * 逆向倒仓 发布物流网关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/9/21
 */
public interface ReverseWarehouseGateWayService {

    JdCResponse<Void> returnWarehouseCheck(ReverseWarehouseReq reverseWarehouseReq);
}
