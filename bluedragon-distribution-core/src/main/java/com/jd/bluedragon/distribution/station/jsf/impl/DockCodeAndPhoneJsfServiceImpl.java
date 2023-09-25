package com.jd.bluedragon.distribution.station.jsf.impl;


import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.bluedragon.distribution.workStation.api.DockCodeAndPhoneJsfService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQueryDTO;
import com.jd.dms.java.utils.sdk.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 15:12
 * @Description: 三定场地网格工序管理
 */
@Slf4j
@Service("dockCodeAndPhoneJsfService")
public class DockCodeAndPhoneJsfServiceImpl implements DockCodeAndPhoneJsfService {

    @Autowired
    private DockCodeAndPhoneService dockCodeAndPhoneService;

    /**
     * 获取运输月台号
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    @Override
    public Result<List<String>> queryDockCodeByFlowDirection(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        return dockCodeAndPhoneService.queryDockCodeByFlowDirection(dockCodeAndPhoneQueryDTO);
    }

    /**
     * 获取联系人
     *
     * @param dockCodeAndPhoneQueryDTO
     * @return JdResponse<DockCodeAndPhone>
     */
    @Override
    public Result<DockCodeAndPhone> queryPhoneByDockCodeForTms(DockCodeAndPhoneQueryDTO dockCodeAndPhoneQueryDTO) {
        return dockCodeAndPhoneService.queryPhoneByDockCodeForTms(dockCodeAndPhoneQueryDTO);
    }
}
