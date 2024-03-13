package com.jd.bluedragon.core.jsf.workStation.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.flat.api.schedule.ScheduleJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网格班次管理
 */
@Slf4j
@Service
public class WorkGridScheduleManagerImpl implements WorkGridScheduleManager {
    @Autowired
    private ScheduleJSFService scheduleJSFService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "WorkGridScheduleManagerImpl.getUserScheduleByCondition", mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public List<UserGridScheduleDto> getUserScheduleByCondition(UserGridScheduleQueryDto queryDto) {
        ServiceResult<List<UserGridScheduleDto>> result = scheduleJSFService.listScheduleByUserUniqueCodeAndDate(queryDto);
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
