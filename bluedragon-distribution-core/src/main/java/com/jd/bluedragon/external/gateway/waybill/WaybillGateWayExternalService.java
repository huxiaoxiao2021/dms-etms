package com.jd.bluedragon.external.gateway.waybill;

import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;

/**
 * 运单相关 发布物流网关
 * 目前的调用方有：经济网
 * @author : xumigen
 * @date : 2020/1/2
 */
public interface WaybillGateWayExternalService {

    /**
     * 回传箱号与运单明细
     * @param request
     * @param pin 京东PIN码 物流网关会透传过来
     * @return
     */
    GateWayBaseResponse<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request,String pin);
}
