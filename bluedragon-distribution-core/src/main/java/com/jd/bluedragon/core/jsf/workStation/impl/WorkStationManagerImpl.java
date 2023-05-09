package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkStationManager;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionData;
import com.jdl.basic.api.domain.workStation.WorkStation;
import com.jdl.basic.api.domain.workStation.WorkStationJobTypeDto;
import com.jdl.basic.api.service.workStation.WorkStationJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationManagerImpl.queryByBusinessKey",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<WorkStation> queryByBusinessKey(WorkStation data) {
        Result<WorkStation> result = new Result<>();
        result.toFail("获取三定网格工序数据失败");

        try {
            log.info("三定网格工序管理 queryByBusinessKey-入参 {}", JSON.toJSONString(data));
            return  basicWorkStationJsfService.queryByBusinessKey(data);
        } catch (Exception e) {
            log.error("获取三定网格工序数据异常 {}",  e.getMessage(),e);
            result.toFail("获取三定网格工序数据异常!");
        }
       return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationManagerImpl.queryByRealBusinessKey",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<WorkStation> queryByRealBusinessKey(String businessKey) {
        Result<WorkStation> result = new Result<>();
        result.toFail("获取三定网格工序数据失败");
        try {
            log.info("三定网格工序管理 queryByRealBusinessKey-入参 {}", businessKey);
            return basicWorkStationJsfService.queryByRealBusinessKey(businessKey);
        } catch (Exception e) {
            log.error("获取三定网格工序数据异常 {}",  e.getMessage(),e);
            result.toFail("获取三定网格工序数据异常!");
        }
       return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkStationManagerImpl.queryWorkStationJobTypeBybusinessKey",mState={JProEnum.TP,JProEnum.FunctionError})
    @Cache(key = "WorkStationManagerImpl.queryWorkStationJobTypeBybusinessKey@args0", memoryEnable = true, memoryExpiredTime = 1 * 60 * 1000
            ,redisEnable = true, redisExpiredTime = 2 * 60 * 1000)
    public List<WorkStationJobTypeDto> queryWorkStationJobTypeBybusinessKey(String businessKey) {
        try {
            log.info("三定网格工序工种管理 queryWorkStationJobTypeBybusinessKey-入参 {}", businessKey);
            Result<List<WorkStationJobTypeDto>> result = basicWorkStationJsfService.queryWorkStationJobTypeBybusinessKey(businessKey);
            if(result != null && result.isSuccess()){
                return result.getData();
            }

        } catch (Exception e) {
            log.error("获取三定网格工序工种数据异常 {}",  e.getMessage(),e);
        }
        return null;
    }
}
