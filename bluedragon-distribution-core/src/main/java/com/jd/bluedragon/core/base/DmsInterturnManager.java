package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月04日 13时:32分
 */
public interface DmsInterturnManager {

    /**
     * 预分拣 - 分拣中心查询商家是否可以走B网车队
     * @param siteCode:当前场地code
     * @param vendorId：商家ID
     * @param waybillSign
     * @return
     */
    InvokeResult<Boolean> dispatchToExpress(Integer siteCode, Integer vendorId, String waybillSign);
}
