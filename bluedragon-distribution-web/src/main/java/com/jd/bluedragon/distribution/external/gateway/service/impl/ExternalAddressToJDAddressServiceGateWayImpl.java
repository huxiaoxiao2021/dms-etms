package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.bluedragon.core.jsf.address.DmsExternalJDAddressResponse;
import com.jd.bluedragon.distribution.jy.service.exception.ExternalAddressToJDAddressServiceImpl;
import com.jd.bluedragon.external.gateway.service.ExternalAddressToJDAddressServiceGateWay;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/27 11:08
 * @Description:
 */
public class ExternalAddressToJDAddressServiceGateWayImpl implements ExternalAddressToJDAddressServiceGateWay {
    @Autowired
    private ExternalAddressToJDAddressServiceImpl externalAddressToJDAddressServiceImpl;
    @Override
    public DmsExternalJDAddressResponse getJDDistrict(ExternalAddressRequest request) {
        return externalAddressToJDAddressServiceImpl.getJDDistrict(request);
    }
}
