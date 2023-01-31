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

    public int insertSelective(JyComboardAggsEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }
    public JyComboardAggsEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }
    public int updateByPrimaryKeyAndTs(JyComboardAggsEntity record) {
        if (record == null || record.getTs() == null) {
            return 0;
        }
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeyAndTs", record);
    }
    public int updateByPrimaryKey(JyComboardAggsEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public List<JyComboardAggsEntity> queryComboardAggs(JyComboardAggsCondition comboardAggsCondition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryComboardAggs", comboardAggsCondition);
    }
}
