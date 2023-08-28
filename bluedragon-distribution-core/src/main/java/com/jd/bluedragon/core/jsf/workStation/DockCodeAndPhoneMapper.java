package com.jd.bluedragon.core.jsf.workStation;

import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;

import java.util.List;


/**
 * 获取运输月台号和联系人接口
 *
 * @author hujiping
 * @date 2022/4/6 6:04 PM
 */
public interface DockCodeAndPhoneMapper {

    /**
     * 获取运输月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    Result<List<String>> queryDockCodeByFlowDirection(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO);

    /**
     * 根据月台号获取网格信息
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<WorkStationGrid>
     */
    Result<List<WorkStationGrid>> queryPhoneByDockCodeForTms(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO);
}
