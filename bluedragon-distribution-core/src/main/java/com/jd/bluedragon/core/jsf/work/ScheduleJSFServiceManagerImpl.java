package com.jd.bluedragon.core.jsf.work;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.flat.api.schedule.ScheduleJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.JyUserDto;
import com.jdl.jy.flat.dto.schedule.UserDateScheduleQueryDto;
import com.jdl.jy.flat.dto.schedule.UserScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("scheduleJSFServiceManager")
public class ScheduleJSFServiceManagerImpl implements ScheduleJSFServiceManager {

    @Autowired
    private ScheduleJSFService scheduleJSFService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "ScheduleJSFServiceManagerImpl.listUserDateSchedule",mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public ServiceResult<UserScheduleDto> listUserDateSchedule(UserDateScheduleQueryDto dto){
        return scheduleJSFService.listUserDateSchedule(dto);
    }
}
