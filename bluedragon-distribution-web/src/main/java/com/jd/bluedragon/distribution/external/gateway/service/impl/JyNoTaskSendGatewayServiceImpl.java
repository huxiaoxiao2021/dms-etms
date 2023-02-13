package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.send.JyNoTaskSendService;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Slf4j
@UnifiedExceptionProcess
public class JyNoTaskSendGatewayServiceImpl implements JyNoTaskSendGatewayService {

    @Autowired
    JyNoTaskSendService jyNoTaskSendService;

    @Override
    public JdCResponse<List<VehicleSpecResp>> listVehicleType() {
        return retJdCResponse(jyNoTaskSendService.listVehicleType());
    }

    @Override
    public JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.createVehicleTask(createVehicleTaskReq));
    }

    @Override
    public JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.deleteVehicleTask(deleteVehicleTaskReq));
    }

    @Override
    public JdCResponse<DeleteVehicleTaskCheckResponse> checkBeforeDeleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.checkBeforeDeleteVehicleTask(deleteVehicleTaskReq));
    }
    @Override
    public JdCResponse<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.listVehicleTask(vehicleTaskReq));
    }

    @Override
    public JdCResponse<VehicleTaskResp> listVehicleTaskSupportTransfer(
        TransferVehicleTaskReq transferVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.listVehicleTaskSupportTransfer(transferVehicleTaskReq));
    }

    @Override
    public JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return retJdCResponse(jyNoTaskSendService.bindVehicleDetailTask(bindVehicleDetailTaskReq));
    }

    @Override
    public JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return retJdCResponse(jyNoTaskSendService.transferSendTask(transferSendTaskReq));
    }

    @Override
    public JdCResponse<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        return retJdCResponse(jyNoTaskSendService.cancelSendTask(request));
    }

    @Override
    public JdCResponse<GetSendRouterInfoResq> getSendRouterInfoByScanCode(GetSendRouterInfoReq getSendRouterInfoReq) {
        return null;
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


}
