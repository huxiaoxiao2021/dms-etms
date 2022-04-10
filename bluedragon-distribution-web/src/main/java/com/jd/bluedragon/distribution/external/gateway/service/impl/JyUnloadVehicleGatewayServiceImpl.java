package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.dto.select.StringSelectOption;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName JyUnloadVehicleGatewayServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/1 16:40
 **/
public class JyUnloadVehicleGatewayServiceImpl implements JyUnloadVehicleGatewayService {

    @Autowired
    private IJyUnloadVehicleService unloadVehicleService;

    @Override
    public JdCResponse<Boolean> createNoTaskUnloadTask(UnloadNoTaskRequest request) {
        JdCResponse<Boolean> jdCResponse = new JdCResponse<Boolean>();
        JyBizTaskUnloadDto dto = new JyBizTaskUnloadDto();
        // 无任务模式
        dto.setManualCreatedFlag(Constants.CONSTANT_NUMBER_ONE);
        dto.setVehicleNumber(request.getVehicleNumber());
        dto.setOperateSiteId(request.getOperateSiteId());
        dto.setOperateSiteName(request.getOperateSiteName());
        jdCResponse.toSucceed();
        jdCResponse.setData(unloadVehicleService.createUnloadTask(dto));
        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.fetchUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        return retJdCResponse(unloadVehicleService.fetchUnloadTask(request));
    }

    @Override
    public JdCResponse<List<SelectOption>> vehicleStatusOptions() {
        List<SelectOption> optionList = new ArrayList<>();
        for (JyBizTaskUnloadStatusEnum statusEnum : JyBizTaskUnloadStatusEnum.UNLOAD_STATUS_OPTIONS) {
            SelectOption option = new SelectOption(statusEnum.getCode(), statusEnum.getName(), statusEnum.getCode());
            optionList.add(option);
        }
        JdCResponse<List<SelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    public JdCResponse<List<StringSelectOption>> productTypeOptions() {
        List<StringSelectOption> optionList = new ArrayList<>();
        for (UnloadProductTypeEnum _enum : UnloadProductTypeEnum.values()) {
            StringSelectOption keyValue = new StringSelectOption(_enum.getCode(), _enum.getName(), _enum.getDisplayOrder());
            optionList.add(keyValue);
        }

        Collections.sort(optionList, new StringSelectOption.OrderComparator());

        JdCResponse<List<StringSelectOption>> response = new JdCResponse<>();
        response.toSucceed();
        response.setData(optionList);
        return response;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<Integer> unloadScan(UnloadScanRequest request) {
        JdVerifyResponse<Integer> response = new JdVerifyResponse<>();
        response.toSuccess();

        // 扫描前校验拦截结果
        if (!checkBarInterceptResult(response, request)) {
            return response;
        }

        InvokeResult<Integer> invokeResult = unloadVehicleService.unloadScan(request);
        return new JdVerifyResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    /**
     * 调用验货拦截链
     * @param response
     * @param request
     * @return
     */
    private boolean checkBarInterceptResult(JdVerifyResponse<Integer> response, UnloadScanRequest request) {
        // 非强制提交，校验拦截
        if (!request.getForceSubmit()) {
            // TODO 卸车扫描调用拦截链
        }

        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadScanDetail> unloadDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.unloadDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadGoodsDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request) {
        return retJdCResponse(unloadVehicleService.unloadGoodsDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.toScanAggByProduct",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.toScanAggByProduct(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.toScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {
        return retJdCResponse(unloadVehicleService.toScanBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.interceptBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.interceptBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.moreScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.moreScanBarCodeDetail(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.unloadPreviewDashboard",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request) {
        return retJdCResponse(unloadVehicleService.unloadPreviewDashboard(request));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnloadVehicleGatewayService.submitUnloadCompletion",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdCResponse<Boolean> submitUnloadCompletion(UnloadCompleteRequest request) {
        return retJdCResponse(unloadVehicleService.submitUnloadCompletion(request));
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
