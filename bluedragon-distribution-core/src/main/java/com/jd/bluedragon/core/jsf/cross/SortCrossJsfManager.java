package com.jd.bluedragon.core.jsf.cross;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jdl.basic.api.domain.cross.CrossDataJsfResp;
import com.jdl.basic.api.domain.cross.CrossPageQuery;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import com.jdl.basic.api.domain.cross.TableTrolleyQuery;

/**
 * @author liwenji
 * @date 2022-11-16 9:16
 */
public interface SortCrossJsfManager {

    /**
     * 分页查询场地滑道列表数据
     * @param query
     * @return
     */
    CrossDataJsfResp queryCrossDataByDmsCode(CrossPageQuery query);

    /**
     * 通过道口号分页查询笼车编号
     * @param query
     * @return
     */
    TableTrolleyJsfResp queryTableTrolleyListByCrossCode(TableTrolleyQuery query);

    /**
     * 通过场地分页查询笼车编号
     * @param query
     * @return
     */
    TableTrolleyJsfResp queryTableTrolleyListByDmsId(TableTrolleyQuery query);

    /**
     * 根据始发和目的地站点查询滑道笼车
     * @param query
     * @return
     */
    TableTrolleyJsfResp queryCTTByStartEndSiteCode(TableTrolleyQuery query);

    /**
     * 根据滑道笼车号获取流向信息
     * @param query
     * @return
     */
    TableTrolleyJsfResp queryCTTByCTTCode(TableTrolleyQuery query);

    /**
     * 根据场地流向查询滑道笼车信息
     */
    JdResult<TableTrolleyJsfResp> queryCrossCodeTableTrolleyBySiteFlow(TableTrolleyQuery query);

    /**
     * 根据滑道笼车号查询场地流向
     */
    JdResult<TableTrolleyJsfResp> querySiteFlowByCrossCodeTableTrolley(TableTrolleyQuery query);

    /**
     * 根据场地流向批量查询滑道笼车信息
     */
    JdResult<TableTrolleyJsfResp> queryCrossCodeTableTrolleyBySiteFlowList(TableTrolleyQuery query);

}
