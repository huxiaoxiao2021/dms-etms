package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 11:05
 * @Description: 波次发货
 */
@Service("jySendPredictAggsWriteService")
public class JySendPredictAggsWriteServiceImpl {

    @Autowired
    private JySendPredictAggsDao JySendPredictAggsDao;


    public Boolean insertOrUpdateJySendPredictAggs(JySendPredictAggsPO entity) {
        Boolean result = JySendPredictAggsDao.updateByBizProduct(entity) > 0;
        if (!result) {
            return JySendPredictAggsDao.insert(entity) > 0;
        }
        return result;
    }
}
