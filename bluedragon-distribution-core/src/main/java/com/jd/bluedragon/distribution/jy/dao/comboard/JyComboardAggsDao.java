package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyComboardAggsDto;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsCondition;

import java.util.List;

public class JyComboardAggsDao extends BaseDao<JyComboardAggsEntity> {
    private final static String NAMESPACE = JyComboardAggsDao.class.getName();

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyComboardAggsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyComboardAggsEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public JyComboardAggsEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".deleteByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyComboardAggsEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public int updateByPrimaryKey(JyComboardAggsEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }

    public List<JyComboardAggsEntity> queryComboardAggs(JyComboardAggsCondition comboardAggsCondition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryComboardAggs", comboardAggsCondition);
    }
}
