package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.response.RouteTypeResponse;
import com.jd.bluedragon.distribution.base.domain.CreateAndReceiveSiteInfo;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * 发往物流网关的接口不要在此类中加方法
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsSiteService {

    /**
     * 按运力封车时：通过扫描的运力编码获取运力详细信息
     *
     * @param capacityCode
     * @return
     */
    RouteTypeResponse getCapacityCodeInfo(String capacityCode);

    /**
     * 获取批次信息
     *
     * @param sendCode
     * @return
     */
    InvokeResult<CreateAndReceiveSiteInfo> getSitesInfoBySendCode(String sendCode);

}
