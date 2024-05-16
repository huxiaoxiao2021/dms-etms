package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.request.*;
import com.jd.bluedragon.common.dto.blockcar.response.PreSealVehicleMeasureDto;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.common.dto.blockcar.response.SealVehicleResponseData;
import com.jd.bluedragon.common.dto.blockcar.response.TransportInfoDto;
import com.jd.bluedragon.common.dto.seal.request.CancelSealRequest;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.TransAbnormalTypeDto;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.dto.send.BatchCodeShuttleSealDto;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.enums.OperateBizSubTypeEnum;
import com.jd.bluedragon.distribution.jy.service.send.JyAviationRailwaySendSealService;
import com.jd.bluedragon.distribution.newseal.domain.CancelPreSealVehicleRequest;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleMeasureInfo;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.distribution.rest.seal.PreSealVehicleResource;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.external.gateway.service.NewSealVehicleGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ql.dms.report.WeightVolSendCodeJSFService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolSendCodeSumVo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : xumigen
 * @date : 2019/7/8
 */
public class NewSealVehicleGatewayServiceImpl implements NewSealVehicleGatewayService {
    private static final Logger logger = LoggerFactory.getLogger(NewSealVehicleGatewayServiceImpl.class);


    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;

    @Autowired
    @Qualifier("preSealVehicleResource")
    private PreSealVehicleResource preSealVehicleResource;

    @Autowired
    private NewSealVehicleService newSealVehicleService;
    @Autowired
    private WeightVolSendCodeJSFService weightVolSendCodeJSFService;

    @Autowired
    private JyAviationRailwaySendSealService jyAviationRailwaySendSealService;
    @Autowired
    @Qualifier("aviationRailwayShuttleSealProducer")
    private DefaultJMQProducer aviationRailwayShuttleSealProducer;

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
    public JdVerifyResponse<SealCarTaskInfoDto> getTaskInfo(SealCarTaskInfoRequest request) {

        JdVerifyResponse<SealCarTaskInfoDto> response = new JdVerifyResponse<SealCarTaskInfoDto>();
        SealCarTaskInfoDto sealCarTaskInfoDto = new SealCarTaskInfoDto();

        NewSealVehicleRequest param = new NewSealVehicleRequest();
        param.setTransWorkItemCode(request.getTransWorkItemCode());
        param.setVehicleNumber(request.getVehicleNumber());
        param.setUserErp(request.getErp());
        param.setDmsCode(request.getDmsCode());
        param.setDmsSiteId(request.getDmsSiteId());

        TransWorkItemResponse transWorkItemResponse = newSealVehicleResource.getVehicleNumberOrItemCodeByParam(param);


        sealCarTaskInfoDto.setRouteLineCode(transWorkItemResponse.getRouteLineCode());
        sealCarTaskInfoDto.setRouteLineName(transWorkItemResponse.getRouteLineName());
        sealCarTaskInfoDto.setSendCode(transWorkItemResponse.getSendCode());
        sealCarTaskInfoDto.setTransType(transWorkItemResponse.getTransType());
        sealCarTaskInfoDto.setTransWorkItemCode(transWorkItemResponse.getTransWorkItemCode());
        sealCarTaskInfoDto.setVehicleNumber(transWorkItemResponse.getVehicleNumber());

        response.setCode(transWorkItemResponse.getCode());
        response.setMessage(transWorkItemResponse.getMessage());
        if(Objects.equals(transWorkItemResponse.getExtraBusinessCode(), TransWorkItemResponse.CODE_HINT)){
            response.addPromptBox(transWorkItemResponse.getExtraBusinessCode(), transWorkItemResponse.getExtraBusinessMessage());
        }
        response.setData(sealCarTaskInfoDto);

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
        transportInfoDto.setNextSiteCode(routeTypeResponse.getSiteCode());
        transportInfoDto.setNextSiteName(routeTypeResponse.getSiteName());
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
    public JdCResponse<SealVehicleResponseData> newCheckTranCodeAndBatchCode(SealCarPreRequest sealCarPreRequest) {
        JdCResponse<SealVehicleResponseData> jdCResponse = new JdCResponse<>();

        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.newCheckTranCodeAndBatchCode(sealCarPreRequest);
        SealVehicleResponseData data = new SealVehicleResponseData();

        if (newSealVehicleResponse.getCode() >= 30000 && newSealVehicleResponse.getCode() <= 40000) {
            if(Boolean.TRUE.equals(sealCarPreRequest.getQueryWeightVolumeFlag())) {
                fillWeightVolume(data, sealCarPreRequest.getSendCode());
            }
            jdCResponse.setCode(JdCResponse.CODE_CONFIRM);
            jdCResponse.setMessage(newSealVehicleResponse.getMessage());
            return jdCResponse;
        }
        data.setCode(newSealVehicleResponse.getExtraBusinessCode());
        data.setMessage(newSealVehicleResponse.getExtraBusinessMessage());
        jdCResponse.setData(data);
        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());
        if(jdCResponse.isSucceed() && Boolean.TRUE.equals(sealCarPreRequest.getQueryWeightVolumeFlag())) {
            fillWeightVolume(data, sealCarPreRequest.getSendCode());
        }
        return jdCResponse;
    }
    // 查询批次对应的包裹重量体积数据
    private void fillWeightVolume(SealVehicleResponseData response,String sendCode) {
        if(StringUtils.isBlank(sendCode)) {
            return;
        }
        Double weight = Constants.DOUBLE_ZERO;
        Double volume = Constants.DOUBLE_ZERO;
        try {
            final BaseEntity<WeightVolSendCodeSumVo> weightVolSendCodeSumVoBaseEntity = weightVolSendCodeJSFService.sumWeightAndVolumeBySendCode(sendCode);
            if (weightVolSendCodeSumVoBaseEntity != null && weightVolSendCodeSumVoBaseEntity.isSuccess()
                    && weightVolSendCodeSumVoBaseEntity.getData() != null) {
                if(!Objects.isNull(weightVolSendCodeSumVoBaseEntity.getData().getPackageVolumeSum())) {
                    volume = BigDecimal.valueOf(weightVolSendCodeSumVoBaseEntity.getData().getPackageVolumeSum()).setScale(2, RoundingMode.CEILING).doubleValue();
                }
                if(!Objects.isNull(weightVolSendCodeSumVoBaseEntity.getData().getPackageWeightSum())){
                    weight = BigDecimal.valueOf(weightVolSendCodeSumVoBaseEntity.getData().getPackageWeightSum()).setScale(2, RoundingMode.CEILING).doubleValue();
                }
            } else {
                logger.error("根据批次号查询批次下重量体积失败：{}", sendCode);
            }
        } catch (Exception e) {
            logger.error("查询批次称重量方jsf服务异常：批次号={},errMsg={} ", sendCode, e.getMessage(), e);
        }
        response.setWeight(weight);
        response.setVolume(volume);
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
        // 转换网格参数
        com.jd.bluedragon.distribution.api.domain.OperatorData operatorData
                = BeanConverter.convertToOperatorData(sealCarRequest.getCurrentOperate());
        // 设置网格信息
        newSealVehicleRequest.setOperatorData(operatorData);
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
    public JdVerifyResponse<Void> newVerifyVehicleJobByVehicleNumber(SealCarPreRequest sealCarPreRequest) {
        JdVerifyResponse<Void> jdVerifyResponse = new JdVerifyResponse<Void>();

        NewSealVehicleResponse response = newSealVehicleResource.newVerifyVehicleJobByVehicleNumber(sealCarPreRequest);

        jdVerifyResponse.setCode(response.getCode());
        jdVerifyResponse.setMessage(response.getMessage());
        if(Objects.equals(response.getExtraBusinessCode(), NewSealVehicleResponse.CODE_HINT)){
            jdVerifyResponse.addPromptBox(response.getExtraBusinessCode(), response.getExtraBusinessMessage());
        }
        return jdVerifyResponse;
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
        // 设置业务来源
        newSealVehicleRequest.setBizType(OperateBizSubTypeEnum.SHUTTLE_SEAL.getCode());
        // 转换网格参数
        com.jd.bluedragon.distribution.api.domain.OperatorData operatorData
                = BeanConverter.convertToOperatorData(sealCarRequest.getCurrentOperate());
        // 设置网格信息
        newSealVehicleRequest.setOperatorData(operatorData);
        NewSealVehicleResponse newSealVehicleResponse = newSealVehicleResource.doSealCarWithVehicleJob(newSealVehicleRequest);

        jdCResponse.setCode(newSealVehicleResponse.getCode());
        jdCResponse.setMessage(newSealVehicleResponse.getMessage());

        List<com.jd.etms.vos.dto.SealCarDto > successSealCarList = (List<com.jd.etms.vos.dto.SealCarDto>)newSealVehicleResponse.getData();
        //结果处理
        this.doSealCarWithVehicleJobResponseHandler(sealCarRequest, successSealCarList);
        return jdCResponse;
    }
    //干支封车结果集加工处理
    private void doSealCarWithVehicleJobResponseHandler(SealCarRequest request, List<com.jd.etms.vos.dto.SealCarDto> successSealCarList) {
        if(CollectionUtils.isEmpty(successSealCarList)) {
            return;
        }
        this.aviationRailwayTrunkSendTaskHandler(request, successSealCarList);
    }

    //空铁发货岗干支发货任务相关逻辑处理
    private void aviationRailwayTrunkSendTaskHandler(SealCarRequest request, List<com.jd.etms.vos.dto.SealCarDto > successSealCarList) {
        if(!JyFuncCodeEnum.AVIATION_RAILWAY_SEND_SEAL_POSITION.getCode().equals(request.getPost())) {
            return;
        }
        if(CollectionUtils.isEmpty(successSealCarList)) {
            return;
        }
        String transportCode = request.getSealCarDtoList().get(0).getTransportCode();

        List<String> successSealCarBatchCodes = successSealCarList.stream().map(o -> o.getBatchCodes()).flatMap(Collection::stream).collect(Collectors.toList());

        BatchCodeShuttleSealDto shuttleSealDto = new BatchCodeShuttleSealDto();
        shuttleSealDto.setSuccessSealBatchCodeList(successSealCarBatchCodes);
        shuttleSealDto.setOperateTime(new Date());
        shuttleSealDto.setTransportCode(transportCode);
        shuttleSealDto.setOperatorErp(Objects.isNull(request.getUser()) ? Constants.SYS_DMS : request.getUser().getUserErp());
        if(!Objects.isNull(request.getCurrentOperate())) {
            shuttleSealDto.setOperateSiteId(request.getCurrentOperate().getSiteCode());
        }
        try{
            jyAviationRailwaySendSealService.batchCodeShuttleSealMark(shuttleSealDto);
        }catch (Exception ex) {
            logger.error("传摆封车成功后触发修改航空干支封车传摆已封标识失败，errMsg={},封车成功批次为{}，request={}",
                    ex.getMessage(), JSONObject.toJSONString(successSealCarBatchCodes), JSONObject.toJSONString(request), ex);
            String msg = JsonHelper.toJson(shuttleSealDto);
            if(logger.isInfoEnabled()) {
                logger.info("传摆封车后处理空铁发货干支任务逻辑消息生产：businessId={},msg={}", transportCode, msg);
            }
            aviationRailwayShuttleSealProducer.sendOnFailPersistent(transportCode, msg);
        }
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

    @Override
    @BusinessLog(sourceSys = 1,bizType = 1011,operateType = 1018)
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.removeEmptyBatchCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> removeEmptyBatchCode(List<String> request){
        JdCResponse<List<String>> jdCResponse = new JdCResponse();
        if (CollectionUtils.isEmpty(request)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("批次号不能为空！");
            return jdCResponse;
        }
        List<String> removeBatchCodes=new ArrayList<>();
        for (String item : request) {
            if (!newSealVehicleService.checkBatchCodeIsNewSealVehicle(item)) {
                removeBatchCodes.add(item);
            }
        }

        jdCResponse.setCode(JdCResponse.CODE_SUCCESS);
        jdCResponse.setMessage(JdCResponse.MESSAGE_SUCCESS);
        jdCResponse.setData(removeBatchCodes);

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
            param.setSelectedSendCodes(sc.getSelectedSendCodes());
            sealCarDtos.add(param);
        }
        return sealCarDtos;
    }


    /**
     * 取消预封车
     * @param request
     * @return
     */
    @Override
    public JdCResponse<Boolean> cancelPreBlockCar(CancelPreBlockCarRequest request) {
        JdCResponse<Boolean> result = new JdCResponse<Boolean>();
        result.toSucceed("取消预封车成功！");
        //取消封车逻辑
        CancelPreSealVehicleRequest param = this.prepareParam(request);
        NewSealVehicleResponse serviceResult = preSealVehicleResource.cancelPreSeal(param);
        if (!JdResult.CODE_SUC.equals(serviceResult.getCode())){
            result.toFail(serviceResult.getMessage());
        }
        return result;
    }

    private CancelPreSealVehicleRequest prepareParam(CancelPreBlockCarRequest request) {
        CancelPreSealVehicleRequest param = new CancelPreSealVehicleRequest();
        param.setOperateUserErp(request.getUser().getUserErp());
        param.setOperateUserName(request.getUser().getUserName());
        param.setSiteCode(request.getCurrentOperate().getSiteCode());
        param.setSiteName(request.getCurrentOperate().getSiteName());
        param.setVehicleNumber(request.getCarNum());
        return param;
    }
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getUnSealSendCodes",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<String>> getUnSealSendCodes(SealCarPreRequest request) {
    	JdCResponse<List<String>> result = new JdCResponse<List<String>>();
    	if(request == null) {
    		result.toFail("传入参数为空！");
    		return result;
    	}
    	NewSealVehicleRequest queryRequest = new NewSealVehicleRequest();
    	queryRequest.setTransportCode(request.getTransportCode());
    	queryRequest.setVehicleNumber(request.getVehicleNumber());
    	queryRequest.setSiteCode(request.getCreateSiteCode());
    	JdResult<List<String>> queryResult = newSealVehicleService.getUnSealSendCodes(queryRequest);
    	if(queryResult != null && queryResult.isSucceed()) {
    		result.toSucceed(queryResult.getMessage());
    		result.setData(queryResult.getData());
    	}else if(queryResult != null){
    		result.toFail(queryResult.getMessage());
    	}else {
    		result.toFail("调用接口返回数据为空！");
    	}
    	return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.querySealCodes",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<SealCodesResponse> querySealCodes(SealCodeRequest param) {
        JdCResponse<SealCodesResponse> response = new JdCResponse<>();
        response.toSucceed();
        NewSealVehicleResponse<SealCodesResponse> sealVehicleResponse = newSealVehicleService.querySealCodes(param);
        if (null == sealVehicleResponse) {
            response.toFail();
        } else if (!NewSealVehicleResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
            response.toFail(sealVehicleResponse.getMessage());
        } else {
            response.setData(sealVehicleResponse.getData());
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.doDeSealCodes",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> doDeSealCodes(DeSealCodeRequest request) {
        JdCResponse<String> response = new JdCResponse<>();
        response.toSucceed();
        NewSealVehicleResponse<String> sealVehicleResponse = newSealVehicleService.doDeSealCodes(request);
        if (null == sealVehicleResponse) {
            response.toFail();
        } else if (!NewSealVehicleResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
            response.toFail(sealVehicleResponse.getMessage());
        } else {
            response.setData(sealVehicleResponse.getData());
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.createTransAbnormalStandard",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> createTransAbnormalStandard(TransAbnormalDto param) {
        JdCResponse<String> response = new JdCResponse<>();
        response.toSucceed();
        NewSealVehicleResponse<String> sealVehicleResponse = newSealVehicleService.createTransAbnormalStandard(param);
        if (null == sealVehicleResponse) {
            response.toFail();
        } else if (!NewSealVehicleResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
            response.toFail(sealVehicleResponse.getMessage());
        } else {
            response.setData(sealVehicleResponse.getData());
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.getTransAbnormalTypeCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<TransAbnormalTypeDto>> getTransAbnormalTypeCode() {
        JdCResponse<List<TransAbnormalTypeDto>> response = new JdCResponse<>();
        response.toSucceed();
        NewSealVehicleResponse<List<TransAbnormalTypeDto>> sealVehicleResponse = newSealVehicleService.getTransAbnormalTypeCode();
        if (null == sealVehicleResponse) {
            response.toFail();
        } else if (!NewSealVehicleResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
            response.toFail(sealVehicleResponse.getMessage());
        } else {
            response.setData(sealVehicleResponse.getData());
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.createTransAbnormalAndDeSealCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<String> createTransAbnormalAndDeSealCode(TransAbnormalAndDeSealRequest request) {
        JdCResponse<String> response = new JdCResponse<>();
        response.toSucceed();
        NewSealVehicleResponse<String> sealVehicleResponse = newSealVehicleService.createTransAbnormalAndDeSealCode(request);
        if (null == sealVehicleResponse) {
            response.toFail();
        } else if (!NewSealVehicleResponse.CODE_OK.equals(sealVehicleResponse.getCode())) {
            response.toFail(sealVehicleResponse.getMessage());
        } else {
            response.setData(sealVehicleResponse.getData());
        }
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.NewSealVehicleGatewayServiceImpl.createTransAbnormalAndUnseal",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public NewUnsealVehicleResponse<Boolean> createTransAbnormalAndUnseal(TransAbnormalAndUnsealRequest request) {
        return newSealVehicleResource.createTransAbnormalAndUnsealWithCheckUsage(request, false);
    }


	@Override
	public NewSealVehicleResponse<String> doSealCodes(DoSealCodeRequest request) {
		return newSealVehicleService.doSealCodes(request);
	}
}
