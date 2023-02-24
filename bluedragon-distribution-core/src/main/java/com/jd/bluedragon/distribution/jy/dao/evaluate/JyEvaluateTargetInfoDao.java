package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoQuery;
import org.apache.poi.hssf.record.formula.functions.Na;

import java.util.List;


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

    public List<JyEvaluateTargetInfoEntity> queryPageList(JyEvaluateTargetInfoQuery query) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryPageList", query);
    }

    public Long queryCount(JyEvaluateTargetInfoQuery query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryCount", query);
    }

    public JyEvaluateTargetInfoEntity queryInfoByTargetBizId(String businessId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryInfoByTargetBizId", businessId);
    }
}
