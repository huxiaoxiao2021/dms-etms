package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.transport.service.TransportRelatedJSFService;
import com.jd.bluedragon.distribution.transport.service.TransportRelatedService;
import org.springframework.stereotype.Service;

@Service("transportRelatedJSFService")
public class TransportRelatedJSFServiceImpl implements TransportRelatedJSFService {

    private TransportRelatedService transportRelatedService;

    @Override
    public InvokeResult<String> isMergeCar(Integer siteCode, String transWorkCode, String sealCarCode, String simpleCode, String vehicleNumber) {
        InvokeResult<String> invokeResult = new InvokeResult<>();
        String isMergeCar = transportRelatedService.isMergeCar(siteCode, transWorkCode, sealCarCode, simpleCode, vehicleNumber);
        invokeResult.setData(isMergeCar);
        if (isMergeCar == null) {
            invokeResult.error();
        } else {
            invokeResult.success();
        }
        return invokeResult;
    }
}
