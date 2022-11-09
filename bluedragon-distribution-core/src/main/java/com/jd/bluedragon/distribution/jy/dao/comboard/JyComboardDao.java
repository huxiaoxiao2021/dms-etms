package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;

public interface JyComboardDao {
    int deleteByPrimaryKey(Long id);

    int insert(JyComboardEntity record);

    int insertSelective(JyComboardEntity record);

    JyComboardEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(JyComboardEntity record);

    int updateByPrimaryKey(JyComboardEntity record);
}