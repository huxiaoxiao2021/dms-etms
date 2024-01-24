package com.jd.bluedragon.core.jsf.workStation.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.schedule.WorkGridSchedule;
import com.jdl.basic.api.domain.schedule.WorkGridScheduleRequest;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.basic.api.service.schedule.WorkGridScheduleJsfService;
import com.jdl.basic.api.service.user.UserJsfService;
import com.jdl.jy.flat.api.schedule.ScheduleJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 网格班次管理
 */
@Slf4j
@Service
public class WorkGridScheduleManagerImpl implements WorkGridScheduleManager {
    @Autowired
    private ScheduleJSFService scheduleJSFService;

    @Override
    public UserGridScheduleDto getUserScheduleByCondition(UserGridScheduleQueryDto queryDto) {
        ServiceResult<UserGridScheduleDto> result = scheduleJSFService.getScheduleByCondition(queryDto);
        if (result == null) {
            log.error("WorkGridScheduleManagerImpl.getUserScheduleByCondition 返回结果为空！");
            return null;
        }
        if (result.retFail()) {
            log.error("WorkGridScheduleManagerImpl.getUserScheduleByCondition 调用失败  message{}！", result.getMessage());
            return null;
        }
        if (result.getData() == null) {
            log.error("WorkGridScheduleManagerImpl.getUserScheduleByCondition 返回data为null");
            return null;
        }
        return result.getData();
    }
}
