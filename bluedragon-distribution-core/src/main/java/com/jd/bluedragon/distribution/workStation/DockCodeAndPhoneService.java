package com.jd.bluedragon.distribution.workStation;



import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;


/**
 * 获取运输月台号和联系人接口
 *
 * @author hujiping
 * @date 2022/4/6 6:04 PM
 */
public interface DockCodeAndPhoneService {

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
