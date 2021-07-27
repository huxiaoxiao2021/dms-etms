package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.working.WorkingClockDataJsfService;
import com.jd.dms.wb.report.api.working.dto.WorkingClockDTO;
import com.jd.dms.wb.report.api.working.dto.WorkingClockRecordPdaVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class WorkingClockDataManagerImpl implements WorkingClockDataManager {
    @Resource
    private WorkingClockDataJsfService workingClockDataJsfService;

    @Override
    public BaseEntity<Pager<WorkingClockRecordPdaVO>> pdaLocalClockRecord(WorkingClockDTO dto) {
        BaseEntity<Pager<WorkingClockRecordPdaVO>> entity = workingClockDataJsfService.pdaLocalClockRecord(dto);
        return entity;
    }

    @Override
    public BaseEntity<Pager<WorkingClockRecordPdaVO>> pdaGroupMembersClockRecord(WorkingClockDTO dto) {
        BaseEntity<Pager<WorkingClockRecordPdaVO>> entity = workingClockDataJsfService.pdaGroupMembersClockRecord(dto);
        return entity;
    }

    @Override
    public BaseEntity<Object> clock(WorkingClockDTO dto) {
        BaseEntity<Object> entity = workingClockDataJsfService.clock(dto);
        return entity;
    }

    @Override
    public BaseEntity<Object> disabledClock(WorkingClockDTO dto) {
        BaseEntity<Object> entity = workingClockDataJsfService.disabledClock(dto);
        return entity;
    }

    @Override
    public BaseEntity<Object> updateClockTime(WorkingClockDTO dto) {
        BaseEntity<Object> entity = workingClockDataJsfService.updateClockTime(dto);
        return entity;
    }

    @Override
    public BaseEntity<Object> confirmClockHours(WorkingClockDTO dto) {
        BaseEntity<Object> entity = workingClockDataJsfService.confirmClockHours(dto);
        return entity;
    }
}
