package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendAviationPlanEntity;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:05
 * @Description
 */
public class JyBizTaskSendAviationPlanDao extends BaseDao<JyBizTaskSendAviationPlanEntity> {

    private final static String NAMESPACE = JyBizTaskSendAviationPlanDao.class.getName();

    //    int deleteByPrimaryKey(Long id);
//
//    int insert(JyNizTaskSendAviationPlanEntity record);
//
    public int insertSelective(JyBizTaskSendAviationPlanEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public JyBizTaskSendAviationPlanEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public int updateByBizId(JyBizTaskSendAviationPlanEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateByBizId", entity);
    }

//
//    JyNizTaskSendAviationPlanEntity selectByPrimaryKey(Long id);
//
//    int updateByPrimaryKeySelective(JyNizTaskSendAviationPlanEntity record);
//
//    int updateByPrimaryKey(JyNizTaskSendAviationPlanEntity record);
}
