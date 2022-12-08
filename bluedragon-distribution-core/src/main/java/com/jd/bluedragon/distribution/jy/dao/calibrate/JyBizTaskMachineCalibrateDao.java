package com.jd.bluedragon.distribution.jy.dao.calibrate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/7 5:44 PM
 */
public class JyBizTaskMachineCalibrateDao extends BaseDao<JyBizTaskMachineCalibrateEntity> {

    private final static String NAMESPACE = JyBizTaskMachineCalibrateDao.class.getName();

    public int insert(JyBizTaskMachineCalibrateEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int update(JyBizTaskMachineCalibrateEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".update", entity);
    }

    public int delete(JyBizTaskMachineCalibrateEntity entity){
        return this.getSqlSession().delete(NAMESPACE + ".delete", entity);
    }

    public JyBizTaskMachineCalibrateEntity queryOneByCondition(JyBizTaskMachineCalibrateEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneByCondition", entity);
    }
}
