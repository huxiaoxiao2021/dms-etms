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
@Service("jySendPredictAggsWriteService")
public class JySendPredictAggsWriteServiceImpl {

    @Autowired
    private JySendPredictAggsDao JySendPredictAggsDao;


    public Boolean insertOrUpdateJySendPredictAggs(JySendPredictAggsPO entity) {
        Boolean result = JySendPredictAggsDao.updateByBizProduct(entity) >0;
        if(!result){
            return JySendPredictAggsDao.insert(entity) > 0;
        }
        return result;
    }
}
