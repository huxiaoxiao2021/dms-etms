package com.jd.bluedragon.core.base;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;

public interface AutoDistJsfManager {
    /**
     * 上海亚一的pda专用的包裹补码功能，实现扫描包裹号或者运单号将信息推送给WCS
     * <doc>
     *     上海亚一的pda调用的接口,如果不输入siteCode的值的话，默认传0
     * </doc>
     */
    BaseDmsAutoJsfResponse<Object> supplementSiteCode(String barCode , Integer siteCode);
}
