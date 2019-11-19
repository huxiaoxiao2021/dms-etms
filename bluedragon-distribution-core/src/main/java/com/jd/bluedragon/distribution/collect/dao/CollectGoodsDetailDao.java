package com.jd.bluedragon.distribution.collect.dao;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsDetailDao
 * @Description: --Dao接口
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
public interface CollectGoodsDetailDao extends Dao<CollectGoodsDetail> {

    int deleteByCollectGoodsDetail(CollectGoodsDetail collectGoodsDetail);

    boolean transfer(String sourcePlaceCode, String targetPlaceCode,Integer targetPlaceType, Integer createSiteCode, String waybillCode);

    int findCount(CollectGoodsDetail collectGoodsDetail);

    List<CollectGoodsDetailCondition> findSacnWaybill(CollectGoodsDetail collectGoodsDetail);

    List<CollectGoodsDetail> queryByCondition(CollectGoodsDetailCondition collectGoodsDetailCondition);

    CollectGoodsDetail findCollectGoodsDetailByPackageCode(String packageCode);
}
