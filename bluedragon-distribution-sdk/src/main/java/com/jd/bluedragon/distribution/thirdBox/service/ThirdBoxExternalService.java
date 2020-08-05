package com.jd.bluedragon.distribution.thirdBox.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.thirdBox.request.WaybillSyncRequest;

/**
 * 三方装箱
 * @author : xumigen
 * @date : 2020/6/17
 */
public interface ThirdBoxExternalService {
    Response<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request, String pin);
}
