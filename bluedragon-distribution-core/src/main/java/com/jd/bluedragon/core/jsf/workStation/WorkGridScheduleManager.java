package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.schedule.WorkGridSchedule;
import com.jdl.basic.api.domain.schedule.WorkGridScheduleRequest;
import com.jdl.jy.flat.dto.schedule.DataScheduleNatureDto;
import com.jdl.jy.flat.dto.schedule.ScheduleAggsDto;
import com.jdl.jy.flat.dto.schedule.ScheduleDetailDto;
import com.jdl.jy.flat.dto.schedule.UserGridScheduleQueryDto;

import java.util.List;

/**
 *  网格班次管理
 */
public interface WorkGridScheduleManager {

	List<WorkGridSchedule> queryByWorkGridKey(WorkGridScheduleRequest request);

	List<ScheduleDetailDto> findListByGridKeyAndErp(UserGridScheduleQueryDto request);

	List<ScheduleAggsDto> findListByGridKeyAndNature(DataScheduleNatureDto request);

}
