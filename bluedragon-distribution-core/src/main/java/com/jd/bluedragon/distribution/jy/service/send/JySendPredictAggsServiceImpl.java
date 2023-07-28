package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.dbrouter.SendAggsChangeDataSources;
import com.jd.bluedragon.distribution.jy.dao.send.JySendPredictAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsRequest;
import com.jd.bluedragon.distribution.jy.send.JySendPredictProductType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/6 11:05
 * @Description:
 */

@Service("jySendPredictAggsService")
public class JySendPredictAggsServiceImpl implements JySendPredictAggsService{

    @Autowired
    private JySendPredictAggsDao JySendPredictAggsDao;


    @Override
    @SendAggsChangeDataSources
    public Long getToScanCountSum(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getunScanSumByCondition(query);
    }

    @Override
    @SendAggsChangeDataSources
    public List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getSendPredictProductTypeList(query);
    }

    @Override
    public Boolean insertOrUpdateJySendPredictAggs(JySendPredictAggsPO entity) {
        Boolean result = JySendPredictAggsDao.updateByBizProduct(entity) >0;
        if(!result){
            return JySendPredictAggsDao.insert(entity) > 0;
        }
        return result;
    }
}
