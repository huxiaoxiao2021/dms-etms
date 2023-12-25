package com.jd.bluedragon.core.jsf.work;

import com.jdl.basic.api.domain.workStation.WorkGridFlowDirection;
import com.jdl.basic.api.domain.workStation.WorkGridFlowDirectionQuery;
import com.jdl.basic.common.utils.Result;

import java.util.List;

public interface WorkGridFlowDirectionManager {

    /**
     * 根据岗位码 线路类型 流向类型查询流向
     *
     * @param query@return
     */
    Result<List<WorkGridFlowDirection>> queryFlowByCondition(WorkGridFlowDirectionQuery query);

}
