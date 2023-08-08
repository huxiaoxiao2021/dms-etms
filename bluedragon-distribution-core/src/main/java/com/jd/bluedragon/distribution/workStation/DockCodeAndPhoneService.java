package com.jd.bluedragon.distribution.workStation;


import com.jd.bluedragon.common.domain.DockCodeAndPhone;
import com.jd.bluedragon.common.domain.DockCodeAndPhoneQuery;

/**
 * 获取运输月台号和联系人接口
 *
 * @author hujiping
 * @date 2022/4/6 6:04 PM
 */
public interface DockCodeAndPhoneService {

    /**
     * 获取运输月台号和联系人
     *
     * @param
     * @return
     */
    DockCodeAndPhone queryDockCodeAndPhone(DockCodeAndPhoneQuery dockCodeAndPhoneQuery);
}
