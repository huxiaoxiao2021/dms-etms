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
@SendAggsChangeDataSources
@Service("jySendPredictAggsService")
public class JySendPredictAggsServiceImpl implements JySendPredictAggsService{

    @Autowired
    private JySendPredictAggsDao JySendPredictAggsDao;


    @Override
    public Long getToScanCountSum(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getunScanSumByCondition(query);
    }

    @Override
    public List<JySendPredictProductType> getSendPredictProductTypeList(JySendPredictAggsRequest query) {
        return JySendPredictAggsDao.getSendPredictProductTypeList(query);
    }
    
}
