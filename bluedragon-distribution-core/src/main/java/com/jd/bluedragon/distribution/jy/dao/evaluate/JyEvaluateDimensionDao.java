package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.common.annotation.CacheMethod;

import java.util.List;

public class JyEvaluateDimensionDao extends BaseDao<JyEvaluateDimensionEntity> {

    final static String NAMESPACE = JyEvaluateDimensionDao.class.getName();

    @CacheMethod(key = "JyEvaluateDimensionDao.findAllDimension", cacheBean = "redisCache", timeout = 1000 * 60 * 10)
    public List<JyEvaluateDimensionEntity> findAllDimension() {
        return this.getSqlSession().selectList(NAMESPACE + ".findAllDimension");
    }

}
