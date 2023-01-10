package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.*;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
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
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;

    @Override
    public List<JySendVehicleProductType> getSendVehicleProductTypeList(String sendVehicleBizId) {

        JySendProductAggsDaoStrategy jySendProductAggsDao = getJySendProductAggsDao();
        String keyword = jySendProductAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendProductAggsServiceImpl"+keyword+".getSendVehicleProductTypeList");
        List<JySendVehicleProductType> list = jySendProductAggsDao.getSendVehicleProductTypeList(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return list;
    }

    @Override
    public Long getToScanCountSum(String sendVehicleBizId) {
        JySendProductAggsDaoStrategy jySendProductAggsDao = getJySendProductAggsDao();
        String keyword = jySendProductAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendProductAggsServiceImpl"+keyword+".getToScanCountSum");
        Long toScanCountSum = jySendProductAggsDao.getToScanCountSum(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return toScanCountSum;
    }

    @Override
    public Boolean insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity) {
        Boolean result = jySendProductAggsDaoMain.updateByBizProduct(entity)>0;
        if(!result){
            return jySendProductAggsDaoMain.insert(entity)>0;
        }
        return result;
    }

    @Override
    public Boolean insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity) {
        Boolean result = jySendProductAggsDaoBak.updateByBizProduct(entity) >0;
        if(!result){
            return jySendProductAggsDaoBak.insert(entity) > 0;
        }
        return result;
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
        if(jyDuccConfigManager.getJySendAggOldOrNewDataReadSwitch()){
            if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                return jySendProductAggsDaoBak;
            }else {
                return jySendProductAggsDaoMain;
            }
        }
        return jySendProductAggsDao;
    }



}