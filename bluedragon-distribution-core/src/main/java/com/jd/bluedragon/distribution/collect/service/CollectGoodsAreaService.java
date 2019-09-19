package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsAreaService
 * @Description: 集货区表--Service接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsAreaService extends Service<CollectGoodsArea> {


    boolean findExistByAreaCode(CollectGoodsArea e);
    List<CollectGoodsArea> findBySiteCode(CollectGoodsArea e);

    boolean deleteByCode(List<String> codes);
}
