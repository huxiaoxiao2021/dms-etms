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
    List<JyBizTaskSendVehicleDetailEntity> findEffectiveSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 取消
     * @param detailEntity
     * @return
     */
    int cancelDetail(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 按状态统计流向数量
     * @param entity
     * @return
     */
    int countByStatus(JyBizTaskSendVehicleDetailEntity entity);

}
