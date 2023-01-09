package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface TransportRelatedJSFService {

    /*
        判断是否是合流车
        Integer siteCode,
        String transWorkCode,
        String sealCarCode,
        String simpleCode,
        String vehicleNumber
     */
    public InvokeResult<String> isMergeCar(Integer siteCode, String transWorkCode, String sealCarCode,
                                           String simpleCode, String vehicleNumber);
}
