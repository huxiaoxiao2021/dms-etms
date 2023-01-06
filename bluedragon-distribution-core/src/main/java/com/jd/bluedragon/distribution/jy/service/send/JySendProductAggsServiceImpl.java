package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendProductAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendProductAggsService")
public class JySendProductAggsServiceImpl implements JySendProductAggsService {

    @Autowired
    private JySendProductAggsDao jySendProductAggsDao;


    @Override
    public JySendProductAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        return jySendProductAggsDao.getVehicleSendStatistics(sendVehicleBizId);
    }

    @Override
    public List<JySendProductAggsEntity> findBySendVehicleBizId(String sendVehicleBizId) {
        return jySendProductAggsDao.findBySendVehicleBizId(sendVehicleBizId);
    }

    @Override
    public List<JySendVehicleProductType> getSendVehicleProductTypeList(String sendVehicleBizId) {
        return jySendProductAggsDao.getSendVehicleProductTypeList(sendVehicleBizId);
    }

    @Override
    public Long getToScanCountSum(String sendVehicleBizId) {
        return jySendProductAggsDao.getToScanCountSum(sendVehicleBizId);
    }


}