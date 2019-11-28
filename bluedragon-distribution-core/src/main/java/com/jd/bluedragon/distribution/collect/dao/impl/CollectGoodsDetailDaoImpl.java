package com.jd.bluedragon.distribution.collect.dao.impl;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsDetailDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsDetailDaoImpl
 * @Description: --Dao接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Repository("collectGoodsDetailDao")
public class CollectGoodsDetailDaoImpl extends BaseDao<CollectGoodsDetail> implements CollectGoodsDetailDao {


    @Override
    public int deleteByCollectGoodsDetail(CollectGoodsDetail CollectGoodsDetail) {
        return this.sqlSession.delete(this.nameSpace+".deleteByCollectGoodsDetail",CollectGoodsDetail);
    }

    @Override
    public boolean transfer(String sourcePlaceCode, String targetPlaceCode,Integer targetPlaceType, Integer createSiteCode, String waybillCode) {

        CollectGoodsDetailCondition updateParam = new CollectGoodsDetailCondition();
        updateParam.setCollectGoodsPlaceCode(sourcePlaceCode);
        updateParam.setCreateSiteCode(createSiteCode);
        updateParam.setWaybillCode(waybillCode);
        updateParam.setTargetCollectGoodsPlaceCode(targetPlaceCode);
        updateParam.setCollectGoodsPlaceType(targetPlaceType);
        if(sourcePlaceCode == null && waybillCode == null){
            return false;
        }
        return this.sqlSession.update(this.nameSpace+".transfer",updateParam)>0;
    }

    @Override
    public int findCount(CollectGoodsDetail collectGoodsDetail) {
        return this.sqlSession.selectOne(this.nameSpace+".findCount",collectGoodsDetail);
    }

    @Override
    public List<CollectGoodsDetailCondition> findSacnWaybill(CollectGoodsDetail collectGoodsDetail) {
        return this.sqlSession.selectList(this.nameSpace+".findSacnWaybill",collectGoodsDetail);
    }

    @Override
    public List<CollectGoodsDetail> queryByCondition(CollectGoodsDetailCondition collectGoodsDetailCondition) {
        return this.sqlSession.selectList(this.nameSpace+".queryByCondition",collectGoodsDetailCondition);
    }

    @Override
    public CollectGoodsDetail findCollectGoodsDetailByPackageCode(String packageCode){
        return this.sqlSession.selectOne(this.nameSpace+".findCollectGoodsDetailByPackageCode",packageCode);
    }
}
