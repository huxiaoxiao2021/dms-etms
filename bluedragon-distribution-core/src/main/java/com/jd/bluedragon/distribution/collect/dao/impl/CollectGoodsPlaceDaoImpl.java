package com.jd.bluedragon.distribution.collect.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CollectGoodsPlaceDaoImpl
 * @Description: 集货位表--Dao接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Repository("collectGoodsPlaceDao")
public class CollectGoodsPlaceDaoImpl extends BaseDao<CollectGoodsPlace> implements CollectGoodsPlaceDao {


    @Override
    public int findPlaceNotEmpty(CollectGoodsPlace collectGoodsPlace) {
        return sqlSession.selectOne(this.nameSpace+".findPlaceNotEmpty", collectGoodsPlace);
    }

    @Override
    public boolean updateStatus(CollectGoodsPlace collectGoodsPlace) {
        return sqlSession.update(this.nameSpace+".updateStatus", collectGoodsPlace)>0;
    }

    @Override
    public CollectGoodsPlace findAbnormalPlace(CollectGoodsPlace collectGoodsPlace) {
        return sqlSession.selectOne(this.nameSpace+".findAbnormalPlace", collectGoodsPlace);
    }

    @Override
    public CollectGoodsPlace findPlaceByCode(CollectGoodsPlace collectGoodsPlace) {
        return sqlSession.selectOne(this.nameSpace+".findPlaceByCode", collectGoodsPlace);
    }

    @Override
    public List<CollectGoodsPlace> findPlaceByAreaCode(CollectGoodsPlace collectGoodsPlace) {
        return sqlSession.selectList(this.nameSpace+".findPlaceByAreaCode", collectGoodsPlace);
    }

    @Override
    public int deleteByAreaCode(Integer createSiteCode, List<String> codes) {
        Map<String, Object> map = new HashMap<>();
        map.put("createSiteCode", createSiteCode);
        map.put("list", codes);
        return sqlSession.delete(this.nameSpace+".deleteByAreaCode", map);
    }
}
