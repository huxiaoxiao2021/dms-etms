package com.jd.bluedragon.distribution.jy.dao.unload;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsServiceImpl;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/2/21 20:27
 * @Description:
 */
@Service("jyUnloadAggsSpecialDao")
public class JyUnloadAggsSpecialDao {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JyUnloadAggsDao jyUnloadAggsDao;

    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;

    @Autowired
    private JyUnloadAggsDaoMain jyUnloadAggsDaoMain;

    @Autowired
    private JyUnloadAggsDaoBak jyUnloadAggsDaoBak;


    /**
     * 根据开关获取主库DAO 或者 备库DAO
     * @return
     */
    public JyUnloadAggsDaoStrategy getJyUnloadAggsDao(){
        if(jyDuccConfigManager.getJyUnloadAggsOldOrNewDataReadSwitch()){
            log.info("getJyUnloadAggsDao-getJyUnloadAggsOldOrNewDataReadSwitch 读新库开启");
            if(jyDuccConfigManager.getJyUnloadAggsDataReadSwitchInfo()){
                log.info("getJySendAggsDao-getJyUnloadAggsDataReadSwitchInfo 读备库开启");
                return jyUnloadAggsDaoBak;
            }else {
                log.info("getJyUnloadAggsDao-getJyUnloadAggsDataReadSwitchInfo 读主库开启");
                return jyUnloadAggsDaoMain;
            }
        }
        log.info("getJyUnloadAggsDao-getJyUnloadAggsOldOrNewDataReadSwitch 关闭");
        return jyUnloadAggsDao;
    }

    public int insert(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.insert(entity);
    }

    public Boolean insertOrUpdateJyUnloadCarAggsMain(JyUnloadAggsEntity entity) {
        Boolean result = jyUnloadAggsDaoMain.updateByBizProductBoard(entity) > 0;
        if(!result){
            return jyUnloadAggsDaoMain.insertSelective(entity) > 0;
        }
        return result;
    }

    public Boolean insertOrUpdateJyUnloadCarAggsBak(JyUnloadAggsEntity entity) {
        log.info("insertOrUpdateJyUnloadCarAggsBak-entity-{}", JSON.toJSONString(entity));
        Boolean result = jyUnloadAggsDaoBak.updateByBizProductBoard(entity)>0;
        log.info("insertOrUpdateJyUnloadCarAggsBak-更新结果-{}",result);
        if(!result){
            log.info("insertOrUpdateJyUnloadCarAggsBak-执行插入-{}",JSON.toJSONString(entity));
            return jyUnloadAggsDaoBak.insertSelective(entity) > 0;
        }
        return result;
    }
}
