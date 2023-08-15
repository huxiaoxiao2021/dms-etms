package com.jd.bluedragon.core.jsf.workStation;

import java.util.List;

import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.domain.workStation.WorkGridQuery;
import com.jdl.basic.common.utils.Result;

/**
 * 三定 场地网格管理
 * @author wuyoude
 *
 */
public interface WorkGridManager {

    /**
     * 根据业务主键查询
     * @param businessKey
     * @return
     */
    Result<WorkGrid> queryByWorkGridKey(String businessKey);
    /**
     * 线上化任务扫描-查询站点列表
     * @param workGridQuery
     * @return
     */
	List<Integer> querySiteListForManagerScan(WorkGridQuery workGridQuery);
    /**
     * 线上化站点任务扫描-查询站点下网格列表
     * @param workGridQuery
     * @return
     */
	List<WorkGrid> queryListForManagerSiteScan(WorkGridQuery workGridQuery);
}
