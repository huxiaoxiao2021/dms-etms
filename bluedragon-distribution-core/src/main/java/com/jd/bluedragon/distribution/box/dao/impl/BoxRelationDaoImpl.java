package com.jd.bluedragon.distribution.box.dao.impl;

import com.jd.bluedragon.distribution.box.dao.BoxRelationDao;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.domain.BoxRelationQ;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BoxRelationDaoImpl
 * @Description
 * @Author wyh
 * @Date 2020/12/14 16:10
 **/
public class BoxRelationDaoImpl extends BaseDao<BoxRelation> implements BoxRelationDao {

    @Override
    public List<BoxRelation> queryBoxRelation(BoxRelation relation) {
        return sqlSession.selectList(this.nameSpace + ".queryBoxRelation", relation);
    }

    @Override
    public int updateByUniqKey(BoxRelation relation) {
        return sqlSession.update(this.nameSpace + ".updateByUniqKey", relation);
    }

    @Override
    public int countByBoxCode(BoxRelation relation) {
        return sqlSession.selectOne(this.nameSpace + ".countByBoxCode", relation);
    }

    @Override
    public int countByQuery(BoxRelationQ query) {
        return (Integer)sqlSession.selectOne(getNameSpace() + ".pageNum_queryByPagerCondition", query);
    }

    @Override
    public List<BoxRelation> getByBoxCode(String boxCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("boxCode", boxCode);
        return sqlSession.selectList(this.nameSpace + ".getByBoxCode", param);
    }
}
