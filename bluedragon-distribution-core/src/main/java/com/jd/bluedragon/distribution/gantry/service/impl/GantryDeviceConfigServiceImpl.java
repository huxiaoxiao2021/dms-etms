package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.distribution.gantry.dao.GantryDeviceConfigDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import java.util.Date;
import java.util.logging.Logger;

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
     * @param gatryNum 龙门架编号
     * @param packageOperateTime 龙门架上传的包裹交接时间
     */
    public GantryDeviceConfig getGantryDeviceOperateType(String gatryNum,Date packageOperateTime){
        logger.debug("龙门架编号:"+gatryNum+"包裹操作时间:"+packageOperateTime);
        if(StringHelper.isEmpty(gatryNum)||packageOperateTime==null) {
            StringBuilder sb=new StringBuilder(50);
            sb.append("龙门架编号:").append(gatryNum).append("包裹操作时间:").append(packageOperateTime);
            logger.info(sb);
            return null;
        }
        return gantryDeviceConfigDao.getGantryDeviceOperateType(gatryNum,packageOperateTime)  ;
    }

}
