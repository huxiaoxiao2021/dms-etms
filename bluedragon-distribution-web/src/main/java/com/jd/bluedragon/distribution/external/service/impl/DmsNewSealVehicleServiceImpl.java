package com.jd.bluedragon.distribution.external.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.blockcar.request.SealCarTaskInfoRequest;
import com.jd.bluedragon.common.dto.blockcar.response.SealCarTaskInfoDto;
import com.jd.bluedragon.distribution.api.request.NewSealVehicleRequest;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.api.response.SealBoxResponse;
import com.jd.bluedragon.distribution.api.response.SealVehicleResponse;
import com.jd.bluedragon.distribution.api.response.TransWorkItemResponse;
import com.jd.bluedragon.distribution.external.service.DmsNewSealVehicleService;
import com.jd.bluedragon.distribution.rest.seal.NewSealVehicleResource;
import com.jd.bluedragon.distribution.rest.seal.SealBoxResource;
import com.jd.bluedragon.distribution.rest.seal.SealVehicleResource;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
@Service("dmsNewSealVehicleService")
public class DmsNewSealVehicleServiceImpl implements DmsNewSealVehicleService {

    @Autowired
    @Qualifier("newSealVehicleResource")
    private NewSealVehicleResource newSealVehicleResource;

    @Autowired
    @Qualifier("sealVehicleResource")
    private SealVehicleResource sealVehicleResource;

    @Autowired
    @Qualifier("sealBoxResource")
    private SealBoxResource sealBoxResource;

    @Autowired
    @Qualifier("newSealVehicleService")
    private NewSealVehicleService newSealVehicleService;

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumBySimpleCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TransWorkItemResponse getVehicleNumBySimpleCode(String simpleCode) {
        return newSealVehicleResource.getVehicleNumBySimpleCode(simpleCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumberOrItemCodeByParam", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse getVehicleNumberOrItemCodeByParam(NewSealVehicleRequest request) {
        return newSealVehicleResource.getVehicleNumberOrItemCodeByParam(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.checkTransportCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public TransWorkItemResponse checkTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.checkTransportCode(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.newCheckTranCodeAndBatchCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType) {
        return newSealVehicleResource.newCheckTranCodeAndBatchCode(transportCode, batchCode, sealCarType);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.seal", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        return newSealVehicleResource.seal(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealInfo", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request) {
        return newSealVehicleResource.findSealInfo(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.unsealCheck", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse unsealCheck(NewSealVehicleRequest request) {
        return newSealVehicleResource.unsealCheck(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.unseal", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        return newSealVehicleResource.unseal(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getTransportCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public RouteTypeResponse getTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.getTransportCode(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealVehicleByCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SealVehicleResponse findSealVehicleByCode(String sealCode) {
        return sealVehicleResource.findSealByCode(sealCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.findSealBoxByBoxCode", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public SealBoxResponse findSealBoxByBoxCode(String sealBoxCode) {
        return sealBoxResource.findSealByCode(sealBoxCode);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.checkSendCodeIsSealed", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean checkSendCodeIsSealed(String sendCode) {
        if (newSealVehicleService.getSealCarTimeBySendCode(sendCode) != null) {
            return true;
        }
        return false;
    }
    /**
     * 根据车牌号获取派车明细编码或根据派车明细编码获取车牌号
     */
    @Override
    @JProfiler(jKey = "DMSWEB.DmsNewSealVehicleServiceImpl.getVehicleNumberOrItemCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkItemResponse getVehicleNumberOrItemCode(NewSealVehicleRequest request){
        return newSealVehicleResource.getVehicleNumberOrItemCodeByParam(request);
    }

}
