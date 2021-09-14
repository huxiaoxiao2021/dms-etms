package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.working.dto.WorkingClockDTO;
import com.jd.dms.wb.report.api.working.dto.WorkingClockRecordPdaVO;

public interface WorkingClockDataManager {

    /**
     * PDA 查询在本小组签到的记录（含支援人员在本组的签到）
     */
    BaseEntity<Pager<WorkingClockRecordPdaVO>> pdaLocalClockRecord(WorkingClockDTO dto);

    /**
     * PDA 查询在本小组组员的签到记录（含本组组员支援其他组的签到记录）
     */
    BaseEntity<Pager<WorkingClockRecordPdaVO>>  pdaGroupMembersClockRecord(WorkingClockDTO dto);


    /**
     * PDA 打卡
     */
    BaseEntity<Object> clock(WorkingClockDTO dto);

    /**
     * 作废打卡记录
     */
    BaseEntity<Object> disabledClock(WorkingClockDTO dto);

    /**
     * 更新打卡时间
     */
    BaseEntity<Object> updateClockTime(WorkingClockDTO dto);

    /**
     * 小组工时确认
     */
    BaseEntity<Object> confirmClockHours(WorkingClockDTO dto);
}
