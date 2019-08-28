package com.jd.bluedragon.distribution.collect.dao;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceDao
 * @Description: 集货位表--Dao接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsPlaceDao extends Dao<CollectGoodsPlace> {


    int findPlaceNotEmpty(CollectGoodsPlace collectGoodsPlace);

    boolean updateStatus(CollectGoodsPlace collectGoodsPlace);

    CollectGoodsPlace findAbnormalPlace(CollectGoodsPlace collectGoodsPlace);

    CollectGoodsPlace findPlaceByCode(CollectGoodsPlace collectGoodsPlace);

    List<CollectGoodsPlace> findPlaceByAreaCode(CollectGoodsPlace collectGoodsPlace);
}
