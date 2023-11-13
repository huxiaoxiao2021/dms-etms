package com.jd.bluedragon.distribution.jy.api.userSign;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.userSign.UserJobType;

import java.util.List;

public interface UserSignJsfService {
    InvokeResult<List<UserJobType>> getJobTypes();
}
