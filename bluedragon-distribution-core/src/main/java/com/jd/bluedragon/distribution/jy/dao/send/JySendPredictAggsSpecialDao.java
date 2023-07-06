package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendPredictAggsPO;
import com.jd.bluedragon.distribution.jy.send.JySendProductAggsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/21 20:16
 * @Description:
 */
@Service("jySendPredictAggsSpecialDao")
public class JySendPredictAggsSpecialDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private JySendPredictAggsDaoMain jySendPredictAggsDaoMain;

    @Autowired
    private JySendPredictAggsDaoBak jySendPredictAggsDaoBak;

    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;


    /**
     * 获取具体的DAO
     * @return
     */
    public  JySendPredictAggsDaoStrategy getJySendProductAggsDao(){

        if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
            log.info("getJySendProductAggs-JySendAggsDataReadSwitch 读备库开启");
            return jySendPredictAggsDaoBak;
        }
        log.info("getJySendProductAggs-JySendAggsDataReadSwitch 读主库开启");
        return jySendPredictAggsDaoMain;


    }

    public Boolean insertOrUpdateJySendPredictAggsMain(JySendPredictAggsPO entity) {
        Boolean result = jySendPredictAggsDaoMain.updateByBizProduct(entity)>0;
        if(!result){
            return jySendPredictAggsDaoMain.insert(entity)>0;
        }
        return result;
    }


    public Boolean insertOrUpdateJySendPredictAggsBak(JySendPredictAggsPO entity) {
        Boolean result = jySendPredictAggsDaoBak.updateByBizProduct(entity) >0;
        if(!result){
            return jySendPredictAggsDaoBak.insert(entity) > 0;
        }
        return result;
    }
}
