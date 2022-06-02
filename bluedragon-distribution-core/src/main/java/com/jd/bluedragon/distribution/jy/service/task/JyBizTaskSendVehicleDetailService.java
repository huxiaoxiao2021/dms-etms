package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;

import java.util.List;

public interface JyBizTaskSendVehicleDetailService {
    JyBizTaskSendVehicleDetailEntity findByBizId(String bizId);

    int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 保存发货流向
     * @param detailEntity
     * @return
     */
    int saveTaskSendDetail(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 根据条件查询发车任务明细
     * @param entity
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity);

}
