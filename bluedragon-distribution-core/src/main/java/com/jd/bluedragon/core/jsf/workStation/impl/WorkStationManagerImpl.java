package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;

import com.jd.bluedragon.core.jsf.workStation.WorkStationManager;
import com.jdl.basic.api.domain.workStation.WorkStation;
import com.jdl.basic.api.service.workStation.WorkStationJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/3 15:58
 * @Description:
 */
@Slf4j
@Service("workStationManager")
public class WorkStationManagerImpl implements WorkStationManager {

    @Autowired
    private WorkStationJsfService basicWorkStationJsfService;

    @Override
    public Result<WorkStation> queryByBusinessKey(WorkStation data) {
        log.info("三定网格工序管理 queryByBusinessKey-入参 {}", JSON.toJSONString(data));
        return  basicWorkStationJsfService.queryByBusinessKey(data);
    }

    @Override
    public Result<WorkStation> queryByRealBusinessKey(String businessKey) {
        log.info("三定网格工序管理 queryByRealBusinessKey-入参 {}", businessKey);
        return basicWorkStationJsfService.queryByRealBusinessKey(businessKey);
    }
}
