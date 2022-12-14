package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;

public interface PositionQueryJsfManager {

    Result<PositionDetailRecord> queryOneByPositionCode(String positionCode);

}
