package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;
import com.jd.bluedragon.distribution.jy.dao.calibrate.JyBizTaskMachineCalibrateDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运设备校准任务SERVICE
 *
 * @author hujiping
 * @date 2022/12/7 6:38 PM
 */
@Service("jyBizTaskMachineCalibrateService")
public class JyBizTaskMachineCalibrateServiceImpl implements JyBizTaskMachineCalibrateService {

    @Autowired
    private JyBizTaskMachineCalibrateDao jyBizTaskMachineCalibrateDao;

    @Override
    public int insert(JyBizTaskMachineCalibrateEntity entity) {
        return jyBizTaskMachineCalibrateDao.insert(entity);
    }

    @Override
    public int update(JyBizTaskMachineCalibrateEntity entity) {
        if(entity == null || StringUtils.isEmpty(entity.getMachineCode())){
            return 0;
        }
        return jyBizTaskMachineCalibrateDao.update(entity);
    }

    @Override
    public JyBizTaskMachineCalibrateEntity queryOneByCondition(JyBizTaskMachineCalibrateEntity entity) {
        if(StringUtils.isEmpty(entity.getMachineCode())){
            return null;
        }
        return jyBizTaskMachineCalibrateDao.queryOneByCondition(entity);
    }
}
