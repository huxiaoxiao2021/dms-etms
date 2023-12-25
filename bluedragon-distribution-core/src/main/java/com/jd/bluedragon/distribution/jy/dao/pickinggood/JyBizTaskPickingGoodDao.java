package com.jd.bluedragon.distribution.jy.dao.pickinggood;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;

public class JyBizTaskPickingGoodDao extends BaseDao<JyBizTaskPickingGoodEntity> {

    private final static String NAMESPACE = JyBizTaskPickingGoodDao.class.getName();

    public JyBizTaskPickingGoodEntity findByBizIdWithYn(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizIdWithYn", entity);
    }

    public int insertSelective(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public int updateTaskByBizIdWithCondition(JyBizTaskPickingGoodEntityCondition entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateTaskByBizIdWithCondition", entity);
    }
    public int updateStatusByBizId(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateStatusByBizId", entity);
    }


    public JyBizTaskPickingGoodEntity findLatestEffectiveManualCreateTask(Long siteId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findLatestEffectiveManualCreateTask", siteId);
    }

    public JyBizTaskPickingGoodEntity findLatestTaskByBusinessNumber(String businessNumber) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findLatestTaskByBusinessNumber", businessNumber);
    }

    public int deleteByBusinessNumber(JyBizTaskPickingGoodEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByBusinessNumber", entity);
    }

//    int deleteByPrimaryKey(Long id);
//
//    int insert(JyBizTaskPickingGood record);
//
//
//    JyBizTaskPickingGood selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyBizTaskPickingGood record);
//
//    int updateByPrimaryKey(JyBizTaskPickingGood record);
}