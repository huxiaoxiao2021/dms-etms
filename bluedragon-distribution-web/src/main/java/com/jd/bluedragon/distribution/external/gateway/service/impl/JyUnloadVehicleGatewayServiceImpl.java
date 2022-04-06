package com.jd.bluedragon.distribution.external.gateway.service.impl;

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
import com.jd.bluedragon.external.gateway.service.JyUnloadVehicleGatewayService;
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
    public JdCResponse<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        InvokeResult<UnloadVehicleTaskResponse> invokeResult = unloadVehicleService.fetchUnloadTask(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
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
    public JdVerifyResponse<Integer> unloadScan(UnloadScanRequest request) {
        JdVerifyResponse<Integer> response = new JdVerifyResponse<>();
        response.toSuccess();

        // 扫描前校验拦截结果
        if (!checkBarInterceptResult(response, request)) {
            return response;
        }

        InvokeResult<Integer> invokeResult = unloadVehicleService.unloadScan(request);
        return new JdVerifyResponse<>(invokeResult.getCode(), invokeResult.getMessage());
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
    public JdCResponse<UnloadScanDetail> unloadDetail(UnloadCommonRequest request) {
        InvokeResult<UnloadScanDetail> invokeResult = unloadVehicleService.unloadDetail(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    public JdCResponse<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request) {
        InvokeResult<List<UnloadScanAggByProductType>> invokeResult = unloadVehicleService.unloadGoodsDetail(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    public JdCResponse<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request) {
        InvokeResult<List<ProductTypeAgg>> invokeResult = unloadVehicleService.toScanAggByProduct(request);
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @Override
    public JdCResponse<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {

        // scaned_flag null or 0 是待扫

        return null;
    }

    @Override
    public JdCResponse<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request) {

        // "intercept_flag 1:拦截
        return null;
    }

    @Override
    public JdCResponse<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request) {

        // more_scan_flag 1:多扫
        // local_site_flag 1:本场地
        return null;
    }

    @Override
    public JdCResponse<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request) {
        return null;
    }

    @Override
    public JdCResponse<Boolean> submitUnloadComplete(UnloadCompleteRequest request) {
        return null;
    }
}
