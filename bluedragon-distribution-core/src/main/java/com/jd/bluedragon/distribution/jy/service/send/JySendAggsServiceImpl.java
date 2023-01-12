package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoMain;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDao;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAggsDaoBak;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntityQuery;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendAggsService")
public class JySendAggsServiceImpl implements JySendAggsService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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

    @Override
    public List<JySendAggsEntity> getSendAggsListByCondition(JySendAggsEntityQuery query) {
        return jySendAggsDao.getSendAggsListByCondition(query);
    }

    private JySendAggsDaoStrategy getJySendAggsDao(){
        if(jyDuccConfigManager.getJySendAggOldOrNewDataReadSwitch()){
            log.info("getJySendAggsDao-JySendAggOldOrNewDataReadSwitch 读新库开启");
            if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                log.info("getJySendAggsDao--JySendAggsDataReadSwitch 读备库开启");
                return jySendAggsDaoBak;
            }else {
                log.info("getJySendAggsDao-JySendAggsDataReadSwitch 读主库开启");
                return jySendAggsDaoMain;
            }
        }
        log.info("getJySendAggsDao-JySendAggOldOrNewDataReadSwitch 关闭");
        return jySendAggsDao;
    }
}