package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

import java.util.List;

public interface JyBizTaskSendVehicleDetailService {
    JyBizTaskSendVehicleDetailEntity findByBizId(String bizId);

    int updateDateilTaskByVehicleBizId(JyBizTaskSendVehicleDetailEntity detailEntity);

    int updateByBiz(JyBizTaskSendVehicleDetailEntity entity);

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
     * 按条件查询未封车的流向任务
     * @param entity
     * @return
     */
    List<JyBizTaskSendVehicleDetailEntity> findNoSealCarSendVehicleDetail(JyBizTaskSendVehicleDetailEntity entity);

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

    int cancelDetailTaskAndMainTask(JyBizTaskSendVehicleDetailEntity detailEntity);

    /**
     * 按顺序更新发货明细状态
     * @param detailEntity
     * @param oldStatus
     * @return
     */
    int updateStatus(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus);

    /**
     * 不按顺序更新发货明细状态
     * @param detailEntity
     * @param oldStatus
     * @return
     */
    int updateStatusWithoutCompare(JyBizTaskSendVehicleDetailEntity detailEntity, Integer oldStatus);

    /**
     * 按状态统计流向数量
     * @param entity
     * @return
     */
    Integer countByCondition(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 查询发车任务的所有流向
     * @param entity
     * @return
     */
    List<Long> getAllSendDest(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 查询一条发货流向记录
     * @param entity
     * @return
     */
    JyBizTaskSendVehicleDetailEntity findSendDetail(JyBizTaskSendVehicleDetailEntity entity);

    /**
     * 按状态统计发货明细数量
     * @param entity
     * @return
     */
    List<JyBizTaskSendCountDto> sumByVehicleStatus(JyBizTaskSendVehicleDetailEntity entity);

    int updateBizTaskSendDetailStatus(JyBizTaskSendVehicleDetailEntity entity);

    Integer countNoCancelSendDetail(JyBizTaskSendVehicleDetailEntity entity);
    /**
     * 根据transWorkItemCode查询一条数据
     * @param query
     * @return
     */
	JyBizTaskSendVehicleDetailEntity findByTransWorkItemCode(JyBizTaskSendVehicleDetailEntity query);
}
