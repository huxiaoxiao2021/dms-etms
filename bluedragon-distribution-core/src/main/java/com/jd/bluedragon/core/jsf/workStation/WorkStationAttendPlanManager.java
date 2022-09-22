package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.workStation.WorkStationAttendPlan;
import com.jdl.basic.common.utils.Result;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 16:33
 * @Description:三定实时监控-场地人员出勤计划
 */
public interface WorkStationAttendPlanManager {

    /**
     * 根据businessKeys查询
     * @param data
     * @return
     */
    Result<WorkStationAttendPlan> queryByBusinessKeys(WorkStationAttendPlan data);
}
