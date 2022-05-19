package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;

import java.util.List;

public class JyNoTaskSendGatewayServiceImpl implements JyNoTaskSendGatewayService {
    @Override
    public JdCResponse<List<VehicleSpecResp>> getVehicleType() {
        return null;
    }

    @Override
    public JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<List<VehicleTaskResp>> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return null;
    }

    @Override
    public JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return null;
    }

    @Override
    public JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return null;
    }

    @Override
    public JdCResponse cancelSendTask(CancelSendTaskReq cancelSendTaskReq) {
        return null;
    }
}
