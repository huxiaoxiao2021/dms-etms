package com.jd.bluedragon.external.gateway.waybill;

import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 运单相关 发布物流网关
 * @author : xumigen
 * @date : 2020/1/2
 */
public class WaybillGateWayExternalServiceImpl implements WaybillGateWayExternalService {
    private final Logger logger = LoggerFactory.getLogger(WaybillGateWayExternalServiceImpl.class);

    @Override
    public GateWayBaseResponse<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request,String pin) {
        logger.info("同步运单与箱号信息waybillCode[{}]boxCode[{}]",request.getWaybillCode(),request.getBoxCode());
        return null;
    }
}
