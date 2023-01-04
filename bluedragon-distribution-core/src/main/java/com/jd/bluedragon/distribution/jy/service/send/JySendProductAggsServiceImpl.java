package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.*;
import com.jd.bluedragon.distribution.jy.manager.JyDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendProductAggsService")
public class JySendProductAggsServiceImpl implements JySendProductAggsService {

    @Autowired
    private JySendProductAggsDao jySendProductAggsDao;

    @Autowired
    private JySendProductAggsDaoMain jySendProductAggsDaoMain;

    @Autowired
    private JySendProductAggsDaoBak jySendProductAggsDaoBak;

    @Autowired
    private JyDuccConfigManager jyDuccConfigManager;

    @Override
    public List<JySendVehicleProductType> getSendVehicleProductTypeList(String sendVehicleBizId) {
        return getJySendProductAggsDao().getSendVehicleProductTypeList(sendVehicleBizId);
    }

    @Override
    public Long getToScanCountSum(String sendVehicleBizId) {
        return getJySendProductAggsDao().getToScanCountSum(sendVehicleBizId);
    }

    @Override
    public int insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity) {
        return jySendProductAggsDaoMain.insertOrUpdate(entity);
    }

    @Override
    public int insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity) {
        return jySendProductAggsDaoBak.insertOrUpdate(entity);
    }

    /**
     * 获取具体的DAO
     * @return
     */
    private JySendProductAggsDaoStrategy getJySendProductAggsDao(){
        if(jyDuccConfigManager.getJySendProductAggsOldOrNewDataReadSwitch()){
            if (jyDuccConfigManager.getJySendProductAggsDataReadSwitch()){
                return jySendProductAggsDaoBak;
            }else {
                return jySendProductAggsDaoMain;
            }
        }
        return jySendProductAggsDao;
    }

}