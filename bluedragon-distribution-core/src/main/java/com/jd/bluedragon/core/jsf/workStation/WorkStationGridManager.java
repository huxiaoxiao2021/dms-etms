package com.jd.bluedragon.core.jsf.workStation;

import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.common.utils.Result;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/15 15:09
 * @Description:三定 场地网格工序管理
 */
public interface WorkStationGridManager {

    /**
     * 根据业务主键查询
     * @param data
     * @return
     */
    Result<WorkStationGrid> queryByBusinessKey(WorkStationGrid data);

    /**
     * 根据gridKey查询
     * @param workStationGridCheckQuery
     * @return
     */
    Result<WorkStationGrid> queryByGridKey(WorkStationGridQuery workStationGridCheckQuery);
}
