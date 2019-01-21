package com.jd.bluedragon.distribution.external.service.impl;

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
    public TransWorkItemResponse getVehicleNumBySimpleCode(String simpleCode) {
        return newSealVehicleResource.getVehicleNumBySimpleCode(simpleCode);
    }

    @Override
    public NewSealVehicleResponse getVehicleNumberOrItemCodeByParam(NewSealVehicleRequest request) {
        return newSealVehicleResource.getVehicleNumberOrItemCodeByParam(request);
    }

    @Override
    public TransWorkItemResponse checkTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.checkTransportCode(request);
    }

    @Override
    public NewSealVehicleResponse newCheckTranCodeAndBatchCode(String transportCode, String batchCode, Integer sealCarType) {
        return newSealVehicleResource.newCheckTranCodeAndBatchCode(transportCode, batchCode, sealCarType);
    }

    @Override
    public NewSealVehicleResponse seal(NewSealVehicleRequest request) {
        return newSealVehicleResource.seal(request);
    }

    @Override
    public NewSealVehicleResponse findSealInfo(NewSealVehicleRequest request) {
        return newSealVehicleResource.findSealInfo(request);
    }

    @Override
    public NewSealVehicleResponse unsealCheck(NewSealVehicleRequest request) {
        return newSealVehicleResource.unsealCheck(request);
    }

    @Override
    public NewSealVehicleResponse unseal(NewSealVehicleRequest request) {
        return newSealVehicleResource.unseal(request);
    }

    @Override
    public RouteTypeResponse getTransportCode(NewSealVehicleRequest request) {
        return newSealVehicleResource.getTransportCode(request);
    }

    @Override
    public SealVehicleResponse findSealVehicleByCode(String sealCode) {
        return sealVehicleResource.findSealByCode(sealCode);
    }

    @Override
    public SealBoxResponse findSealBoxByBoxCode(String sealBoxCode) {
        return sealBoxResource.findSealByCode(sealBoxCode);
    }

    @Override
    public boolean checkSendCodeIsSealed(String sendCode) {
        if (newSealVehicleService.getSealCarTimeBySendCode(sendCode) != null) {
            return true;
        }
        return false;
    }


}
