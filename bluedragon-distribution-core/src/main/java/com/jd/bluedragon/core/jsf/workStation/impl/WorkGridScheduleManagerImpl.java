package com.jd.bluedragon.core.jsf.workStation.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.workStation.WorkGridScheduleManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.schedule.WorkGridSchedule;
import com.jdl.basic.api.domain.schedule.WorkGridScheduleRequest;
import com.jdl.basic.api.service.schedule.WorkGridScheduleJsfService;
import com.jdl.jy.flat.api.schedule.ScheduleJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.schedule.*;
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
    private WorkGridScheduleJsfService workGridScheduleJsfService;

    @Autowired
    private ScheduleJSFService scheduleJSFService;


    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridScheduleManagerImpl.queryByWorkGridKey", mState={JProEnum.TP,JProEnum.FunctionError})
	@Override
	public List<WorkGridSchedule> queryByWorkGridKey(WorkGridScheduleRequest request) {
    	List<WorkGridSchedule> result = new ArrayList<>();
        try {
            log.info("queryByWorkGridKey|网格班次查询:param={}", JSON.toJSONString(request));
            Result<List<WorkGridSchedule>> response = workGridScheduleJsfService.queryByWorkGridKey(request);
            if (response.isSuccess()) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("获取网格班次列表数据异常{}", e.getMessage(), e);
        }
        return result;
	}

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridScheduleManagerImpl.findListByGridKeyAndErp", mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public List<ScheduleDetailDto> findListByGridKeyAndErp(UserGridScheduleQueryDto request) {
        List<ScheduleDetailDto> result = new ArrayList<>();
        try {
            log.info("findListByGridKeyAndErp|网格排班计划查询:param={}", JSON.toJSONString(request));
            ServiceResult<List<ScheduleDetailDto>> response = scheduleJSFService.findListByGridKeyAndErp(request);
            if (response.retSuccess()) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("获取网格排班计划列表数据异常{}", e.getMessage(), e);
        }
        return result;
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WorkGridScheduleManagerImpl.findListByGridKeyAndNature", mState={JProEnum.TP,JProEnum.FunctionError})
    @Override
    public List<ScheduleAggsDto> findListByGridKeyAndNature(DataScheduleNatureDto request) {
        List<ScheduleAggsDto> result = new ArrayList<>();
        try {
            log.info("findListByGridKeyAndNature|网格排班工种人数查询:param={}", JSON.toJSONString(request));
            ServiceResult<List<ScheduleAggsDto>> response = scheduleJSFService.findListByGridKeyAndNature(request);
            if (response.retSuccess()) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("获取网格排班工种人数列表数据异常{}", e.getMessage(), e);
        }
        return result;
    }

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
