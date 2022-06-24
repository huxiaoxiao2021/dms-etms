package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.TransTypeEnum;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.seal.request.*;
import com.jd.bluedragon.common.dto.seal.response.SealCodeResp;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.external.gateway.service.JySealCarGatewayService;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@UnifiedExceptionProcess
public class JySealCarGatewayServiceImpl implements JySealCarGatewayService {
    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;
    @Autowired
    JySealVehicleService jySealVehicleService;

    @Override
    public JdCResponse<SealCodeResp> listSealCodeByBizId(SealCodeReq sealCodeReq) {
        return retJdCResponse(jySealVehicleService.listSealCodeByBizId(sealCodeReq));
    }

    @Override
    public JdCResponse<SealVehicleInfoResp> getSealVehicleInfo(SealVehicleInfoReq sealVehicleInfoReq) {
        return retJdCResponse(jySealVehicleService.getSealVehicleInfo(sealVehicleInfoReq));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealCarGatewayServiceImpl.getTransportResourceByTransCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<TransportInfoDto> getTransportResourceByTransCode(TransportReq transportReq) {
        JdCResponse<TransportInfoDto> jdCResponse = new JdCResponse<>();
        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setSiteCode(transportReq.getCurrentOperate().getSiteCode());
        param.setSiteName(transportReq.getCurrentOperate().getSiteName());
        param.setUserCode(transportReq.getUser().getUserCode());
        param.setUserName(transportReq.getUser().getUserName());
        param.setTransportCode(transportReq.getTransportCode());
        RouteTypeResponse routeTypeResponse = newSealVehicleResource.getTransportCode(param);
        if (routeTypeResponse.getCode().equals(JdResponse.CODE_OK)) {
            jdCResponse.toSucceed(routeTypeResponse.getMessage());
        } else if (routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_UNSEAL_CAR_OUT_CHECK) || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_CHECK) || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_ERROR)) {
            jdCResponse.toConfirm(routeTypeResponse.getMessage());
        } else {
            jdCResponse.toFail(routeTypeResponse.getMessage());
        }
        TransportInfoDto transportInfoDto = BeanUtils.copy(routeTypeResponse, TransportInfoDto.class);
        jdCResponse.setData(transportInfoDto);
        return jdCResponse;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealCarGatewayServiceImpl.checkTransportCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse checkTransportCode(CheckTransportCodeReq checkTransportCodeReq) {
        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setTransportCode(checkTransportCodeReq.getTransportCode());
        param.setTransWorkItemCode(checkTransportCodeReq.getTransWorkItemCode());
        TransWorkItemResponse workItemResponse = newSealVehicleResource.checkTransportCode(param);
        return new JdCResponse(workItemResponse.getCode(), workItemResponse.getMessage());
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealCarGatewayServiceImpl.getVehicleNumberByWorkItemCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<TransportResp> getVehicleNumberByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {

        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setTransWorkItemCode(getVehicleNumberReq.getTransWorkItemCode());
        param.setVehicleNumber(getVehicleNumberReq.getVehicleNumber());
        param.setUserErp(getVehicleNumberReq.getUser().getUserErp());
        param.setDmsCode(getVehicleNumberReq.getCurrentOperate().getDmsCode());
        TransWorkItemResponse transWorkItemResponse = newSealVehicleResource.getVehicleNumberOrItemCodeByParam(param);

        TransportResp transportResp = new TransportResp();
        transportResp.setRouteLineCode(transWorkItemResponse.getRouteLineCode());
        transportResp.setRouteLineName(transWorkItemResponse.getRouteLineName());
        transportResp.setSendCode(transWorkItemResponse.getSendCode());
        transportResp.setTransType(transWorkItemResponse.getTransType());
        if (ObjectHelper.isNotNull(transportResp.getTransType())
                && ObjectHelper.isNotNull(TransTypeEnum.getEnum(transportResp.getTransType()))){
            transportResp.setTransTypeName(TransTypeEnum.getEnum(transportResp.getTransType()).getName());
        }
        transportResp.setTransWorkItemCode(transWorkItemResponse.getTransWorkItemCode());
        transportResp.setVehicleNumber(transWorkItemResponse.getVehicleNumber());

        return new JdCResponse(transWorkItemResponse.getCode(), transWorkItemResponse.getMessage(), transportResp);
    }

    @Override
    public JdCResponse<TransportResp> getTransWorkItemByWorkItemCode(GetVehicleNumberReq getVehicleNumberReq) {
        return retJdCResponse(jySealVehicleService.getTransWorkItemByWorkItemCode(getVehicleNumberReq));
    }

    @Override
    public JdCResponse sealVehicle(SealVehicleReq sealVehicleReq) {
        return retJdCResponse(jySealVehicleService.sealVehicle(sealVehicleReq));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JySealCarGatewayServiceImpl.validateTranCodeAndSendCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse validateTranCodeAndSendCode(ValidSendCodeReq validSendCodeReq) {
        SealCarPreRequest sealCarPreRequest = BeanUtils.copy(validSendCodeReq,SealCarPreRequest.class);
        if (ObjectHelper.isEmpty(sealCarPreRequest.getSealCarType())){
            sealCarPreRequest.setSealCarType(SealCarTypeEnum.SEAL_BY_TASK.getType());
        }
        if (ObjectHelper.isEmpty(sealCarPreRequest.getSealCarSource())){
            sealCarPreRequest.setSealCarSource(SealCarSourceEnum.COMMON_SEAL_CAR.getCode());
        }
        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.newCheckTranCodeAndBatchCode(sealCarPreRequest);
        return new JdCResponse(newSealVehicleResponse.getCode(),newSealVehicleResponse.getMessage());
    }

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }
}
