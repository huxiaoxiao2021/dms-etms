package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.jy.dto.BizTaskConstraint;
import com.jd.bluedragon.distribution.jy.dto.JyBizTaskMessage;

public interface BizTaskService {

    BizTaskConstraint bizConstraintAssemble(String bizId);

    void bizTaskNotify(JyBizTaskMessage message);
}
