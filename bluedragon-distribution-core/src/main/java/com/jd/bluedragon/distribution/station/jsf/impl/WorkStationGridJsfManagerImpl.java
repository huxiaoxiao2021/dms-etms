package com.jd.bluedragon.distribution.station.jsf.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.workStation.api.WorkStationGridJsfManager;
import com.jd.bluedragon.distribution.workStation.DockCodeAndPhoneService;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhone;
import com.jd.bluedragon.distribution.workStation.domain.DockCodeAndPhoneQuery;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 15:12
 * @Description: 三定场地网格工序管理
 */
@Slf4j
@Service("workStationGridJsfManager")
public class WorkStationGridJsfManagerImpl implements WorkStationGridJsfManager {

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
