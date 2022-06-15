package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

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
     * 按状态查发货任务流向
     * @param entity
     * @param statuses
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findBySiteAndStatus(JyBizTaskSendVehicleDetailEntity entity, List<Integer> statuses);

    /**
     * 取消
     * @param detailEntity
     * @return
     */
    int cancelDetail(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 按顺序更新发货明细状态
     * @param detailEntity
     * @param oldStatus
     * @return
     */
    int updateStatus(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus);

    /**
     * 按状态统计流向数量
     * @param entity
     * @return
     */
    int countByStatus(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 查询发车任务的所有流向
     * @param entity
     * @return
     */
    List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity);

    int updateBizTaskSendDetailStatus(JyBizTaskSendVehicleDetailEntity entity);
}
