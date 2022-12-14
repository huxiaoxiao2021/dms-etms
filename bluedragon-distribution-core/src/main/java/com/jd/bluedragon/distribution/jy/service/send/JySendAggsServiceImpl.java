package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.JySendAggsDaoInterface;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao2;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendAggsService")
public class JySendAggsServiceImpl implements JySendAggsService {

    @Autowired
    private UccPropertyConfiguration configuration;

    @Autowired
    JySendAggsDao jySendAggsDao;

    @Autowired
    JySendAggsDao2 jySendAggsDao2;


    @Override
    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        return getJySendAggsDao().getVehicleSendStatistics(sendVehicleBizId);
    }

    @Override
    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        return getJySendAggsDao().findBySendVehicleBiz(sendVehicleBizId);
    }

    @Override
    public int insertOrUpdateJySendGoodsAggs(JySendAggsEntity entity) {
        return jySendAggsDao.insertOrUpdate(entity);
    }

    private JySendAggsDaoInterface getJySendAggsDao(){
        if (configuration.getDataReadSwitch()){
            return jySendAggsDao2;
        }else {
            return jySendAggsDao;
        }
    }
}