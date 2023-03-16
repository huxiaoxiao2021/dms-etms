package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;

import java.util.List;

public class JyEvaluateRecordDao extends BaseDao<JyEvaluateRecordEntity> {

    final static String NAMESPACE = JyEvaluateRecordDao.class.getName();

    public int insertSelective(JyEvaluateRecordEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int batchInsert(List<JyEvaluateRecordEntity> dataList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", dataList);
    }

    public JyEvaluateRecordEntity findRecordBySourceBizId(String sourceBizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findRecordBySourceBizId", sourceBizId);
    }

    public List<JyEvaluateRecordEntity> findRecordsBySourceBizId(String sourceBizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".findRecordsBySourceBizId", sourceBizId);
    }

}
