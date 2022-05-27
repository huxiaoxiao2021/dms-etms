package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendCodeDao;

import java.util.List;

public class JyVehicleSendRelationServiceImpl implements JyVehicleSendRelationService{
    JySendCodeDao jySendCodeDao;

    @Override
    public List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId) {
        return jySendCodeDao.querySendCodesByVehicleDetailBizId(vehicleDetailBizId);
    }
}
