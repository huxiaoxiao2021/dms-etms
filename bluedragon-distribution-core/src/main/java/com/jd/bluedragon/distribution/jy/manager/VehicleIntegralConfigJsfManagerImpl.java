package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.vehicle.VehicleIntegralConfig;
import com.jdl.basic.api.service.vehicle.VehicleIntegralConfigJsfService;
import com.jdl.basic.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleIntegralConfigJsfManagerImpl implements VehicleIntegralConfigJsfManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private VehicleIntegralConfigJsfService vehicleIntegralConfigJsfService;


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.VehicleIntegralConfigJsfManagerImpl.findConfigByVehicleType", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "VehicleIntegralConfigJsfManagerImpl.findConfigByVehicleType", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
    public VehicleIntegralConfig findConfigByVehicleType(Integer vehicleType) {
        try {
            Result<VehicleIntegralConfig> result = vehicleIntegralConfigJsfService.findConfigByVehicleType(vehicleType);
            if (!result.isSuccess()) {
                logger.warn("findConfigByVehicleType|根据车型查询积分配置失败:vehicleType={},result={}", vehicleType, JsonHelper.toJson(result));
                return null;
            }
            return result.getData();
        } catch (Exception e) {
            logger.error("findConfigByVehicleType|根据车型查询积分配置异常:vehicleType={}", vehicleType, e);
        }
        return null;
    }
}
