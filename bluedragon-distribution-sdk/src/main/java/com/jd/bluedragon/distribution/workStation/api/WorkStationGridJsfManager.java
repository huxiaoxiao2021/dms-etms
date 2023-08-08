package com.jd.bluedragon.distribution.workStation.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQuery;

/**
 * @Author: ext.lishaotan5@jd.com
 * @Date: 2023/8/7 15:13
 * @Description:三定 获取运输月台号和联系人
 */
public interface WorkStationGridJsfManager {

    /**
     * 获取运输月台号和联系人
     * @param dockCodeAndPhoneQuery
     * @return Result<DockCodeAndPhone>
     */
    Result<DockCodeAndPhone> queryDockCodeAndPhone(DockCodeAndPhoneQuery dockCodeAndPhoneQuery);
}
