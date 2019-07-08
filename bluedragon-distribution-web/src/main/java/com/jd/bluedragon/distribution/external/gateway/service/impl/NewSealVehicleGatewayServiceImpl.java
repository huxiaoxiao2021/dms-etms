package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.*;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.external.gateway.service.NewSealVehicleGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/7/8
 */
public class NewSealVehicleGatewayServiceImpl implements NewSealVehicleGatewayService {

    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;

    @Override
    public JdCResponse cancelSeal(CancelSealRequest gatewayRequest){
        cancelSealRequest request = new cancelSealRequest();
        request.setBatchCode(gatewayRequest.getBatchCode());
        request.setOperateTime(gatewayRequest.getOperateTime());
        request.setOperateType(gatewayRequest.getOperateType());
        request.setOperateUserCode(gatewayRequest.getOperateUserCode());
        NewSealVehicleResponse vehicleResponse = newSealVehicleResource.cancelSeal(request);
        JdCResponse jdCResponse = new JdCResponse();
        if(Objects.equals(vehicleResponse.getCode(), JdResponse.CODE_OK)){
            jdCResponse.toSucceed(vehicleResponse.getMessage());
            return jdCResponse;
        }
        jdCResponse.toError(vehicleResponse.getMessage());
        return jdCResponse;
    }


    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     */
    @Override
    public JdCResponse<SealCarTaskInfoDto> getTaskInfo(SearCarTaskInfoRequest request) {

        JdCResponse<SealCarTaskInfoDto> response = new JdCResponse<>();
        SealCarTaskInfoDto sealCarTaskInfoDto = new SealCarTaskInfoDto();

        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setTransWorkItemCode(request.getTransWorkItemCode());
        param.setVehicleNumber(request.getVehicleNumber());
        param.setUserErp(request.getErp());
        param.setDmsCode(request.getDmsCode());

        TransWorkItemResponse transWorkItemResponse = newSealVehicleResource.getVehicleNumberOrItemCodeByParam(param);


        sealCarTaskInfoDto.setRouteLineCode(transWorkItemResponse.getRouteLineCode());
        sealCarTaskInfoDto.setRouteLineName(transWorkItemResponse.getRouteLineName());
        sealCarTaskInfoDto.setSendCode(transWorkItemResponse.getSendCode());
        sealCarTaskInfoDto.setTransType(transWorkItemResponse.getTransType());
        sealCarTaskInfoDto.setTransWorkItemCode(transWorkItemResponse.getTransWorkItemCode());
        sealCarTaskInfoDto.setVehicleNumber(transWorkItemResponse.getVehicleNumber());

        response.setCode(transWorkItemResponse.getCode());
        response.setData(sealCarTaskInfoDto);
        response.setMessage(transWorkItemResponse.getMessage());

        return response;
    }

    /**
     * 校验运力编码信息,返回运输类型
     */
    @Override
    public JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request) {
        JdCResponse<Integer> jdCResponse = new JdCResponse<>();
        NewSealVehicleRequest param = new NewSealVehicleRequest();

        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setUserCode(request.getUser().getUserCode());
        param.setUserName(request.getUser().getUserName());
        param.setTransportCode(request.getTransportCode());

        RouteTypeResponse routeTypeResponse = newSealVehicleResource.getTransportCode(param);

        if (routeTypeResponse.getCode() == 30001 || routeTypeResponse.getCode() == 30002 || routeTypeResponse.getCode() == 30003) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
            jdCResponse.setMessage(routeTypeResponse.getMessage());
        } else {
            jdCResponse.setCode(routeTypeResponse.getCode());
            jdCResponse.setMessage(routeTypeResponse.getMessage());
            jdCResponse.setData(routeTypeResponse.getTransWay());
        }

        return jdCResponse;
    }

    /**
     * 校验任务简码与运力编号是否匹配
     */
    @Override
    public JdCResponse checkTransportCode(CheckTransportCodeRequest request) {
        JdCResponse<Integer> jdCResponse = new JdCResponse<>();
        NewSealVehicleRequest param = new NewSealVehicleRequest();

        param.setTransportCode(request.getTransportCode());
        param.setTransWorkItemCode(request.getTransWorkItemCode());

        TransWorkItemResponse workItemResponse = newSealVehicleResource.checkTransportCode(param);

        jdCResponse.setCode(workItemResponse.getCode());
        jdCResponse.setMessage(workItemResponse.getMessage());

        return jdCResponse;
    }

    /**
     * 检查运力编码和批次号目的地是否一致
     */
    @Override
    public JdCResponse checkTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType) {
        JdCResponse jdCResponse = new JdCResponse<>();

        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.newCheckTranCodeAndBatchCode(
                transportCode, batchCode, sealCarType);

        if (newSealVehicleResponse.getCode() >= 30000 && newSealVehicleResponse.getCode() <= 40000) {
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
            jdCResponse.setMessage(newSealVehicleResponse.getMessage());
            return jdCResponse;
        }

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }

    /**
     * 封车
     */
    @Override
    public JdCResponse sear(SearCarRequest searCarRequest) {
        JdCResponse jdCResponse=new JdCResponse();
        NewSealVehicleRequest newSealVehicleRequest = new NewSealVehicleRequest();
        List<SearCarDto> list = searCarRequest.getSearCarDtoList();
        newSealVehicleRequest.setData(convert(list));
        NewSealVehicleResponse newSealVehicleResponse=newSealVehicleResource.seal(newSealVehicleRequest);

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }


    //参数转化
    private List<SealCarDto> convert(List<SearCarDto> list) {
        List<SealCarDto> sealCarDtos = new ArrayList<>();
        SealCarDto param;
        for (SearCarDto sc : list) {
            param = new SealCarDto();
            param.setSealCarType(sc.getSealCarType());
            param.setItemSimpleCode(sc.getItemSimpleCode());
            param.setTransportCode(sc.getTransportCode());
            param.setBatchCodes(sc.getBatchCodes());
            param.setVehicleNumber(sc.getVehicleNumber());
            param.setSealCodes(sc.getSealCodes());
            param.setVolume(sc.getVolume());
            param.setWeight(sc.getWeight());
            param.setRouteLineCode(sc.getRouteLineCode());
            param.setSealCarTime(sc.getSealCarTime());
            param.setSealSiteId(sc.getSealSiteId());
            param.setSealSiteName(sc.getSealSiteName());
            param.setSealUserCode(sc.getSealUserCode());
            param.setDesealUserName(sc.getSealUserName());
            sealCarDtos.add(param);
        }
        return sealCarDtos;
    }
}
