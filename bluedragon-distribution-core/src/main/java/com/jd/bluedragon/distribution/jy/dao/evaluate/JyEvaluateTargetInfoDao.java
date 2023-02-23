package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;


public class JyEvaluateTargetInfoDao extends BaseDao<JyEvaluateTargetInfoEntity> {

    final static String NAMESPACE = JyEvaluateTargetInfoDao.class.getName();

    public int insertSelective(JyEvaluateTargetInfoEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateByPrimaryKeySelective(JyEvaluateTargetInfoEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", entity);
    }

    public JyEvaluateTargetInfoEntity findBySourceBizId(String sourceBizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findBySourceBizId", sourceBizId);
    }

}
