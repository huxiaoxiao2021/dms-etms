package com.jd.bluedragon.distribution.collect.service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceCondition;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeService
 * @Description: 集货位类型表--Service接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsPlaceTypeService extends Service<CollectGoodsPlaceType> {

    boolean findExistByCreateSiteCode ( CollectGoodsPlaceType e);

    boolean initPlaceType(CollectGoodsPlaceType e);

    List<CollectGoodsPlaceType> findByCreateSiteCode(CollectGoodsPlaceType e);

    boolean deleteByCreateSiteCode(CollectGoodsPlaceType e);

    List<CollectGoodsPlaceType> convert(CollectGoodsPlaceType collectGoodsPlaceType,int smallPackMinNum,int smallPackMaxNum,int smallWaybillMaxNum,
                                        int middlePackMinNum,int middlePackMaxNum,int middleWaybillMaxNum,
                                        int bigPackMinNum,int bigPackMaxNum,int bigWaybillMaxNum);

    boolean savePalceTypeByCollectGoodsPlace(CollectGoodsPlaceCondition condition);
}
