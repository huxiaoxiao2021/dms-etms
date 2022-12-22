package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateCondition;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.calibrate.JyBizTaskMachineCalibrateDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/12/7 6:39 PM
 */
@Service("jyBizTaskMachineCalibrateDetailService")
public class JyBizTaskMachineCalibrateDetailServiceImpl implements JyBizTaskMachineCalibrateDetailService {

    @Autowired
    private JyBizTaskMachineCalibrateDetailDao jyBizTaskMachineCalibrateDetailDao;

    @Override
    public int insert(JyBizTaskMachineCalibrateDetailEntity entity) {
        return jyBizTaskMachineCalibrateDetailDao.insert(entity);
    }

    @Override
    public int update(JyBizTaskMachineCalibrateDetailEntity entity) {
        return jyBizTaskMachineCalibrateDetailDao.update(entity);
    }

    @Override
    public int updateMachineStatus(JyBizTaskMachineCalibrateDetailEntity entity) {
        return 0;
    }

    @Override
    public int updateTaskStatus(JyBizTaskMachineCalibrateDetailEntity entity) {
        return 0;
    }

    @Override
    public JyBizTaskMachineCalibrateDetailEntity selectLatelyOneByCondition(JyBizTaskMachineCalibrateCondition condition) {
        return jyBizTaskMachineCalibrateDetailDao.selectLatelyOneByCondition(condition);
    }

    @Override
    public List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateCondition condition) {
        return jyBizTaskMachineCalibrateDetailDao.selectByCondition(condition);
    }

    @Override
    public Integer selectCountForTask(JyBizTaskMachineCalibrateCondition condition) {
        return jyBizTaskMachineCalibrateDetailDao.selectCountForTask(condition);
    }

    @Override
    public List<JyBizTaskMachineCalibrateDetailEntity> selectByConditionForTask(JyBizTaskMachineCalibrateCondition condition) {
        return jyBizTaskMachineCalibrateDetailDao.selectByConditionForTask(condition);
    }

    @Override
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        jyBizTaskMachineCalibrateDetailDao.batchUpdateStatus(ids, status);
    }

    @Override
    public JyBizTaskMachineCalibrateDetailEntity queryCurrentTaskDetail(JyBizTaskMachineCalibrateCondition condition) {
        return jyBizTaskMachineCalibrateDetailDao.queryCurrentTaskDetail(condition);
    }

    @Override
    public int duplicateNewestTaskDetail(JyBizTaskMachineCalibrateDetailEntity entity) {
        return jyBizTaskMachineCalibrateDetailDao.duplicateNewestTaskDetail(entity);
    }

}
