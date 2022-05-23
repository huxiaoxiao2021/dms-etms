package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

public interface JyBizTaskSendVehicleService {
    /**
     * 根据bizId获取数据
     * @return
     */
    JyBizTaskSendVehicleEntity findByBizId(String bizId);

    int saveSendVehicleTask(JyBizTaskSendVehicleEntity entity);

    int updateSendVehicleTask(JyBizTaskSendVehicleEntity entity);
}
