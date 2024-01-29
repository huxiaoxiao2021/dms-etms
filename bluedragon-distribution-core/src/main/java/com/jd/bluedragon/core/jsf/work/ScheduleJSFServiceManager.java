package com.jd.bluedragon.core.jsf.work;

import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.JyUserDto;
import com.jdl.jy.flat.dto.schedule.UserDateScheduleQueryDto;
import com.jdl.jy.flat.dto.schedule.UserScheduleDto;

public interface ScheduleJSFServiceManager {

    ServiceResult<UserScheduleDto> listUserDateSchedule(UserDateScheduleQueryDto dto);

}
