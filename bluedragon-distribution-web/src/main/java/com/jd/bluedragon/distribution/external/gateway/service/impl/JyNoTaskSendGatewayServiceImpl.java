package com.jd.bluedragon.distribution.external.gateway.service.impl;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyNoTaskSendService;
import com.jd.bluedragon.external.gateway.service.JyNoTaskSendGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Slf4j
@UnifiedExceptionProcess
public class JyNoTaskSendGatewayServiceImpl implements JyNoTaskSendGatewayService {

    @Autowired
    JyNoTaskSendService jyNoTaskSendService;
    @Autowired
    private BaseParamValidateService baseParamValidateService;


    @Override
    public JdCResponse<List<VehicleSpecResp>> listVehicleType() {
        return retJdCResponse(jyNoTaskSendService.listVehicleType());
    }

    @Override
    public JdCResponse<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.createVehicleTask(createVehicleTaskReq));
    }

    @Override
    public JdCResponse<CreateAviationTaskResp> createAviationTask(CreateAviationTaskReq request) {
        final String methodDesc = "JyNoTaskSendGatewayServiceImpl.createAviationTask:";
        try{
            //基本参数校验
            baseParamValidateService.checkUserAndSiteAndGroupAndPost(
                    request.getUser(), request.getCurrentOperate(), request.getGroupCode(), request.getPost());
            if(log.isInfoEnabled()) {
                log.info("{}请求信息={}", methodDesc, JsonHelper.toJson(request));
            }
            return retJdCResponse(jyNoTaskSendService.createAviationTask(request));
        }catch (JyBizException ex) {
            log.error("{}自定义异常捕获，请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage());
            return new JdCResponse<>(JdCResponse.CODE_FAIL, ex.getMessage(), null);//400+自定义异常
        }catch (Exception ex) {
            log.error("{}请求信息={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
            return new JdCResponse<>(JdCResponse.CODE_ERROR, "航空自建任务创建服务异常", null);//500+非自定义异常
        }
    }

    @Override
    public JdCResponse deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.deleteVehicleTask(deleteVehicleTaskReq));
    }

    @Override
    public JdCResponse<DeleteVehicleTaskCheckResponse> checkBeforeDeleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.checkBeforeDeleteVehicleTask(deleteVehicleTaskReq));
    }
    @Override
    public JdCResponse<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.listVehicleTask(vehicleTaskReq));
    }

    @Override
    public JdCResponse<VehicleTaskResp> listVehicleTaskSupportTransfer(
        TransferVehicleTaskReq transferVehicleTaskReq) {
        return retJdCResponse(jyNoTaskSendService.listVehicleTaskSupportTransfer(transferVehicleTaskReq));
    }

    @Override
    public JdCResponse bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return retJdCResponse(jyNoTaskSendService.bindVehicleDetailTask(bindVehicleDetailTaskReq));
    }

    @Override
    public JdCResponse transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return retJdCResponse(jyNoTaskSendService.transferSendTask(transferSendTaskReq));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.cancelSendTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        try {
            return retJdCResponse(jyNoTaskSendService.cancelSendTask(request));
        } catch (JyBizException exception) {
            if (ObjectHelper.isNotNull(exception.getCode())){
                return new JdCResponse(exception.getCode(), exception.getMessage());
            }
            return new JdCResponse(CODE_ERROR, exception.getMessage());
        }
    }

    @Override
    public JdCResponse<GetSendRouterInfoResq> getSendRouterInfoByScanCode(GetSendRouterInfoReq getSendRouterInfoReq) {
        return null;
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }


}
