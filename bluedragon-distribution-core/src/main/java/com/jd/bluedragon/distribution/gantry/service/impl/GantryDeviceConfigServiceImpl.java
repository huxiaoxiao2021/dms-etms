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
 */
@Service("gantryDeviceConfigService")
public class GantryDeviceConfigServiceImpl implements GantryDeviceConfigService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    GantryDeviceConfigDao gantryDeviceConfigDao;

    /**
     * @param machineId 龙门架编号
     * @param packageOperateTime 龙门架上传的包裹交接时间
     */
    @Override
    public GantryDeviceConfig findGantryDeviceConfigByOperateTime(Integer machineId, Date packageOperateTime) {
        logger.debug("龙门架编号:"+machineId+"包裹操作时间:"+packageOperateTime);
        if(packageOperateTime==null) {
            StringBuilder sb=new StringBuilder(50);
            sb.append("龙门架编号:").append(machineId).append("包裹操作时间:").append(packageOperateTime);
            logger.info(sb);
            return null;
        }
        return gantryDeviceConfigDao.findGantryDeviceConfigByOperateTime(machineId, packageOperateTime)  ;
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
    public Integer updateGantryDeviceConfigStatus(GantryDeviceConfig gantryDeviceConfig) {
        return gantryDeviceConfigDao.updateGantryDeviceConfigStatus(gantryDeviceConfig);
    }


}
