package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.*;
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
    private JySendAggsSpecialDao jySendAggsSpecialDao;

    @Override
    public JySendAggsEntity getVehicleSendStatistics(String sendVehicleBizId) {
        JySendAggsDaoStrategy jySendAggsDao = jySendAggsSpecialDao.getJySendAggsDao();
        String keyword = jySendAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendAggsServiceImpl"+keyword+".getVehicleSendStatistics");
        JySendAggsEntity entity = jySendAggsDao.getVehicleSendStatistics(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return entity;
    }

    @Override
    public List<JySendAggsEntity> findBySendVehicleBiz(String sendVehicleBizId) {
        JySendAggsDaoStrategy jySendAggsDao = jySendAggsSpecialDao.getJySendAggsDao();
        String keyword = jySendAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendAggsServiceImpl"+keyword+".findBySendVehicleBiz");
        List<JySendAggsEntity> list = jySendAggsDao.findBySendVehicleBiz(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return list;
    }

    @Override
    public Boolean insertOrUpdateJySendGoodsAggsMain(JySendAggsEntity entity) {
        return jySendAggsSpecialDao.insertOrUpdateJySendGoodsAggsMain(entity);
    }

    @Override
    public Boolean insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity) {
        return jySendAggsSpecialDao.insertOrUpdateJySendGoodsAggsBak(entity);
    }

    @Override
    public JySendAggsEntity findSendAggExistAbnormal(String sendVehicleBizId) {
        JySendAggsDaoStrategy jySendAggsDao = jySendAggsSpecialDao.getJySendAggsDao();
        String keyword = jySendAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendAggsServiceImpl"+keyword+".findSendAggExistAbnormal");
        JySendAggsEntity sendAggExistAbnormal = jySendAggsDao.findSendAggExistAbnormal(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return sendAggExistAbnormal;
    }
}