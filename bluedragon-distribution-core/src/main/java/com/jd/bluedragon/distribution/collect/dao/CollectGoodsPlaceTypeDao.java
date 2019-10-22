package com.jd.bluedragon.distribution.collect.dao;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeDao
 * @Description: 集货位类型表--Dao接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsPlaceTypeDao extends Dao<CollectGoodsPlaceType> {

    int findExistByCreateSiteCode(CollectGoodsPlaceType e);

    List<CollectGoodsPlaceType> findByCreateSiteCode(CollectGoodsPlaceType e);

    int deleteByCreateSiteCode(CollectGoodsPlaceType e);
}
