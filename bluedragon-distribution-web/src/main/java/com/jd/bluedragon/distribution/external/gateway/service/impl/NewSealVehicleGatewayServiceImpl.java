package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.request.CapacityInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.CheckTransportCodeRequest;
import com.jd.bluedragon.common.dto.blockcar.request.PreSealMeasureInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarDto;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarPreRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarRequest;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarTaskInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.response.PreSealVehicleMeasureDto;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.request.SealVehicleVolumeVerifyRequest;
import com.jd.bluedragon.distribution.api.request.cancelSealRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.SealVehicleVolumeVerifyResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleMeasureInfo;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.distribution.rest.seal.PreSealVehicleResource;
import com.jd.bluedragon.external.gateway.service.NewSealVehicleGatewayService;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    @Qualifier("preSealVehicleResource")
    private PreSealVehicleResource preSealVehicleResource;

    @Override
    @BusinessLog(sourceSys = 1,bizType = 11011,operateType = 1101102)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.cancelSeal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse cancelSeal(CancelSealRequest gatewayRequest) {
        cancelSealRequest request = new cancelSealRequest();
        request.setBatchCode(gatewayRequest.getBatchCode());
        request.setOperateTime(gatewayRequest.getOperateTime());
        request.setOperateType(gatewayRequest.getOperateType());
        request.setOperateUserCode(gatewayRequest.getOperateUserCode());
        NewSealVehicleResponse vehicleResponse = newSealVehicleResource.cancelSeal(request);
        JdCResponse jdCResponse = new JdCResponse();
        if (Objects.equals(vehicleResponse.getCode(), JdResponse.CODE_OK)) {
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
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getTaskInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SealCarTaskInfoDto> getTaskInfo(SealCarTaskInfoRequest request) {

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
    @Deprecated
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getAndCheckTransportCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Integer> getAndCheckTransportCode(CapacityInfoRequest request) {
        JdCResponse<Integer> jdCResponse = new JdCResponse<>();
        NewSealVehicleRequest param = new NewSealVehicleRequest();

        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setUserCode(request.getUser().getUserCode());
        param.setUserName(request.getUser().getUserName());
        param.setTransportCode(request.getTransportCode());

        RouteTypeResponse routeTypeResponse = newSealVehicleResource.getTransportCode(param);

        if (routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_CHECK)
                || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_ERROR)) {
            jdCResponse.setData(routeTypeResponse.getTransWay());
            jdCResponse.toConfirm(routeTypeResponse.getMessage());
            return jdCResponse;
        }
        if (routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_OK)){
            jdCResponse.setData(routeTypeResponse.getTransWay());
            jdCResponse.toSucceed(routeTypeResponse.getMessage());
            return jdCResponse;
        }

        jdCResponse.toFail(routeTypeResponse.getMessage());
        return jdCResponse;
    }

    /**
     * 获取运力编码信息
     */
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getTransportCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<TransportInfoDto> getTransportInfoByCode(CapacityInfoRequest request) {
        JdCResponse<TransportInfoDto> jdCResponse = new JdCResponse<>();
        NewSealVehicleRequest param = new NewSealVehicleRequest();

        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setUserCode(request.getUser().getUserCode());
        param.setUserName(request.getUser().getUserName());
        param.setTransportCode(request.getTransportCode());
        RouteTypeResponse routeTypeResponse = newSealVehicleResource.getTransportCode(param);
        if(routeTypeResponse.getCode().equals(JdResponse.CODE_OK)){
            jdCResponse.toSucceed(routeTypeResponse.getMessage());
        }else if(routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_UNSEAL_CAR_OUT_CHECK) || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_CHECK)
                || routeTypeResponse.getCode().equals(NewSealVehicleResponse.CODE_TRANSPORT_RANGE_ERROR)){
            jdCResponse.toConfirm(routeTypeResponse.getMessage());
        }else {
            jdCResponse.toFail(routeTypeResponse.getMessage());
        }
        TransportInfoDto transportInfoDto = new TransportInfoDto();
        BeanUtils.copyProperties(routeTypeResponse,transportInfoDto);
        jdCResponse.setData(transportInfoDto);
        return jdCResponse;
    }

    /**
     * 校验任务简码与运力编号是否匹配
     */
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.checkTransportCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
    @Deprecated
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.checkTranCodeAndBatchCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
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
     * 检查运力编码和批次号目的地是否一致（新）
     */
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.newCheckTranCodeAndBatchCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse newCheckTranCodeAndBatchCode(SealCarPreRequest sealCarPreRequest) {
        JdCResponse jdCResponse = new JdCResponse<>();

        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.newCheckTranCodeAndBatchCode(sealCarPreRequest);

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
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1012)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.sealCar",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse sealCar(SealCarRequest sealCarRequest) {
        JdCResponse jdCResponse = new JdCResponse();
        NewSealVehicleRequest newSealVehicleRequest = new NewSealVehicleRequest();
        List<SealCarDto> list = sealCarRequest.getSealCarDtoList();
        newSealVehicleRequest.setData(convert(list));
        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.seal(newSealVehicleRequest);

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }

    /**
     * 【传摆封车】&& 按运力封车需 校验车牌号能否创建车次任务
     */
    @Deprecated
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.verifyVehicleJobByVehicleNumber",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse verifyVehicleJobByVehicleNumber(String transportCode, String vehicleNumber, Integer sealCarType) {
        JdCResponse jdCResponse = new JdCResponse();

        NewSealVehicleResponse response = newSealVehicleResource.verifyVehicleJobByVehicleNumber(transportCode, vehicleNumber, sealCarType);

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    /**
     * 【传摆封车】&& 按运力封车需 校验车牌号能否创建车次任务（新）
     */
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.newVerifyVehicleJobByVehicleNumber",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest) {
        JdCResponse jdCResponse = new JdCResponse();

        NewSealVehicleResponse response = newSealVehicleResource.newVerifyVehicleJobByVehicleNumber(sealCarPreRequest);

        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());

        return jdCResponse;
    }

    /**
     * 校验批次体积是否合格
     *  1、现阶段只支持按任务公路零担
     *
     * @param request
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.verifySendVolume",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse verifySendVolume(SealVehicleVolumeVerifyRequest request) {
        JdCResponse jdCResponse = new JdCResponse();

        SealVehicleVolumeVerifyResponse response = newSealVehicleResource.verifySendVolume(request);
        jdCResponse.setCode(response.getCode());
        jdCResponse.setMessage(response.getMessage());
        return jdCResponse;
    }

    /**
     * 传摆封车
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1013)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.doSealCarWithVehicleJob",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse doSealCarWithVehicleJob(SealCarRequest sealCarRequest) {
        JdCResponse jdCResponse = new JdCResponse();

        NewSealVehicleRequest newSealVehicleRequest = new NewSealVehicleRequest();
        List<SealCarDto> list = sealCarRequest.getSealCarDtoList();
        newSealVehicleRequest.setData(convert(list));

        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.doSealCarWithVehicleJob(newSealVehicleRequest);

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }

    /**
     * 传摆预封车
     * @param sealCarRequest
     * @return
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1014)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.preSealFerry",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<Void> preSealFerry(SealCarRequest sealCarRequest) {
        JdVerifyResponse<Void> jdvResponse = new JdVerifyResponse();

        NewSealVehicleRequest newSealVehicleRequest = new NewSealVehicleRequest();
        List<SealCarDto> list = sealCarRequest.getSealCarDtoList();
        newSealVehicleRequest.setData(convert(list));

        NewSealVehicleResponse<Boolean> newSealVehicleResponse = preSealVehicleResource.preSealFerry(newSealVehicleRequest);
        if(NewSealVehicleResponse.CODE_OK.equals(newSealVehicleResponse.getCode())){
            jdvResponse.toSuccess(newSealVehicleResponse.getMessage());
            return jdvResponse;
        }

        int confirmCode=30000;
        if(newSealVehicleResponse.getCode()>confirmCode){
            jdvResponse.toSuccess(newSealVehicleResponse.getMessage());
            jdvResponse.addConfirmBox(newSealVehicleResponse.getCode(),newSealVehicleResponse.getMessage());
        }else {
            jdvResponse.toFail(newSealVehicleResponse.getMessage());
        }

        return jdvResponse;
    }

    /**
     * 传摆预封车更新服务
     * @param sealCarRequest
     * @return
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1015)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.updatePreSealFerry",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse updatePreSealFerry(SealCarRequest sealCarRequest) {
        JdCResponse jdCResponse = new JdCResponse();

        NewSealVehicleRequest newSealVehicleRequest = new NewSealVehicleRequest();
        List<SealCarDto> list = sealCarRequest.getSealCarDtoList();
        newSealVehicleRequest.setData(convert(list));

        NewSealVehicleResponse<Boolean> newSealVehicleResponse = preSealVehicleResource.updatePreSealFerry(newSealVehicleRequest);
        if(NewSealVehicleResponse.CODE_OK.equals(newSealVehicleResponse.getCode())){
            jdCResponse.toSucceed(newSealVehicleResponse.getMessage());
            return jdCResponse;
        }

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }

    /**
     * 根据运力编码获取车辆信息（车牌、重量体积）
     * @param transportCode
     * @return
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1016)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getVehicleNumBySimpleCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<PreSealVehicleMeasureDto> getVehicleNumBySimpleCode(String transportCode){
        JdCResponse<PreSealVehicleMeasureDto> response=new JdCResponse<PreSealVehicleMeasureDto>();

        NewSealVehicleResponse<PreSealVehicleMeasureInfo> res=preSealVehicleResource.getVehicleNumBySimpleCode(transportCode);
        if(res.getCode().equals(NewSealVehicleResponse.CODE_OK)){
            response.setCode(JdCResponse.CODE_SUCCESS);
            response.setMessage(res.getMessage());
            response.setData(JSON.parseObject(JSON.toJSONString(res.getData()), PreSealVehicleMeasureDto.class));
        }else {
            response.setCode(res.getCode());
            response.setMessage(res.getMessage());
        }

        return response;
    }

    /**
     * 更新重量体积
     * @param request
     * @return
     */
    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1017)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.updatePreSealVehicleMeasureInfo",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse updatePreSealVehicleMeasureInfo(PreSealMeasureInfoRequest request) {
        JdCResponse jdCResponse = new JdCResponse();
        if (request.getTransportCode() == null || request.getVehicleNumber() == null) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("运力编码和车牌号不能为空！");
            return jdCResponse;
        }

        if (request.getVolume() == null || ! NumberHelper.gt0(request.getVolume())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("体积不能为空且必须大于0！");
            return jdCResponse;
        }

        com.jd.bluedragon.distribution.newseal.domain.PreSealMeasureInfoRequest resourceRequest =JSON.parseObject(JSON.toJSONString(request), com.jd.bluedragon.distribution.newseal.domain.PreSealMeasureInfoRequest.class);
        NewSealVehicleResponse<Boolean> newSealVehicleResponse = preSealVehicleResource.updatePreSealVehicleMeasureInfo(resourceRequest);
        if(newSealVehicleResponse.getCode().equals(NewSealVehicleResponse.CODE_OK)){
            jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        }else {
            jdCResponse.setCode(newSealVehicleResponse.getCode());
        }
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        return jdCResponse;
    }

    //参数转化
    private List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> convert(List<SealCarDto> list) {
        List<com.jd.bluedragon.distribution.wss.dto.SealCarDto> sealCarDtos = new ArrayList<>();
        com.jd.bluedragon.distribution.wss.dto.SealCarDto param;
        for (SealCarDto sc : list) {
            param = new com.jd.bluedragon.distribution.wss.dto.SealCarDto();
            param.setSealCarType(sc.getSealCarType());
            param.setItemSimpleCode(sc.getItemSimpleCode());
            param.setTransportCode(sc.getTransportCode());
            param.setTransWay(sc.getTransWay());
            param.setTransWayName(sc.getTransWayName());
            param.setBatchCodes(sc.getBatchCodes());
            param.setVehicleNumber(sc.getVehicleNumber());
            param.setSealCodes(sc.getSealCodes());
            param.setVolume(sc.getVolume());
            param.setWeight(sc.getWeight());
            int pc=NumberUtils.toInt(sc.getPalletCount(),-1);
            if (pc > -1) {
              param.setPalletCount(pc);
            }
            param.setRouteLineCode(sc.getRouteLineCode());
            param.setSealCarTime(sc.getSealCarTime());
            param.setSealSiteId(sc.getSealSiteId());
            param.setSealSiteName(sc.getSealSiteName());
            param.setSealUserCode(sc.getSealUserCode());
            param.setSealUserName(sc.getSealUserName());
            sealCarDtos.add(param);
        }
        return sealCarDtos;
    }
}
