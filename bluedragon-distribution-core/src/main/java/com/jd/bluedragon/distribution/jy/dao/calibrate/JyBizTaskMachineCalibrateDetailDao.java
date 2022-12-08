package com.jd.bluedragon.distribution.jy.dao.calibrate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/7 5:45 PM
 */
public class JyBizTaskMachineCalibrateDetailDao extends BaseDao<JyBizTaskMachineCalibrateDetailEntity> {

    private final static String NAMESPACE = JyBizTaskMachineCalibrateDetailDao.class.getName();

    public int insert(JyBizTaskMachineCalibrateDetailEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int update(JyBizTaskMachineCalibrateDetailEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".update", entity);
    }

    public int delete(JyBizTaskMachineCalibrateDetailEntity entity){
        return this.getSqlSession().delete(NAMESPACE + ".delete", entity);
    }

    public List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateDetailEntity condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectByCondition", condition);
    }
}
