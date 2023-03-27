package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.bluedragon.core.jsf.address.DmsExternalJDAddressResponse;
import com.jd.bluedragon.core.jsf.address.ExternalAddressToJDAddressServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/27 11:06
 * @Description:
 */
@Service("externalAddressToJDAddressServiceImpl")
public class ExternalAddressToJDAddressServiceImpl {

    @Autowired
    private ExternalAddressToJDAddressServiceManager externalAddressToJDAddressServiceManager;

    public DmsExternalJDAddressResponse getJDDistrict(ExternalAddressRequest request){
        return externalAddressToJDAddressServiceManager.getJDDistrict(request);
    }
}
