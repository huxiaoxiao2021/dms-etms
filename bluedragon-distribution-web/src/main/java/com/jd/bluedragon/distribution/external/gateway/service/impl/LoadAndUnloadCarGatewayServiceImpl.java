package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.unloadCar.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
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
    public JdCResponse<UnloadCarScanResult> getUnloadScan(UnloadCarScanRequest unloadCarScanRequest) {
        JdCResponse<UnloadCarScanResult> jdCResponse = new JdCResponse<UnloadCarScanResult>();
        if (unloadCarScanRequest == null) {
            jdCResponse.toError("参数不能为空");
            return jdCResponse;
        }
        InvokeResult<UnloadCarScanResult> invokeResult = loadAndUnloadVehicleResource.getUnloadScan(unloadCarScanRequest);

        jdCResponse.setCode(convertCode(invokeResult.getCode()));
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
    public JdVerifyResponse<UnloadCarScanResult> packageCodeScan(UnloadCarScanRequest unloadCarScanRequest) {
        JdVerifyResponse<UnloadCarScanResult> response = new JdVerifyResponse<>();

        InvokeResult<UnloadCarScanResult> invokeResult = loadAndUnloadVehicleResource.packageCodeScan(unloadCarScanRequest);
        // 包裹号转大宗标识
        Integer transfer = unloadCarScanRequest.getTransfer();
        if (GoodsLoadScanConstants.PACKAGE_TRANSFER_TO_WAYBILL.equals(transfer)) {
            // 如果没有发生异常
            if (InvokeResult.RESULT_SUCCESS_CODE == invokeResult.getCode()) {
                Integer packageSize = invokeResult.getData().getPackageSize();
                response.setCode(JdCResponse.CODE_CONFIRM);
                JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox();
                msgBox.setMsg("大宗订单按单操作！此单共计" + packageSize + "件，请确认包裹集齐！");
                msgBox.setType(MsgBoxTypeEnum.CONFIRM);
                response.addBox(msgBox);
                return response;
            }
        }
        response.setCode(convertCode(invokeResult.getCode()));
        response.setMessage(invokeResult.getMessage());
        response.setData(invokeResult.getData());

        return response;
    }

    @Override
    public JdCResponse<UnloadCarScanResult> waybillScan(UnloadCarScanRequest unloadCarScanRequest) {
        JdCResponse<UnloadCarScanResult> response = new JdCResponse<>();
        InvokeResult<UnloadCarScanResult> invokeResult = loadAndUnloadVehicleResource.waybillScan(unloadCarScanRequest);
        response.setCode(convertCode(invokeResult.getCode()));
        response.setMessage(invokeResult.getMessage());
        response.setData(invokeResult.getData());
        return response;
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

    @Override
    public JdCResponse<Void> startUnloadTask(UnloadCarTaskReq unloadCarTaskReq) {
        JdCResponse<Void> jdcResponse = new JdCResponse<>();
        if (unloadCarTaskReq == null) {
            jdcResponse.toError("参数错误");
            return jdcResponse;
        }
        InvokeResult<Void> result = loadAndUnloadVehicleResource.startUnloadTask(unloadCarTaskReq);
        if (!Objects.equals(result.getCode(), InvokeResult.RESULT_SUCCESS_CODE)) {
            jdcResponse.toError(result.getMessage());
            return jdcResponse;
        }
        jdcResponse.toSucceed(result.getMessage());
        jdcResponse.setData(result.getData());
        return jdcResponse;
    }

    @Override
    public JdCResponse<UnloadScanDetailDto> unloadScan(UnloadCarScanRequest req) {
        JdCResponse<UnloadScanDetailDto> jdCResponse = new JdCResponse<UnloadScanDetailDto>();
        if (req == null) {
            jdCResponse.toError("参数不能为空");
            return jdCResponse;
        }
        InvokeResult<UnloadScanDetailDto> invokeResult = loadAndUnloadVehicleResource.unloadScan(req);

        jdCResponse.setCode(convertCode(invokeResult.getCode()));
        jdCResponse.setMessage(invokeResult.getMessage());
        jdCResponse.setData(invokeResult.getData());

        return jdCResponse;
    }

    @Override
    public JdVerifyResponse<UnloadScanDetailDto> packageCodeScanNew(UnloadCarScanRequest req) {
        return null;
    }

    private int convertCode(int invokeResultCode) {
        int code;
        if (InvokeResult.RESULT_SUCCESS_CODE == invokeResultCode) {
            code = JdCResponse.CODE_SUCCESS;
        } else if (InvokeResult.RESULT_PARAMETER_ERROR_CODE == invokeResultCode) {
            code = JdCResponse.CODE_FAIL;
        } else if (InvokeResult.SERVER_ERROR_CODE == invokeResultCode) {
            code = JdCResponse.CODE_ERROR;
        } else if (InvokeResult.RESULT_MULTI_ERROR == invokeResultCode) {
            code = JdCResponse.CODE_PARTIAL_SUCCESS;
        } else {
            code = invokeResultCode;
        }
        return code;
    }

}
