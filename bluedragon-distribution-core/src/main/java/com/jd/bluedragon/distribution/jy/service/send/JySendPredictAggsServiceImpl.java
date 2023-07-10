package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsSpecialDao;
import com.jd.bluedragon.distribution.jy.dao.send.JySendProductAggsDaoStrategy;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntityQuery;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 11:05
 * @Description:
 */

@Service("jySendPredictAggsService")
public class JySendPredictAggsServiceImpl implements JySendPredictAggsService{

    @Autowired
    private JySendPredictAggsSpecialDao jySendPredictAggsSpecialDao;

    @Override
    public Long getToScanCountSum(JySendPredictAggsRequest query) {
        JySendPredictAggsDaoStrategy jySendPredictAggsDao = jySendPredictAggsSpecialDao.getJySendPredictAggsDao();
        String keyword = jySendPredictAggsDao.getClass().getSimpleName();
        CallerInfo info = ProfilerHelper.registerInfo("DMSWEB.JySendPredictAggsServiceImpl"+keyword+".getToScanCountSum");
        Long toScanCountSum = jySendPredictAggsDao.getunScanCountByCondition(query);
        Profiler.registerInfoEnd(info);
        return toScanCountSum;
    }

    @Override
    public Boolean insertOrUpdateJySendPredictAggsMain(JySendPredictAggsPO entity) {
        return jySendPredictAggsSpecialDao.insertOrUpdateJySendPredictAggsMain(entity);
    }

    @Override
    public Boolean insertOrUpdateJySendPredictAggsBak(JySendPredictAggsPO entity) {
        return jySendPredictAggsSpecialDao.insertOrUpdateJySendPredictAggsBak(entity);
    }
}
