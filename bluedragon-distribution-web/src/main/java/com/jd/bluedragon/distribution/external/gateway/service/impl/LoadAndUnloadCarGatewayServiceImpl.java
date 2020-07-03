package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.unloadCar.HelperDto;
import com.jd.bluedragon.common.dto.unloadCar.TaskHelpersReq;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskDto;
import com.jd.bluedragon.common.dto.unloadCar.UnloadCarTaskReq;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDetailScanResult;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanRequest;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarScanResult;
import com.jd.bluedragon.distribution.rest.loadAndUnload.LoadAndUnloadVehicleResource;
import com.jd.bluedragon.external.gateway.service.LoadAndUnloadCarGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    @Override
    public JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTask(UnloadCarTaskReq unloadCarTaskReq) {
        JdCResponse<List<UnloadCarTaskDto>> jdCResponse = new JdCResponse<>();
        if (unloadCarTaskReq == null) {
            jdCResponse.toError("参数不能为空");
            return jdCResponse;
        }
        InvokeResult<List<UnloadCarTaskDto>> result = loadAndUnloadVehicleResource.getUnloadCarTask(unloadCarTaskReq);
        if(!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)){
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<UnloadCarTaskDto>> updateUnloadCarTaskStatus(UnloadCarTaskReq unloadCarTaskReq) {
        JdCResponse<List<UnloadCarTaskDto>> jdCResponse = new JdCResponse<>();
        if (unloadCarTaskReq == null) {
            jdCResponse.toError("参数错误");
            return jdCResponse;
        }
        InvokeResult<List<UnloadCarTaskDto>> result = loadAndUnloadVehicleResource.updateUnloadCarTaskStatus(unloadCarTaskReq);
        if (!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<HelperDto>> getUnloadCarTaskHelpers(String taskCode) {
        JdCResponse<List<HelperDto>> jdCResponse = new JdCResponse<>();
        if (taskCode == null) {
            jdCResponse.toError("任务编码不能为空");
        }
        InvokeResult<List<HelperDto>> result = loadAndUnloadVehicleResource.getUnloadCarTaskHelpers(taskCode);
        if (!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<HelperDto>> updateUnloadCarTaskHelpers(TaskHelpersReq taskHelpersReq) {
        JdCResponse<List<HelperDto>> jdCResponse = new JdCResponse<>();

        InvokeResult<List<HelperDto>> result = loadAndUnloadVehicleResource.updateUnloadCarTaskHelpers(taskHelpersReq);
        if (!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

    @Override
    public JdCResponse<List<UnloadCarTaskDto>> getUnloadCarTaskScan(TaskHelpersReq taskHelpersReq) {
        JdCResponse<List<UnloadCarTaskDto>> jdCResponse = new JdCResponse<>();

        InvokeResult<List<UnloadCarTaskDto>> result = loadAndUnloadVehicleResource.getUnloadCarTaskScan(taskHelpersReq);
        if (!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
            jdCResponse.toError(result.getMessage());
            return jdCResponse;
        }
        jdCResponse.toSucceed(result.getMessage());
        jdCResponse.setData(result.getData());
        return jdCResponse;
    }

}
