package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/21 16:13
 * @Description:
 */
@Service("jySendAggsSpecialDao")
public class JySendAggsSpecialDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private JySendAggsDao jySendAggsDao;

    @Autowired
    private JySendAggsDaoMain jySendAggsDaoMain;

    @Autowired
    private JySendAggsDaoBak jySendAggsDaoBak;


    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;


    public JySendAggsDaoStrategy getJySendAggsDao(){
        if(jyDuccConfigManager.getJySendAggOldOrNewDataReadSwitch()){
            log.info("getJySendAggsDao-JySendAggOldOrNewDataReadSwitch 读新库开启");
            if (jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                log.info("getJySendAggsDao--JySendAggsDataReadSwitch 读备库开启");
                return jySendAggsDaoBak;
            }else {
                log.info("getJySendAggsDao-JySendAggsDataReadSwitch 读主库开启");
                return jySendAggsDaoMain;
            }
        }
        log.info("getJySendAggsDao-JySendAggOldOrNewDataReadSwitch 关闭");
        return jySendAggsDao;
    }

    public JySendAggsDaoBak getJySendAggsDaoBak(){
        return jySendAggsDaoBak;
    }

    public JySendAggsDaoMain getJySendAggsDaoMain(){
        return jySendAggsDaoMain;
    }

    public Boolean insertOrUpdateJySendGoodsAggsMain(JySendAggsEntity entity){
        Boolean result = jySendAggsDaoMain.updateByBizId(entity) > 0;
        if(!result){
            return jySendAggsDaoMain.insertBySendAggEntity(entity) > 0;
        }
        return result;
    }

    public Boolean insertOrUpdateJySendGoodsAggsBak(JySendAggsEntity entity){
        Boolean result = jySendAggsDaoBak.updateByBizId(entity) > 0;
        if(!result){
            return jySendAggsDaoBak.insertBySendAggEntity(entity) >0;
        }
        return result;
    }


}
