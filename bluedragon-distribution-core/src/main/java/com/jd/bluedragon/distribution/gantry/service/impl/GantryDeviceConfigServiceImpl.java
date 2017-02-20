package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.distribution.gantry.dao.GantryDeviceConfigDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * Created by yanghongqiang on 2016/3/11.
 * 龙门架配置信息操作服务类
 * 1:提供获取龙门架操作类型接口
 *
 * @edit wangtingwei@jd.com 重构龙门架配置
 * @time   2016-08-19
 * @version 2.0
 * ************************************************************************************************
 * 1.龙门架操作类型判断由之前的startTime与endTime组合判断改为只由startTime判断
 * 2.配置表endTime作为批次发货最大操作结束点，一旦此时没有更新批次号，将会根据前一批次号作为模板生成新批次号
 *
 * ************************************************************************************************
 */
@Service("gantryDeviceConfigService")
public class GantryDeviceConfigServiceImpl implements GantryDeviceConfigService {

    private static final Log logger = LogFactory.getLog(GantryDeviceConfigServiceImpl.class);

    @Autowired
    GantryDeviceConfigDao gantryDeviceConfigDao;

    /**
     * @param machineId 龙门架编号
     * @param packageOperateTime 龙门架上传的包裹交接时间
     * @return 返回龙门架操作类型
     */
    @Override
    public GantryDeviceConfig findGantryDeviceConfigByOperateTime(Integer machineId, Date packageOperateTime) {
        if(logger.isInfoEnabled()) {
            logger.debug("龙门架编号:" + machineId + "包裹操作时间:" + packageOperateTime);
        }
        if(packageOperateTime==null) {
            if(logger.isWarnEnabled()) {
                StringBuilder sb = new StringBuilder(50);
                sb.append("龙门架编号:").append(machineId).append("包裹操作时间:").append(packageOperateTime);
                logger.warn(sb);
            }
            return null;
        }
        List<GantryDeviceConfig> list= gantryDeviceConfigDao.findGantryDeviceConfigByOperateTime(machineId, packageOperateTime);
        GantryDeviceConfig target=null;
        if(null!=list&&list.size()>0){
            for (GantryDeviceConfig item:list){
                if(null==target){
                    target=item;
                    continue;
                }
                if(item.getStartTime().after(target.getStartTime())){
                    target=item;
                }
            }
        }
        return target;
    }

    @Override
    public GantryDeviceConfig findMaxStartTimeGantryDeviceConfigByMachineId(Integer machineId) {
        logger.debug("龙门架编号:"+machineId);

        GantryDeviceConfig gantryDeviceConfig= gantryDeviceConfigDao.findMaxStartTimeGantryDeviceConfigByMachineId(machineId)  ;
        if(gantryDeviceConfig==null) {
            StringBuilder sb=new StringBuilder(50);
            sb.append("龙门架编号:").append(machineId);
            logger.info(sb);
            return null;
        }
        return  gantryDeviceConfig;
    }

    @Override
    public List<GantryDeviceConfig> findAllGantryDeviceCurrentConfig(Integer createSiteCode) {
        return gantryDeviceConfigDao.findAllGantryDeviceCurrentConfig(createSiteCode);
    }

    @Override
    public GantryDeviceConfig checkSendCode(String sendCode) {
        return gantryDeviceConfigDao.checkSendCode(sendCode);
    }



    @Override
    public int add(GantryDeviceConfig gantryDeviceConfig) {
        return  gantryDeviceConfigDao.add(GantryDeviceConfigDao.namespace,gantryDeviceConfig);
    }

    @Override
    public Integer addUseJavaTime(GantryDeviceConfig gantryDeviceConfig) {
        return  gantryDeviceConfigDao.addUseJavaTime(gantryDeviceConfig);
    }
    @Override
    public int updateLockStatus(GantryDeviceConfig gantryDeviceConfig) {
        return gantryDeviceConfigDao.updateLockStatus(gantryDeviceConfig);
    }

    @Override
    public int updateBusinessType(GantryDeviceConfig gantryDeviceConfig) {
        return gantryDeviceConfigDao.updateBusinessType(gantryDeviceConfig);
    }

    @Override
    public int unlockDevice(GantryDeviceConfig gantryDeviceConfig) {
        return gantryDeviceConfigDao.unlockDevice(gantryDeviceConfig);
    }

    @Override
    public int lockDevice(GantryDeviceConfig gantryDeviceConfig) {
        return gantryDeviceConfigDao.lockDevice(gantryDeviceConfig);
    }
}
