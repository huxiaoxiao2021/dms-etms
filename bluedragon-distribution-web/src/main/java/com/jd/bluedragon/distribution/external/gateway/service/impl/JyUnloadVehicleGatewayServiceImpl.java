package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.select.StringSelectOption;
import com.jd.bluedragon.distribution.jy.enums.ProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.IUnloadVehicleService;
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
    private IUnloadVehicleService unloadVehicleService;

    @Override
    public JdCResponse<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        return null;
    }

    @Override
    public JdCResponse<List<StringSelectOption>> productTypeOptions() {
        List<StringSelectOption> optionList = new ArrayList<>();
        for (ProductTypeEnum _enum : ProductTypeEnum.values()) {
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
    public JdVerifyResponse<Long> unloadScan(UnloadScanRequest request) {
        // 返回本次扫描的包裹数
        // 调用验货拦截服务
        // 统计设备的扫描数量
        // 一个包裹、箱号只能扫描一次
        return null;
    }

    @Override
    public JdCResponse<UnloadScanDetail> unloadDetail(UnloadVehicleRequest request) {
        return null;
    }

    @Override
    public JdCResponse<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadVehicleRequest request) {
        return null;
    }

    @Override
    public JdCResponse<List<ProductTypeAgg>> toScanAggByProduct(UnloadVehicleRequest request) {
        return null;
    }

    @Override
    public JdCResponse<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {

        // scaned_flag null or 0 是待扫

        return null;
    }

    @Override
    public JdCResponse<InterceptScanBarCode> interceptBarCodeDetail(UnloadVehicleRequest request) {

        // "intercept_flag 1:拦截
        return null;
    }

    @Override
    public JdCResponse<MoreScanBarCode> moreScanBarCodeDetail(UnloadVehicleRequest request) {

        // more_scan_flag 1:多扫
        // local_site_flag 1:本场地
        return null;
    }

    @Override
    public JdCResponse<UnloadPreviewData> unloadPreviewDashboard(UnloadVehicleRequest request) {
        return null;
    }

    @Override
    public JdCResponse<Boolean> submitUnloadComplete(UnloadCompleteRequest request) {
        return null;
    }
}
