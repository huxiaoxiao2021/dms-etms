package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;

import java.util.List;

/**
 * 抽检申诉服务接口
 */
public interface SpotCheckAppealService {

    /**
     * 插入
     */
    void insertRecord(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据条件查询
     */
    List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据条件统计总数
     */
    Integer countByCondition(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID更新
     */
    void updateById(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID列表批量更新
     */
    void batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID查找
     */
    SpotCheckAppealEntity findById(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID列表批量查找
     */
    List<SpotCheckAppealEntity> batchFindByIds(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据bizId查找
     */
    SpotCheckAppealEntity findByBizId(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 定时处理超时未确认的设备抽检申诉核对记录
     */
    void dealSpotCheckAppealByNotConfirmAndOverTime();

    /**
     * 通知称重再造系统
     */
    void notifyRemakeSystem(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 批量通知称重再造系统
     */
    void batchNotifyRemakeSystem(List<SpotCheckAppealEntity> entityList);

}
