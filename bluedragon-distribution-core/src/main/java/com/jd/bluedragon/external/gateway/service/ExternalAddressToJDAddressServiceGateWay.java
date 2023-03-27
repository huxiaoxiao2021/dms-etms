package com.jd.bluedragon.external.gateway.service;

import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.bluedragon.core.jsf.address.DmsExternalJDAddressResponse;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/27 11:10
 * @Description:
 */
public interface ExternalAddressToJDAddressServiceGateWay {

    DmsExternalJDAddressResponse getJDDistrict(ExternalAddressRequest request);
}
