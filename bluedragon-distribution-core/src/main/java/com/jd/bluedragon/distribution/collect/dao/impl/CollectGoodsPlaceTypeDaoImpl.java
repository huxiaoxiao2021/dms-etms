package com.jd.bluedragon.distribution.collect.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceTypeDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeDaoImpl
 * @Description: 集货位类型表--Dao接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Repository("collectGoodsPlaceTypeDao")
public class CollectGoodsPlaceTypeDaoImpl extends BaseDao<CollectGoodsPlaceType> implements CollectGoodsPlaceTypeDao {


    @Override
    public int findExistByCreateSiteCode(CollectGoodsPlaceType e) {
        return sqlSession.selectOne(this.nameSpace+".findExistByCreateSiteCode", e);
    }

    @Override
    public List<CollectGoodsPlaceType> findByCreateSiteCode(CollectGoodsPlaceType e) {
        return sqlSession.selectList(this.nameSpace+".findByCreateSiteCode", e);
    }

    @Override
    public int deleteByCreateSiteCode(CollectGoodsPlaceType e) {
        return sqlSession.update(this.nameSpace+".deleteByCreateSiteCode", e);
    }

}
