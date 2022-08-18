package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.jsf.workStation.WorkStationAttendPlanManager;
import com.jdl.basic.api.domain.workStation.WorkStationAttendPlan;
import com.jdl.basic.api.service.workStation.WorkStationAttendPlanJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 16:40
 * @Description:
 */
@Slf4j
@Service
public class WorkStationAttendPlanManagerImpl implements WorkStationAttendPlanManager {

    @Autowired
    private WorkStationAttendPlanJsfService basicWorkStationAttendPlanJsfService;

    @Override
    public Result<WorkStationAttendPlan> queryByBusinessKeys(WorkStationAttendPlan data) {
        log.info("三定实时监控-场地人员出勤计划 queryByBusinessKeys-WorkStationAttendPlan-{} "+ JSON.toJSONString(data));
        return basicWorkStationAttendPlanJsfService.queryByBusinessKeys(data);
    }
}
