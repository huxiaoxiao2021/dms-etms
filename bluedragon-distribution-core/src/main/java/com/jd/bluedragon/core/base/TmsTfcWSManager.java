package com.jd.bluedragon.core.base;

import com.jd.tms.tfc.dto.ScheduleCargoSimpleDto;
import com.jd.tms.tfc.dto.TransPlanScheduleCargoDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;

import java.util.List;

public interface TmsTfcWSManager {

    /**
     * 提供按照 发货地、目的地、计划发货日期（时间段）查询运单的接口
     *
     * @param cargoSimpleDto
     * @return
     */
    List<TransPlanScheduleCargoDto> getTransPlanScheduleCargoByCondition(ScheduleCargoSimpleDto cargoSimpleDto);

    /**
     * 根据派车任务明细简码获取派车任务明细
     *
     * @param simpleCode
     * @return
     * @throws Exception
     */
    TransWorkItemDto queryTransWorkItemBySimpleCode(String simpleCode) throws Exception;

}
