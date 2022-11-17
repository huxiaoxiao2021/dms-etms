package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;

import java.util.List;

public class JyGroupSortCrossDetailDao extends BaseDao<JyGroupSortCrossDetailEntity> {
    private final static String NAMESPACE = JyGroupSortCrossDetailDao.class.getName();

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyGroupSortCrossDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public JyGroupSortCrossDetailEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".deleteByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public int updateByPrimaryKey(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }

    public int batchInsert(List<JyGroupSortCrossDetailEntity> list) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", list);
    }
}
