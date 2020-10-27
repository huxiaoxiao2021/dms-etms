package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.common.domain.WaybillCache;

public interface WaybillCacheService {

    /**
     * 取总部运单缓存信息
     * @return
     */
    WaybillCache getNoCache(String waybillCode);

    WaybillCache getFromCache(String waybillCode);

    String getRouterByWaybillCode(String waybillCode);

}
