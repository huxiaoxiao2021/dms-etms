package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoMain;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoBak;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendAggsService")
public class JySendAggsServiceImpl implements JySendAggsService {

    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;

    @Autowired
    JySendAggsDao jySendAggsDao;

    @Autowired
    JySendAggsDaoMain jySendAggsDaoMain;

    @Autowired
    JySendAggsDaoBak jySendAggsDaoBak;


    @Override
    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        JySendAggsDaoStrategy jySendAggsDao = getJySendAggsDao();
        String keyword = jySendAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendAggsServiceImpl"+keyword+".getVehicleSendStatistics");
        JySendAggsEntity entity = jySendAggsDao.getVehicleSendStatistics(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        JySendAggsDaoStrategy jySendAggsDao = getJySendAggsDao();
        String keyword = jySendAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendAggsServiceImpl"+keyword+".findBySendVehicleBiz");
        List<JySendAggsEntity> list = jySendAggsDao.findBySendVehicleBiz(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return list;
    }

    @Override
    public Boolean insertOrUpdateJySendGoodsAggsMain(JySendAggsEntity entity) {
        Boolean result = jySendAggsDaoMain.updateByBizId(entity) > 0;
        if(!result){
            return jySendAggsDaoMain.insertBySendAggEntity(entity) > 0;
        }
        return result;
    }

    @Override
    public Boolean insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity) {
        Boolean result = jySendAggsDaoBak.updateByBizId(entity) > 0;
        if(!result){
           return jySendAggsDaoBak.insertBySendAggEntity(entity) >0;
        }
        return result;
    }

    @Override
    public List<JySendAggsEntity> getSendAggMainData(JySendAggsEntity query) {
        return jySendAggsDaoMain.getSendAggMainData(query);
    }

    @Override
    public List<JySendAggsEntity> getSendAggBakData(JySendAggsEntity query) {
        return jySendAggsDaoBak.getSendAggBakData(query);
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