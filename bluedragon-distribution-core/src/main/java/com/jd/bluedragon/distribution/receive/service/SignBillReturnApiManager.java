package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/25 16:46
 */
public interface SignBillReturnApiManager {

    InvokeResult<Boolean> checkSignBillReturn(String newWaybillCode , Integer siteId);
}
    
