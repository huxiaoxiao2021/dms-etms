package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateDimensionEntity;
import com.jd.etms.framework.utils.cache.annotation.Cache;

import java.util.List;

public class JyEvaluateDimensionDao extends BaseDao<JyEvaluateDimensionEntity> {

    final static String NAMESPACE = JyEvaluateDimensionDao.class.getName();


    @Cache(key = "JyEvaluateDimensionDao.findAllDimension", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public List<JyEvaluateDimensionEntity> findAllDimension() {
        return this.getSqlSession().selectList(NAMESPACE + ".findAllDimension");
    }

}
