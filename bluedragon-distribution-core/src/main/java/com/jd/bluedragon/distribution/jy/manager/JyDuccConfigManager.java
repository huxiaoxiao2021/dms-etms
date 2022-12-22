package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.jy.realtime.base.ServiceResult;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/12/21 18:31
 * @Description:
 */
public interface JyDuccConfigManager {

    /**
     * 获取拣运发货岗主备开关切换值
     * @return
     */
    Boolean getJySendAggsDataReadSwitchInfo();

    /**
     * 获取拣运卸车岗主备开关切换值
     * @return
     */
    Boolean getJyUnloadAggsDataReadSwitchInfo();
}
