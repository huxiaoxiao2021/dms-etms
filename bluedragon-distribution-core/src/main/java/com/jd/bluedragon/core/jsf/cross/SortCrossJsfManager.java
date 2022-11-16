package com.jd.bluedragon.core.jsf.cross;

import com.jdl.basic.api.domain.cross.CrossDataJsfResp;
import com.jdl.basic.api.domain.cross.CrossPageQuery;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import com.jdl.basic.api.domain.cross.TableTrolleyQuery;
import com.jdl.basic.common.utils.Result;

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
}
