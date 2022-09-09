package com.jd.bluedragon.core.jsf.workStation;


import com.jdl.basic.api.domain.workStation.WorkStation;
import com.jdl.basic.common.utils.Result;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/8/3 15:39
 * @Description: 三定网格工序管理
 */
public interface WorkStationManager {

    /**
     * 根据业务主键查询
     * @param data
     * @return
     */
    Result<WorkStation> queryByBusinessKey(WorkStation data);

    /**
     * 根据业务主键查询
     * @param businessKey
     * @return
     */
    Result<WorkStation> queryByRealBusinessKey(String businessKey);
}
