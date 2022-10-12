package com.jd.bluedragon.distribution.jy.service.config;

import com.jd.bluedragon.sdk.modules.client.dto.JyDemotionConfigInfo;

import java.util.List;

/**
 * 拣运降级接口
 *
 * @author hujiping
 * @date 2022/10/10 4:16 PM
 */
public interface JyDemotionService {

    // jy flink发货降级开关
    String JY_FLINK_SEND_IS_DEMOTION = "jyFlinkSendIsDemotion";
    // jy flink卸车降级开关
    String JY_FLINK_UNLOAD_IS_DEMOTION = "jyFlinkUnloadIsDemotion";
    // jy封车降级开关
    String JY_SEAL_CAR_MONITOR_IS_DEMOTION = "jySealCarMonitorIsDemotion";
    // jy卸车降级开关
    String JY_VEHICLE_TASK_UNLOAD_DETAIL_IS_DEMOTION = "jyVehicleTaskUnloadDetailIsDemotion";
    // jy发货降级开关
    String JY_VEHICLE_SEND_DETAIL_IS_DEMOTION = "jyVehicleSendDetailIsDemotion";

    /**
     * 校验某功能是否降级
     *
     * @param key
     * @return
     */
    boolean checkIsDemotion(String key);

    /**
     * 获取拣运降级配置
     *
     * @return
     */
    List<JyDemotionConfigInfo> obtainJyDemotionConfig();
}
