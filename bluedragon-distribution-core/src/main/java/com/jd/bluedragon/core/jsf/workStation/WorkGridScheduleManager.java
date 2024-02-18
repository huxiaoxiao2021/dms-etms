package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;

import java.util.List;

public interface WorkGridScheduleManager {
    /**
     * 查询员工某天的排班记录
     * @param queryDto
     * @return
     */
    List<UserGridScheduleDto> getUserScheduleByCondition(UserGridScheduleQueryDto queryDto);
}
