package com.jd.bluedragon.distribution.gantry.service;

import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;

import java.util.Date;
import java.util.List;

/**
 * Created by yanghongqiang on 2016/3/11.
 */
public interface GantryDeviceConfigService {

    /**
     * @param machineId          龙门架编号
     * @param packageOperateTime 龙门架上传的包裹交接时间
     */
    GantryDeviceConfig findGantryDeviceConfigByOperateTime(Integer machineId, Date packageOperateTime);


    /**
     * @param machineId 龙门架编号
     */
    GantryDeviceConfig findMaxStartTimeGantryDeviceConfigByMachineId(Integer machineId);

    /*
    获取当前分拣中心的所有龙门架当前信息
     */
    List<GantryDeviceConfig> findAllGantryDeviceCurrentConfig(Integer createSiteCode);



    int add(GantryDeviceConfig gantryDeviceConfig);

    int updateLockStatus(GantryDeviceConfig gantryDeviceConfig);

    int updateBusinessType(GantryDeviceConfig gantryDeviceConfig);

    GantryDeviceConfig checkSendCode(String sendCode);
}
