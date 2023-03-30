package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.*;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.bluedragon.distribution.jy.send.JySendVehicleProductType;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("jySendProductAggsService")
public class JySendProductAggsServiceImpl implements JySendProductAggsService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JySendProductAggsSpecialDao jySendProductAggsSpecialDao;

    @Override
    public List<JySendVehicleProductType> getSendVehicleProductTypeList(JySendProductAggsEntityQuery query) {

        JySendProductAggsDaoStrategy jySendProductAggsDao = jySendProductAggsSpecialDao.getJySendProductAggsDao();
        String keyword = jySendProductAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendProductAggsServiceImpl"+keyword+".getSendVehicleProductTypeList");
        List<JySendVehicleProductType> list = jySendProductAggsDao.getSendVehicleProductTypeList(query);
        Profiler.registerInfoEnd(info);
        return list;
    }

    @Override
    public Long getToScanCountSum(String sendVehicleBizId) {
        JySendProductAggsDaoStrategy jySendProductAggsDao = jySendProductAggsSpecialDao.getJySendProductAggsDao();
        String keyword = jySendProductAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendProductAggsServiceImpl"+keyword+".getToScanCountSum");
        Long toScanCountSum = jySendProductAggsDao.getToScanCountSum(sendVehicleBizId);
        Profiler.registerInfoEnd(info);
        return toScanCountSum;
    }

    @Override
    public Boolean insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity) {
        return jySendProductAggsSpecialDao.insertOrUpdateJySendProductAggsMain(entity);
    }

    @Override
    public Boolean insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity) {
        return jySendProductAggsSpecialDao.insertOrUpdateJySendProductAggsBak(entity);
    }

}