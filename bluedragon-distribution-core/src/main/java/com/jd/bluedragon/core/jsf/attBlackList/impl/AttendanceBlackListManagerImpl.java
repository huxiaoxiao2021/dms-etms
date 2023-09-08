package com.jd.bluedragon.core.jsf.attBlackList.impl;

import com.jd.bluedragon.core.jsf.attBlackList.AttendanceBlackListManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.attBlackList.AttendanceBlackList;
import com.jdl.basic.api.service.attBlackList.AttendanceBlackListJsfService;
import com.jdl.basic.common.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.bluedragon.Constants;
@Service("attendanceBlackListManagerImpl")
public class AttendanceBlackListManagerImpl implements AttendanceBlackListManager {
    @Autowired
    private AttendanceBlackListJsfService basicAttendanceBlackListJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "AttendanceBlackListManagerImpl.queryByUserCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public Result<AttendanceBlackList> queryByUserCode(String userCode) {
        return basicAttendanceBlackListJsfService.queryByUerCode(userCode);
    }
}
