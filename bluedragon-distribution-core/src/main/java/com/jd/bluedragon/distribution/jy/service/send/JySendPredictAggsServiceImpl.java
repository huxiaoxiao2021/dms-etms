package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsSpecialDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
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
    public Boolean insertOrUpdateJySendPredictAggsMain(JySendPredictAggsPO entity) {
        return jySendPredictAggsSpecialDao.insertOrUpdateJySendPredictAggsMain(entity);
    }

    @Override
    public Boolean insertOrUpdateJySendPredictAggsBak(JySendPredictAggsPO entity) {
        return jySendPredictAggsSpecialDao.insertOrUpdateJySendPredictAggsBak(entity);
    }
}
