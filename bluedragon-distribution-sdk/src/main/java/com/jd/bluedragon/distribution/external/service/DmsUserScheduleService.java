package com.jd.bluedragon.distribution.external.service;


import com.jd.bluedragon.distribution.api.request.sendcode.DmsUserScheduleRequest;
import com.jd.ql.dms.common.domain.JdResponse;

public interface DmsUserScheduleService {

    JdResponse<Boolean> allowEntryTurnStile(DmsUserScheduleRequest request);
}
