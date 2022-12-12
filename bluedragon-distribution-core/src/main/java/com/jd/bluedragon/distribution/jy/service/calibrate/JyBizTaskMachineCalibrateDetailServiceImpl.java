package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.calibrate.JyBizTaskMachineCalibrateDetailDao;
import com.jd.bluedragon.distribution.jy.dto.calibrate.JyBizTaskMachineCalibrateQuery;
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
    public List<JyBizTaskMachineCalibrateDetailEntity> selectByCondition(JyBizTaskMachineCalibrateDetailEntity condition) {
        return jyBizTaskMachineCalibrateDetailDao.selectByCondition(condition);
    }

    @Override
    public JyBizTaskMachineCalibrateDetailEntity queryCurrentTaskDetail(JyBizTaskMachineCalibrateQuery query) {
        return jyBizTaskMachineCalibrateDetailDao.queryCurrentTaskDetail(query);
    }

    @Override
    public int duplicateNewestTaskDetail(JyBizTaskMachineCalibrateDetailEntity entity) {
        return jyBizTaskMachineCalibrateDetailDao.duplicateNewestTaskDetail(entity);
    }

    @Override
    public List<JyBizTaskMachineCalibrateDetailEntity> getMachineCalibrateDetail(JyBizTaskMachineCalibrateQuery query) {
        return jyBizTaskMachineCalibrateDetailDao.getMachineCalibrateDetail(query);
    }

}
