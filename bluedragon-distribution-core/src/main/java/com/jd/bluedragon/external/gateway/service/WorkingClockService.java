package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.working.WorkingClockRecordVo;
import com.jd.bluedragon.common.dto.working.WorkingClockRequest;
import com.jd.bluedragon.common.dto.working.WorkingPage;

/**
 * 计提打卡
 */
public interface WorkingClockService {

    /**
     * PDA 查询在本小组签到的记录（含支援人员在本组的签到）
     */
    JdCResponse<WorkingPage<WorkingClockRecordVo>> pdaLocalClockRecord(WorkingClockRequest dto);

    /**
     * PDA 查询在本小组组员的签到记录（含本组组员支援其他组的签到记录）
     */
    JdCResponse<WorkingPage<WorkingClockRecordVo>>  pdaGroupMembersClockRecord(WorkingClockRequest dto);


    /**
     * PDA 打卡
     */
    JdCResponse<Object> clock(WorkingClockRequest dto);

    /**
     * 作废打卡记录
     */
    JdCResponse<Object> disabledClock(WorkingClockRequest dto);

    /**
     * 更新打卡时间
     */
    JdCResponse<Object> updateClockTime(WorkingClockRequest dto);

    /**
     * 小组工时确认
     */
    JdCResponse<Object> confirmClockHours(WorkingClockRequest dto);
}
