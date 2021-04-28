package com.jd.bluedragon.core.jsf.dms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;

/**
 * 运单拦截信息接口
 */
public interface BlockerQueryWSJsfManager {


    /**
     * 获取运单拦截信息接口
     * @param waybillCode
     * @return
     */
    JdCResponse queryExceptionOrders(String waybillCode);

}
