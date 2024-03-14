package com.jd.bluedragon.core.base;

import com.jd.ql.data.api.model.jsf.WaybillMonitorDto;
import com.jd.ql.data.api.model.jsf.WaybillMonitorQuery;
import com.jd.ql.data.api.model.web.GoldShieldResult;

public interface GoldShieldDataManager {

    /**
     * 根据运单号查询责任单位接口
     * @param query
     * @return
     */
    GoldShieldResult<WaybillMonitorDto> queryGsMonitorInfo(WaybillMonitorQuery query);
}
