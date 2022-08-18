package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
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
public class WorkStationGridManagerImpl implements WorkStationGridManager {

    @Autowired
    private WorkStationGridJsfService workStationGridJsfService;

    @Override
    public Result<WorkStationGrid> queryByBusinessKey(WorkStationGrid data) {
        log.info("三定场地网格工序管理 queryByBusinessKey WorkStationGrid 入参:"+ JSON.toJSONString(data));
        return workStationGridJsfService.queryByBusinessKey(data);
    }

    @Override
    public Result<WorkStationGrid> queryByGridKey(WorkStationGridQuery workStationGridCheckQuery) {
        log.info("三定场地网格工序管理 queryByGridKey WorkStationGridQuery 入参:"+ JSON.toJSONString(workStationGridCheckQuery));
        return workStationGridJsfService.queryByGridKey(workStationGridCheckQuery);
    }
}
