package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;

import java.util.List;

public interface JyBizTaskSendVehicleService {
    /**
     * 根据bizId获取数据
     * @return
     */
    JyBizTaskSendVehicleEntity findByBizId(String bizId);

    int saveSendVehicleTask(JyBizTaskSendVehicleEntity entity);

    int updateSendVehicleTask(JyBizTaskSendVehicleEntity entity);

    /**
     * 根据派车单查发车任务
     * @param entity
     * @return
     */
    JyBizTaskSendVehicleEntity findByTransWorkAndStartSite(JyBizTaskSendVehicleEntity entity);

    /**
     * 初始化发车任务
     * @param entity
     * @return
     */
    int initTaskSendVehicle(JyBizTaskSendVehicleEntity entity);

    /**
     * 按状态统计发货任务数量
     * @param entity
     * @param sendVehicleBizList
     * @return
     */
    List<JyBizTaskSendCountDto> sumTaskByVehicleStatus(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList);

    /**
     * 分页查询发货任务
     * @param entity
     * @param sendVehicleBizList
     * @param typeEnum
     * @param pageNum
     * @param pageSize
     * @param statuses
     * @return
     */
    List<JyBizTaskSendVehicleEntity> querySendTaskOfPage(JyBizTaskSendVehicleEntity entity,
                                                         List<String> sendVehicleBizList,
                                                         JyBizTaskSendSortTypeEnum typeEnum,
                                                         Integer pageNum, Integer pageSize,
                                                         List<Integer> statuses);

    /**
     * 统计发车任务数量
     * @param entity
     * @param sendVehicleBizList
     * @param statuses
     * @return
     */
    Integer countByCondition(JyBizTaskSendVehicleEntity entity, List<String> sendVehicleBizList, List<Integer> statuses);

    /**
     * 更细最晚发车时间
     * @param entity
     * @return
     */
    int updateLastPlanDepartTime(JyBizTaskSendVehicleEntity entity);

    /**
     * 更新最晚封车时间
     * @param entity
     * @return
     */
    int updateLastSealCarTime(JyBizTaskSendVehicleEntity entity);

    /**
     * 按顺序更新发货状态
     * @param entity
     * @param oldStatus
     * @return
     */
    int updateStatus(JyBizTaskSendVehicleEntity entity, Integer oldStatus);

    int updateBizTaskSendStatus(JyBizTaskSendVehicleEntity entity);
}
