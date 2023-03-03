package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
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
@Service("jySendProductAggsSpecialDao")
public class JySendProductAggsSpecialDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JySendProductAggsDao jySendProductAggsDao;

    @Autowired
    private JySendProductAggsDaoMain jySendProductAggsDaoMain;

    @Autowired
    private JySendProductAggsDaoBak jySendProductAggsDaoBak;

    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;


    /**
     * 获取具体的DAO
     * @return
     */
    public  JySendProductAggsDaoStrategy getJySendProductAggsDao(){
        if(jyDuccConfigManager.getJySendAggOldOrNewDataReadSwitch()){
            log.info("getJySendProductAggs-JySendAggOldOrNewDataReadSwitch 读新库开启");
            if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                log.info("getJySendProductAggs-JySendAggsDataReadSwitch 读备库开启");
                return jySendProductAggsDaoBak;
            }else {
                log.info("getJySendProductAggs-JySendAggsDataReadSwitch 读主库开启");
                return jySendProductAggsDaoMain;
            }
        }
        log.info("getJySendProductAggs-JySendAggOldOrNewDataReadSwitch 关闭");
        return jySendProductAggsDao;
    }

    public Boolean insertOrUpdateJySendProductAggsMain(JySendProductAggsEntity entity) {
        Boolean result = jySendProductAggsDaoMain.updateByBizProduct(entity)>0;
        if(!result){
            return jySendProductAggsDaoMain.insert(entity)>0;
        }
        return result;
    }


    public Boolean insertOrUpdateJySendProductAggsBak(JySendProductAggsEntity entity) {
        Boolean result = jySendProductAggsDaoBak.updateByBizProduct(entity) >0;
        if(!result){
            return jySendProductAggsDaoBak.insert(entity) > 0;
        }
        return result;
    }
}
