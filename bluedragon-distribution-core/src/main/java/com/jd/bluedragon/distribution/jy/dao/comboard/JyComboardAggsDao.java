package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;

public interface JyComboardAggsDao {
    int deleteByPrimaryKey(Long id);

    int insert(JyComboardAggsEntity record);

    int insertSelective(JyComboardAggsEntity record);

    JyComboardAggsEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(JyComboardAggsEntity record);

    int updateByPrimaryKey(JyComboardAggsEntity record);
}