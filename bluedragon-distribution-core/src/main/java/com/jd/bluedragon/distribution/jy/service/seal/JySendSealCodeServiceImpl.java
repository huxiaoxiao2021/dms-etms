package com.jd.bluedragon.distribution.jy.service.seal;

import com.jd.bluedragon.distribution.jy.dao.send.JySendSealCodeDao;
import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class JySendSealCodeServiceImpl implements JySendSealCodeService{
    @Autowired
    JySendSealCodeDao jySendSealCodeDao;

    @Override
    public List<String> selectSealCodeByBizId(String bizId) {
        return jySendSealCodeDao.selectSealCodeByBizId(bizId);
    }

    @Override
    public int add(JySendSealCodeEntity entity) {
        return jySendSealCodeDao.insert(entity);
    }

    @Override
    public int addBatch(List<JySendSealCodeEntity> list) {
        return jySendSealCodeDao.insertBatch(list);
    }

    @Override
    public int countByBiz(String sendVehicleBiz) {
        return jySendSealCodeDao.countByBiz(sendVehicleBiz);
    }

    @Override
    public boolean deleteBySendVehicleBizId(JySendSealCodeEntity jySendSealCode) {
        return jySendSealCodeDao.deleteBySendVehicleBizId(jySendSealCode) > 0;
    }
}
