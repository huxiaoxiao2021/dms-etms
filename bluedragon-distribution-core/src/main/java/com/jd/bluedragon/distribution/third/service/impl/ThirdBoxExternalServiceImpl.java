package com.jd.bluedragon.distribution.third.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.thirdBox.request.WaybillSyncRequest;
import com.jd.bluedragon.distribution.thirdBox.service.ThirdBoxExternalService;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.waybill.WaybillGateWayExternalService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2020/6/17
 */
@Service("thirdBoxExternalService")
public class ThirdBoxExternalServiceImpl implements ThirdBoxExternalService {

    @Autowired
    @Qualifier("waybillGateWayExternalService")
    private WaybillGateWayExternalService waybillGateWayExternalService;

    @Override
    @JProfiler(jKey = "DMSWEB.ThirdBoxExternalServiceImpl.syncWaybillCodeAndBoxCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP})
    public Response<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request, String pin) {
        Response<Void> response = new Response<>();
        response.toSucceed("操作成功");
        WaybillSyncRequest paramSyncRequest = new  WaybillSyncRequest();
        com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest gateWayParam =
                new com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest();
        gateWayParam.setTenantCode(paramSyncRequest.getTenantCode());
        gateWayParam.setStartSiteCode(paramSyncRequest.getStartSiteCode());
        gateWayParam.setEndSiteCode(paramSyncRequest.getEndSiteCode());
        gateWayParam.setOperatorId(paramSyncRequest.getOperatorId());
        gateWayParam.setOperatorName(paramSyncRequest.getOperatorName());
        gateWayParam.setOperatorUnitName(paramSyncRequest.getOperatorUnitName());
        gateWayParam.setOperatorTime(paramSyncRequest.getOperatorTime());
        gateWayParam.setBoxCode(paramSyncRequest.getBoxCode());
        gateWayParam.setWaybillCode(paramSyncRequest.getWaybillCode());
        gateWayParam.setPackageCode(paramSyncRequest.getPackageCode());
        gateWayParam.setOperationType(paramSyncRequest.getOperationType());
        GateWayBaseResponse<Void>  gateWayBaseResponse = waybillGateWayExternalService.syncWaybillCodeAndBoxCode(gateWayParam,pin);
        if(!Objects.equals(gateWayBaseResponse.getResultCode(),GateWayBaseResponse.CODE_SUCCESS)){
            response.toError(gateWayBaseResponse.getMessage());
            return response;
        }
        response.toSucceed(gateWayBaseResponse.getMessage());
        return response;
    }
}
