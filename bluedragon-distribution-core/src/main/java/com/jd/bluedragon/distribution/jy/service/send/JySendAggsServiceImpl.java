package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendAggsService")
public class JySendAggsServiceImpl implements JySendAggsService {
    @Autowired
    JySendAggsDao jySendAggsDao;


    @Override
    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        return jySendAggsDao.getVehicleSendStatistics(sendVehicleBizId);
    }

    @Override
    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        return jySendAggsDao.findBySendVehicleBiz(sendVehicleBizId);
    }
}