package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.Response;

/**
 * @author : xumigen
 * @date : 2020/7/2
 */
public interface DmsBoxQueryService {

    /**
     * 是否是经济网箱号
     * @param boxCode
     * @return
     */
    Response<Boolean> isEconomicNetBox(String boxCode);

    /**
     * 如果是经济网并且没有绑定明细 则返回 true  否则是 false
     * @param boxCode
     * @return
     */
    Response<Boolean> isEconomicNetBoxAndNotBoundingPackage(String boxCode);
}
