package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;

public interface JyBizTaskComboardDao {
    int deleteByPrimaryKey(Long id);

    int insert(JyBizTaskComboardEntity record);

    int insertSelective(JyBizTaskComboardEntity record);

    JyBizTaskComboardEntity selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(JyBizTaskComboardEntity record);

    int updateByPrimaryKey(JyBizTaskComboardEntity record);
}