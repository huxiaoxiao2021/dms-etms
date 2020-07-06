package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarDetailScanResult;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanRequest;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarScanResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.rest.loadAndUnload.LoadAndUnloadVehicleResource;
import com.jd.bluedragon.external.gateway.service.LoadAndUnloadCarGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 装卸车物流网关接口实现
 *
 * @author: hujiping
 * @date: 2020/6/24 17:18
 */
@Service("loadAndUnloadCarGatewayService")
public class LoadAndUnloadCarGatewayServiceImpl implements LoadAndUnloadCarGatewayService {

    @Autowired
    private LoadAndUnloadVehicleResource loadAndUnloadVehicleResource;

    @Override
    public JdCResponse<UnloadCarScanResult> getUnloadCar(String sealCarCode) {
        JdCResponse<UnloadCarScanResult> jdCResponse = new JdCResponse<UnloadCarScanResult>();

        InvokeResult<UnloadCarScanResult> invokeResult = loadAndUnloadVehicleResource.getUnloadCar(sealCarCode);

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    @Override
    public JdCResponse<UnloadCarScanResult> barCodeScan(UnloadCarScanRequest unloadCarScanRequest) {
        JdCResponse<UnloadCarScanResult> jdCResponse = new JdCResponse<UnloadCarScanResult>();

        InvokeResult<UnloadCarScanResult> invokeResult = loadAndUnloadVehicleResource.barCodeScan(unloadCarScanRequest);

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    @Override
    public JdCResponse<List<UnloadCarDetailScanResult>> getUnloadCarDetail(String sealCarCode) {
        JdCResponse<List<UnloadCarDetailScanResult>> jdCResponse = new JdCResponse<List<UnloadCarDetailScanResult>>();

        InvokeResult<List<UnloadCarDetailScanResult>> invokeResult = loadAndUnloadVehicleResource.getUnloadCarDetail(sealCarCode);

        jdCResponse.setCode(invokeResult.getCode());
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

}
