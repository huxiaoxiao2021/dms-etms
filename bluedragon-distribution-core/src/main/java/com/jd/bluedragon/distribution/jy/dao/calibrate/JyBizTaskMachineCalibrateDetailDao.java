package com.jd.bluedragon.distribution.jy.dao.calibrate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateCondition;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public JyBizTaskMachineCalibrateDetailEntity selectLatelyOneByCondition(JyBizTaskMachineCalibrateDetailEntity condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectLatelyOneByCondition", condition);
    }

    public List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectByCondition", condition);
    }

    public int batchUpdateStatus(List<Long> ids, Integer taskStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        params.put("taskStatus", taskStatus);
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateStatus", params);
    }

    public JyBizTaskMachineCalibrateDetailEntity queryCurrentTaskDetail(JyBizTaskMachineCalibrateCondition condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryCurrentTaskDetail", condition);
    }

    public int duplicateNewestTaskDetail(JyBizTaskMachineCalibrateDetailEntity deleteEntity){
        return this.getSqlSession().update(NAMESPACE + ".duplicateNewestTaskDetail", deleteEntity);
    }


}
