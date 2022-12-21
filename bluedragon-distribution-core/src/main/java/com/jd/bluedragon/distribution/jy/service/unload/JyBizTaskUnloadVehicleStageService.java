package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;

import java.util.List;

/**
 * 卸车岗服务
 */
public interface JyBizTaskUnloadVehicleStageService {
    /**
     * 生成转运卸车岗阶段子任务bizId
     * @param unloadVehicleBizId
     * @return
     */
    String generateStageBizId(String unloadVehicleBizId);

    /**
     * 按条件查阶段子任务
     * @param condition
     * @return
     */
    JyBizTaskUnloadVehicleStageEntity queryCurrentStage(JyBizTaskUnloadVehicleStageEntity condition);

    /**
     * 批量插入
     * @param entityList
     * @return
     */
    int insertBatch(List<JyBizTaskUnloadVehicleStageEntity> entityList);

    int insertSelective(JyBizTaskUnloadVehicleStageEntity entity);

    int updateByPrimaryKeySelective(JyBizTaskUnloadVehicleStageEntity condition);

    /**
     * 根据主子任务bizId修改任务
     * @param condition
     * @return
     */
    int updateStatusByUnloadVehicleBizId(JyBizTaskUnloadVehicleStageEntity condition);

    /**
     * 根据阶段子任务BizId修改任务
     * @param condition
     * @return
     */
    int updateStatusByBizId(JyBizTaskUnloadVehicleStageEntity condition);

    /**
     * 查询子任务ID
     * @param unloadVehicleBizId
     * @return
     */
    List<Long> countByUnloadVehicleBizId(String unloadVehicleBizId);

    /**
     * 根据bizid查询进行中的阶段任务
     * @param bizId
     * @return
     */
    JyBizTaskUnloadVehicleStageEntity selectUnloadDoingStageTask(String bizId);

    /**
     * 查询正常子任务数量（不含补扫任务）
     * @param entity
     * @return
     */
    int getTaskCount(JyBizTaskUnloadVehicleStageEntity entity);
}
