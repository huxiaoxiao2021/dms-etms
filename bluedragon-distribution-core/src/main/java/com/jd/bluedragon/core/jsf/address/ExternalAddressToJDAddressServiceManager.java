package com.jd.bluedragon.core.jsf.address;

import com.jd.addresstranslation.api.base.ExternalAddressRequest;
import com.jd.bluedragon.distribution.command.JdResult;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/1 11:17
 * @Description: gis 地址解析服务
 */
public interface ExternalAddressToJDAddressServiceManager {

    DmsExternalJDAddressResponse getJDDistrict(ExternalAddressRequest request);
}
