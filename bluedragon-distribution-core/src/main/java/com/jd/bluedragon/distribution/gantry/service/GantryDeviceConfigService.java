package com.jd.bluedragon.distribution.gantry.service;

import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import java.util.Date;

/**
 * Created by yanghongqiang on 2016/3/11.
 */
public interface GantryDeviceConfigService {

    /**
     * @param gatryNum 龙门架编号
     * @param packageOperateTime 龙门架上传的包裹交接时间
     */
    GantryDeviceConfig getGantryDeviceOperateType(String gatryNum,Date packageOperateTime);
}
