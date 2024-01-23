package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;

public interface WorkGridScheduleManager {
    JyUserDto getUserByUserCode(JyUserQueryDto queryDto);

    UserGridScheduleDto getUserScheduleByCondition(UserGridScheduleQueryDto queryDto);
}
