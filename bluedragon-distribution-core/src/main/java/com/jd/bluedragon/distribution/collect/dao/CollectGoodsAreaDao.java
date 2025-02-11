package com.jd.bluedragon.distribution.collect.dao;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsAreaDao
 * @Description: 集货区表--Dao接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsAreaDao extends Dao<CollectGoodsArea> {

    int findExistByAreaCode(CollectGoodsArea e);

    List<CollectGoodsArea> findBySiteCode(CollectGoodsArea e);

    int deleteByCode(Integer createSiteCode, List<String> codes);
}
