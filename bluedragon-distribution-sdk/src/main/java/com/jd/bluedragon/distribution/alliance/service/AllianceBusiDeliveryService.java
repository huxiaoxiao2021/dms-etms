package com.jd.bluedragon.distribution.alliance.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface AllianceBusiDeliveryService {

    /**
     * 加盟商余额校验
     * @param waybillCode 运单号
     * @return 校验结果
     */
    InvokeResult<Boolean> checkAllianceMoney(String waybillCode);

}
