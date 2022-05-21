package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jyBizTaskSendVehicleService")
public class JyBizTaskSendVehicleServiceImpl implements JyBizTaskSendVehicleService{
    @Autowired
    JyBizTaskSendVehicleDao jyBizTaskSendVehicleDao;

    @Override
    public JyBizTaskSendVehicleEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDao.findByBizId(bizId);
    }

    @Override
    public int saveSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.insert(entity);
    }

    @Override
    public int updateSendVehicleTask(JyBizTaskSendVehicleEntity entity) {
        return jyBizTaskSendVehicleDao.updateByBizId(entity);
    }
}
