package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendModeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehiclePhotoEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JySendLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.external.gateway.service.JySendVehicleGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName JySendVehicleGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/5/26 18:38
 **/
public class JySendVehicleGatewayServiceImpl implements JySendVehicleGatewayService {

    @Autowired
    private IJySendVehicleService jySendVehicleService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    public JdCResponse<List<SelectOption>> sendModeOptions() {
        List<SelectOption> options = new ArrayList<>();
        for (SendModeEnum _enum : SendModeEnum.values()) {
            options.add(new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc()));
        }

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(options);
        return response;
    }

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
    public JdCResponse<List<SelectOption>> lineTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JySendLineTypeEnum _enum : JySendLineTypeEnum.values()) {
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.fetchSendVehicleTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {
        return retJdCResponse(jySendVehicleService.fetchSendVehicleTask(request));
    }

    @Override
    public JdCResponse<List<SelectOption>> sendPhotoOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (SendVehiclePhotoEnum _enum : SendVehiclePhotoEnum.values()) {
            SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc());
            optionList.add(option);
        }

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.uploadPhoto",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Boolean> uploadPhoto(SendPhotoRequest request) {
        return retJdCResponse(jySendVehicleService.uploadPhoto(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.sendVehicleInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request) {
        return retJdCResponse(jySendVehicleService.sendVehicleInfo(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.sendDestDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
        return retJdCResponse(jySendVehicleService.sendDestDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.loadProgress",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
        return retJdCResponse(jySendVehicleService.loadProgress(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.checkSendVehicleNormalStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request) {
        return retJdCResponse(jySendVehicleService.checkSendVehicleNormalStatus(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.interceptedBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackRequest request) {
        return retJdCResponse(jySendVehicleService.interceptedBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.forceSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackRequest request) {
        return retJdCResponse(jySendVehicleService.forceSendBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.abnormalSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackRequest request) {
        return retJdCResponse(jySendVehicleService.abnormalSendBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.selectSealDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ToSealDestAgg> selectSealDest(SelectSealDestRequest request) {
        return retJdCResponse(jySendVehicleService.selectSealDest(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.sendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {
        return jySendVehicleService.sendScan(request);
    }

    @Override
    public JdCResponse checkMainLineSendTask(CheckSendCodeRequest request) {
        return retJdCResponse(jySendVehicleService.checkMainLineSendTask(request));
    }

    @Override
    public JdCResponse<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request) {
        return retJdCResponse(jySendVehicleService.sendTaskDetail(request));
    }
}
