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
        int i = jySendProductAggsDaoMain.updateByBizProduct(entity);
        int j = 0;
        if(i == 0){
            jySendProductAggsDaoMain.insert(entity);
        }
        return i + j;
    }

    @Override
    public int insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity) {
        int i = jySendProductAggsDaoBak.updateByBizProduct(entity);
        int j = 0;
        if(i == 0){
            jySendProductAggsDaoBak.insert(entity);
        }
        return i + j;
    }

    @Override
    public List<JySendProductAggsEntity> getSendProductAggMainData(JySendProductAggsEntity query) {
        return jySendProductAggsDaoMain.getSendProductAggMainData(query);
    }

    @Override
    public List<JySendProductAggsEntity> getSendProductAggBakData(JySendProductAggsEntity query) {
        return jySendProductAggsDaoBak.getSendProductAggBakData(query);
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