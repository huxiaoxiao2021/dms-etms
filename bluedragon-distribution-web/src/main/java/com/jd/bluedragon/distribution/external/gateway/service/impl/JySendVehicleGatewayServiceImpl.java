package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendModeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehiclePhotoEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.send.request.SendBatchReq;
import com.jd.bluedragon.common.dto.send.response.SendBatchResp;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JySendLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.service.send.IJySendVehicleService;
import com.jd.bluedragon.external.gateway.service.JySendVehicleGatewayService;
import com.jd.bluedragon.utils.converter.ResultConverter;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.enums.TenantEnum;
import com.jdl.sorting.tech.tenant.core.context.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * @ClassName JySendVehicleGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/5/26 18:38
 **/
public class JySendVehicleGatewayServiceImpl implements JySendVehicleGatewayService {

    @Autowired
    @Qualifier("jySendVehicleService")
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

    public JdCResponse<List<SelectOption>> scanTypeOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        String tenantCode = TenantContext.getTenantCode();
        //冷链租户返回按件、按单、按板，非冷链返回按件、按单
        if(StringUtils.isNotBlank(tenantCode) && TenantEnum.TENANT_COLD_MEDICINE.getCode().equals(tenantCode)){
            for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.getOneAndWaybillAndBoardEnum()) {
                SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
                optionList.add(option);
            }
        }else{
            for (SendVehicleScanTypeEnum _enum : Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,SendVehicleScanTypeEnum.SCAN_WAYBILL,
                    SendVehicleScanTypeEnum.SCAN_TABLE_TROLLEY)) {
                SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
                optionList.add(option);
            }
        }

        Collections.sort(optionList, new SelectOption.OrderComparator());

        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<List<SelectOption>> scanTypeOptionsNew() {
        List<SelectOption> optionList = new ArrayList<>();
        String tenantCode = TenantContext.getTenantCode();
        //冷链租户返回按件、按单、按板，非冷链返回按件、按单
        if(StringUtils.isNotBlank(tenantCode) && TenantEnum.TENANT_COLD_MEDICINE.getCode().equals(tenantCode)){
            for (SendVehicleScanTypeEnum _enum : SendVehicleScanTypeEnum.getOneAndWaybillAndBoardEnum()) {
                SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
                optionList.add(option);
            }
        }else{
            for (SendVehicleScanTypeEnum _enum : Arrays.asList(SendVehicleScanTypeEnum.SCAN_ONE,SendVehicleScanTypeEnum.SCAN_WAYBILL,
                    SendVehicleScanTypeEnum.SCAN_TABLE_TROLLEY)) {
                SelectOption option = new SelectOption(_enum.getCode(), _enum.getName(), _enum.getDesc(), _enum.getCode());
                optionList.add(option);
            }
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JySendVehicleGatewayService.fetchWaitingVehicleDistributionList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Pager<WaitingVehicleDistribution>> fetchWaitingVehicleDistributionList(WaitingVehicleDistributionRequest request) {
        return retJdCResponse(jySendVehicleService.fetchWaitingVehicleDistributionList(request));
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

    @Override
    public JdCResponse<SendBatchResp> querySendBatch(SendBatchReq request) {
        return retJdCResponse(jySendVehicleService.listSendBatchByTaskDetail(request));
    }

    @Override
    public JdCResponse<List<SendVehicleProductTypeAgg>> getSendVehicleToScanAggByProduct(SendVehicleCommonRequest request) {
        return retJdCResponse(jySendVehicleService.sendVehicleToScanAggByProduct(request));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySendVehicleGatewayServiceImpl.getSendVehicleToScanPackageDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SendVehicleToScanPackageDetailResponse> getSendVehicleToScanPackageDetail(SendVehicleToScanPackageDetailRequest request) {
        return retJdCResponse(jySendVehicleService.sendVehicleToScanPackageDetail(request));
    }

    @Override
    public JdCResponse<SendVehicleProductTypeAgg> getProductToScanInfo(SendAbnormalRequest request) {
        return retJdCResponse(jySendVehicleService.getProductToScanInfo(request));
    }

    /**
     * 根据发货任务获取特殊产品类型数量
     * @param request 请求参数
     * @return 待扫列表统计
     * @author fanggang7
     * @time 2023-07-26 10:00:32 周三
     */
    @Override
    public JdCResponse<SendVehicleToScanTipsDto> getSpecialProductTypeToScanList(SendVehicleToScanTipsRequest request) {
        return ResultConverter.convertResultToJdcResponse(jySendVehicleService.getSpecialProductTypeToScanList(request));
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySendVehicleGatewayServiceImpl.callByWorkItem", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> callByWorkItem(CallNumberRequest request) {
        return retJdCResponse(jySendVehicleService.callByWorkItem(request));
    }


    @Override
    public JdCResponse<String> remindTransJob(RemindTransJobRequest request) {
        return retJdCResponse(jySendVehicleService.remindTransJob(request));
    }
}
