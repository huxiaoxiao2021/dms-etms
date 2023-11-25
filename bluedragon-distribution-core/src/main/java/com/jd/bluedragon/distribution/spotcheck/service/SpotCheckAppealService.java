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

}
