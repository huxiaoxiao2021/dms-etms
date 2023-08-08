package com.jd.bluedragon.core.jsf.workStation.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.DockCodeAndPhone;
import com.jd.bluedragon.common.domain.DockCodeAndPhoneQuery;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridJsfManager;
import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.service.workStation.WorkStationGridJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 15:12
 * @Description: 三定场地网格工序管理
 */
@Slf4j
@Service
public class WorkStationGridJsfManagerImpl implements WorkStationGridJsfManager {

    @Autowired
    private WorkStationGridJsfService basicWorkStationGridJsfService;

    @Autowired
    private DockCodeAndPhoneService dockCodeAndPhoneService;


    /**
     * 获取运输月台号和联系人
     * @param
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationGridJsfManagerImpl.queryDockCodeAndPhone",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<DockCodeAndPhone> queryDockCodeAndPhone(DockCodeAndPhoneQuery dockCodeAndPhoneQuery) {
        Result<DockCodeAndPhone> result = new Result<>();
        DockCodeAndPhone dockCodeAndPhone = dockCodeAndPhoneService.queryDockCodeAndPhone(dockCodeAndPhoneQuery);
        result.setData(dockCodeAndPhone);
        return result;
    }
}
