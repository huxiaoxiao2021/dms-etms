package com.jd.bluedragon.distribution.boxlimit.dao;

import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;

import java.util.List;

public interface BoxLimitConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BoxLimitConfig record);

    int insertSelective(BoxLimitConfig record);

    BoxLimitConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BoxLimitConfig record);

    int updateByPrimaryKey(BoxLimitConfig record);

    /**
     * 批量新增
     */
    int batchInsert(List<BoxLimitConfig> dataList);

    /**
     * 根据条件查询
     */
    List<BoxLimitConfig> queryByCondition(BoxLimitQueryDTO dto);

    /**
     * 根据条件查询
     */
    Integer countByCondition(BoxLimitQueryDTO dto);

    /**
     * 根据机构ID查询记录是否存在
     */
    List<BoxLimitConfig> queryBySiteIds(List<Integer> siteIds);

    /**
     * 根据ID 批量删除
     */
    int batchDelete(List<Integer> ids);
}