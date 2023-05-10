package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SelectSealDestRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleProgressRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.external.gateway.service.JyWarehouseSendGatewayService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class JyWarehouseSendGatewayServiceImpl implements JyWarehouseSendGatewayService {

    @Override
    public JdCResponse<List<SelectOption>> vehicleStatusOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JySendVehicleStatusEnum _enum : JySendVehicleStatusEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getOrder());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
            optionList.add(option);
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTaskPage(SendVehicleTaskRequest request) {
        return null;
    }

    @Override
    public JdCResponse<SendVehicleTaskResponse> fetchToSendAndSendingTaskPage(SendVehicleTaskRequest request) {
        return null;
    }

    @Override
    public JdCResponse<SendVehicleProgress> loadStatistics(SendVehicleProgressRequest request) {
        return null;
    }

    @Override
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> getMixScanTaskDefaultName(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> createMixScanTaskAndAddFlow(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> appendMixScanTaskFlow(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse deleteMixScanTask(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> removeMixScanTaskFlow(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> mixScanTaskComplete(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskRes> mixScanTaskFocus(MixScanTaskReq mixScanTaskReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskQueryRes> getMixScanTaskListPage(MixScanTaskQueryReq mixScanTaskQueryReq) {
        return null;
    }

    @Override
    public JdCResponse<MixScanTaskFlowRes> getMixScanTaskFlowListPage(MixScanTaskQueryReq mixScanTaskQueryReq) {
        return null;
    }

    @Override
    public JdCResponse<ToSealDestAgg> selectMixScanTaskSealDest(SelectSealDestRequest request) {
        return null;
    }

    @Override
    public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return null;
    }
}
