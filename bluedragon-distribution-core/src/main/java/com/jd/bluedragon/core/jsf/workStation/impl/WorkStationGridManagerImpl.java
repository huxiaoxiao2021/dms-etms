package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationGridManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionData;
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
    private WorkStationGridJsfService basicWorkStationGridJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationGridManagerImpl.queryByBusinessKey",mState={JProEnum.TP,JProEnum.FunctionError})

    public Result<WorkStationGrid> queryByBusinessKey(WorkStationGrid data) {
        Result<WorkStationGrid> result = new Result<>();
        result.toFail("获取三定场地网格工序数据失败");
        try {
            log.info("三定场地网格工序管理 queryByBusinessKey WorkStationGrid 入参:"+ JSON.toJSONString(data));
            return basicWorkStationGridJsfService.queryByBusinessKey(data);
        } catch (Exception e) {
            log.error("获取三定场地网格工序数据异常 {}",  e.getMessage(),e);
            result.toFail("获取三定场地网格工序异常!");
        }
       return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationGridManagerImpl.queryByGridKey",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<WorkStationGrid> queryByGridKey(WorkStationGridQuery workStationGridCheckQuery) {
        Result<WorkStationGrid> result = new Result<>();
        result.toFail("获取三定场地网格工序数据失败");
        try {
            log.info("三定场地网格工序管理 queryByGridKey WorkStationGridQuery 入参:"+ JSON.toJSONString(workStationGridCheckQuery));
            return basicWorkStationGridJsfService.queryByGridKey(workStationGridCheckQuery);
        } catch (Exception e) {
            log.error("获取三定场地网格工序数据异常 {}",  e.getMessage(),e);
            result.toFail("获取三定场地网格工序异常!");
        }
        return result;
    }
}
