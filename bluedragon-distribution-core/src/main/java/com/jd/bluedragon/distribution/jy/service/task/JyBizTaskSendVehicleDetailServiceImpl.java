package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskSendVehicleDetailDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JyBizTaskSendVehicleDetailServiceImpl implements JyBizTaskSendVehicleDetailService{
    @Autowired
    JyBizTaskSendVehicleDetailDao jyBizTaskSendVehicleDetailDao;
    @Override
    public JyBizTaskSendVehicleDetailEntity findByBizId(String bizId) {
        return jyBizTaskSendVehicleDetailDao.findByBizId(bizId);
    }

    @Override
    public int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity) {
        return jyBizTaskSendVehicleDetailDao.updateDateilTaskByVehicleBizId(detailEntity);
    }
}
