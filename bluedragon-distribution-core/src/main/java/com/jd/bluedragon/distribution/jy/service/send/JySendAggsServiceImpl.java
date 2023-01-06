package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoMain;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoBak;
import com.jd.bluedragon.distribution.jy.manager.JyDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendAggsService")
public class JySendAggsServiceImpl implements JySendAggsService {

    @Autowired
    private JyDuccConfigManager jyDuccConfigManager;

    @Autowired
    JySendAggsDao jySendAggsDao;

    @Autowired
    JySendAggsDaoMain jySendAggsDaoMain;

    @Autowired
    JySendAggsDaoBak jySendAggsDaoBak;




    @Override
    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        return getJySendAggsDao().getVehicleSendStatistics(sendVehicleBizId);
    }

    @Override
    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        return getJySendAggsDao().findBySendVehicleBiz(sendVehicleBizId);
    }

    @Override
    public int insertOrUpdateJySendGoodsAggsMain(JySendAggsEntity entity) {
        return jySendAggsDaoMain.insertOrUpdate(entity);
    }

    @Override
    public int insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity) {
        return jySendAggsDaoBak.insertOrUpdate(entity);
    }

    private JySendAggsDaoStrategy getJySendAggsDao(){
        if(jyDuccConfigManager.getJySendAggOldOrNewDataReadSwitch()){
            if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                return jySendAggsDaoBak;
            }else {
                return jySendAggsDaoMain;
            }
        }
        return jySendAggsDao;
    }
}