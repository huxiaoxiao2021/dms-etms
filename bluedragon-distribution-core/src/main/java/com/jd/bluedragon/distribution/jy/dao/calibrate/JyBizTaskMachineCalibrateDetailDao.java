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

    public int closeCalibrateDetailById(JyBizTaskMachineCalibrateDetailEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".closeCalibrateDetailById", entity);
    }

    public JyBizTaskMachineCalibrateDetailEntity selectLatelyOneByMachineCode(JyBizTaskMachineCalibrateDetailEntity condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectLatelyOneByMachineCode", condition);
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

    public List<JyBizTaskMachineCalibrateDetailEntity> queryCurrentTaskDetail(JyBizTaskMachineCalibrateCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryCurrentTaskDetail", condition);
    }

    public int duplicateNewestTaskDetail(JyBizTaskMachineCalibrateDetailEntity deleteEntity){
        return this.getSqlSession().update(NAMESPACE + ".duplicateNewestTaskDetail", deleteEntity);
    }

    public Integer selectCountForTask(JyBizTaskMachineCalibrateCondition condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectCountForTask", condition);
    }

    public List<JyBizTaskMachineCalibrateDetailEntity> selectByConditionForTask(JyBizTaskMachineCalibrateCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectByConditionForTask", condition);
    }

    public JyBizTaskMachineCalibrateDetailEntity selectById(Long taskId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectById", taskId);
    }
}
