package com.jd.bluedragon.core.jsf.workStation;

import java.util.List;

import com.jd.bluedragon.common.domain.DockCodeAndPhone;
import com.jd.bluedragon.common.domain.DockCodeAndPhoneQuery;
import com.jdl.basic.api.domain.workStation.WorkStationGrid;
import com.jdl.basic.api.domain.workStation.WorkStationGridCountVo;
import com.jdl.basic.api.domain.workStation.WorkStationGridQuery;
import com.jdl.basic.common.utils.PageDto;
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

    /**
     * 分页查询
     * @param workStationGridQuery 查询入参
     * @return 分页结果
     */
    Result<PageDto<WorkStationGrid>> queryPageList(WorkStationGridQuery workStationGridQuery);

    /**
     * 按条件查询统计信息
     * @param workStationGridQuery 查询入参
     * @return 查询结果
     */
    Result<Long> queryCount(WorkStationGridQuery workStationGridQuery);
    
    /**
     * 线上化任务扫描-查询站点列表
     * @param workStationGridQuery
     * @return
     */
	List<Integer> querySiteListForManagerScan(WorkStationGridQuery workStationGridQuery);
    /**
     * 线上化站点任务扫描-查询站点下网格列表
     * @param workStationGridQuery
     * @return
     */
	List<WorkStationGrid> queryListForManagerSiteScan(WorkStationGridQuery workStationGridQuery);

    /**
     * 获取运输月台号和联系人
     * @param dockCodeAndPhoneQuery
     * @return Result<DockCodeAndPhone>
     */
    Result<DockCodeAndPhone> queryDockCodeAndPhone(DockCodeAndPhoneQuery dockCodeAndPhoneQuery);
}
