package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.schedule.WorkGridSchedule;
import com.jdl.basic.api.domain.schedule.WorkGridScheduleRequest;
import com.jdl.jy.flat.dto.schedule.DataScheduleNatureDto;
import com.jdl.jy.flat.dto.schedule.ScheduleAggsDto;
import com.jdl.jy.flat.dto.schedule.ScheduleDetailDto;
import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.api.domain.user.JyUserQueryDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;

import java.util.List;

/**
 *  网格班次管理
 */
public interface WorkGridScheduleManager {

	List<WorkGridSchedule> queryByWorkGridKey(WorkGridScheduleRequest request);

	List<ScheduleDetailDto> findListByGridKeyAndErp(UserGridScheduleQueryDto request);

	List<ScheduleAggsDto> findListByGridKeyAndNature(DataScheduleNatureDto request);

    /**
     * 查询员工某天的排班记录
     * @param queryDto
     * @return
     */
    List<UserGridScheduleDto> getUserScheduleByCondition(UserGridScheduleQueryDto queryDto);
}
