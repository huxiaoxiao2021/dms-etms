package com.jd.bluedragon.distribution.spotcheck.dao;

import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;

import java.util.List;

public interface SpotCheckAppealDao {

    /**
     * 插入
     */
    int insertRecord(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据条件查询
     */
    List<SpotCheckAppealEntity> findByCondition(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据条件统计总数
     */
    int countByCondition(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID更新
     */
    int updateById(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID列表批量更新
     */
    int batchUpdateByIds(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID查找
     */
    SpotCheckAppealEntity findById(SpotCheckAppealEntity spotCheckAppealEntity);

    /**
     * 根据ID列表批量查找
     */
    List<SpotCheckAppealEntity> batchFindByIds(SpotCheckAppealEntity spotCheckAppealEntity);

}
