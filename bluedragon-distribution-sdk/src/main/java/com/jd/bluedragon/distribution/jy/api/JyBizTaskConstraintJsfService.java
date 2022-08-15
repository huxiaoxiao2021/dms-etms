package com.jd.bluedragon.distribution.jy.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dto.BizTaskConstraint;

public interface JyBizTaskConstraintJsfService {

    Result<BizTaskConstraint> getBizTaskConstraint(Long bizId, String taskType);

}
