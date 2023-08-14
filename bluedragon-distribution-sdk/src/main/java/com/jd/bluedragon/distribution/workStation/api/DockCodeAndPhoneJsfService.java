package com.jd.bluedragon.distribution.workStation.api;


import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;


/**
 * @Author: ext.lishaotan5@jd.com
 * @Date: 2023/8/7 15:13
 * @Description: 获取运输月台号和联系人
 */
public interface DockCodeAndPhoneJsfService {

    /**
     * 获取运输月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    Result<List<String>> queryDockCodeByFlowDirection(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO);

    /**
     * 获取联系人
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    Result<DockCodeAndPhone> queryPhoneByDockCodeForTms(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO);
}
