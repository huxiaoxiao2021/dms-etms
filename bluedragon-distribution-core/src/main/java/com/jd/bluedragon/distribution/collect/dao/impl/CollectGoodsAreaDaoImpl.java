package com.jd.bluedragon.distribution.collect.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsAreaDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: CollectGoodsAreaDaoImpl
 * @Description: 集货区表--Dao接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Repository("collectGoodsAreaDao")
public class CollectGoodsAreaDaoImpl extends BaseDao<CollectGoodsArea> implements CollectGoodsAreaDao {


    @Override
    public int findExistByAreaCode(CollectGoodsArea e) {
        return sqlSession.selectOne(this.nameSpace+".findExistByAreaCode", e);
    }

    @Override
    public List<CollectGoodsArea> findBySiteCode(CollectGoodsArea e) {
        return sqlSession.selectList(this.nameSpace+".findBySiteCode", e);
    }

    @Override
    public int deleteByCode(Integer createSiteCode, List<String> codes) {
        Map<String, Object> map = new HashMap<>();
        map.put("createSiteCode", createSiteCode);
        map.put("list", codes);
        return sqlSession.delete(this.nameSpace+".deleteByCode", map);
    }
}
