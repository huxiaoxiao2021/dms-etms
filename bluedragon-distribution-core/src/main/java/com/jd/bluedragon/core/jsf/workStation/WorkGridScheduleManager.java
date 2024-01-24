package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;

public interface WorkGridScheduleManager {
    UserGridScheduleDto getUserScheduleByCondition(UserGridScheduleQueryDto queryDto);
}
