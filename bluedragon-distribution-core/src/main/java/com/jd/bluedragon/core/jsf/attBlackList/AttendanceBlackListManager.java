package com.jd.bluedragon.core.jsf.attBlackList;


import com.jdl.basic.api.domain.attBlackList.AttendanceBlackList;

import com.jdl.basic.common.utils.Result;

public interface AttendanceBlackListManager {

    Result<AttendanceBlackList>  queryByUserCode(String  userCode);

}
