package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.Response;

/**
 * @author : xumigen
 * @date : 2020/7/2
 */
public interface DmsBoxQueryService {

    Response<Boolean> isEconomicNetBox(String boxCode);
}
