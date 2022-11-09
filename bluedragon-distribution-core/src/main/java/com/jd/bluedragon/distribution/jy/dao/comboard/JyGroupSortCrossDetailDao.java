package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;

public interface JyGroupSortCrossDetailDao {
    int deleteByPrimaryKey(Long id);

    int insert(JyGroupSortCrossDetailEntity record);

    int insertSelective(JyGroupSortCrossDetailEntity record);

    JyGroupSortCrossDetailEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(JyGroupSortCrossDetailEntity record);

    int updateByPrimaryKey(JyGroupSortCrossDetailEntity record);
}