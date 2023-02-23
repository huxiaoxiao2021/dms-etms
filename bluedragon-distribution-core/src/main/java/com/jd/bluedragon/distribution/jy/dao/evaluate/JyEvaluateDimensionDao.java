package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;

import java.util.List;
import java.util.Map;

public class JyEvaluateDimensionDao extends BaseDao<JyEvaluateDimensionEntity> {

    final static String NAMESPACE = JyEvaluateDimensionDao.class.getName();

    public List<JyEvaluateDimensionEntity> findAllDimension() {
        return this.getSqlSession().selectList(NAMESPACE + ".findAllDimension");
    }

    public Map<Integer, JyEvaluateDimensionEntity> findAllDimensionMap() {
        return this.getSqlSession().selectMap(NAMESPACE + ".findAllDimensionMap", "code");
    }

}
