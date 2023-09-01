package com.jd.bluedragon.distribution.jy.dao.summary;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;

import java.util.List;

public class JyStatisticsSummaryDao extends BaseDao<JyStatisticsSummaryEntity> {

    private final static String NAMESPACE = JyStatisticsSummaryDao.class.getName();

//    int deleteByPrimaryKey(Long id){
//        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", id);
//    }
//
    public int insert(JyStatisticsSummaryEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int insertSelective(JyStatisticsSummaryEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public List<JyStatisticsSummaryEntity> queryByBusinessKeysAndType(JyStatisticsSummaryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByBusinessKeysAndType", condition);
    }

    public JyStatisticsSummaryEntity queryByBusinessKeyAndType(JyStatisticsSummaryEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBusinessKeyAndType", entity);
    }
//
//    JyStatisticsSummaryEntity selectByPrimaryKey(Long id){
//        return this.getSqlSession().selectOne(NAMESPACE + ".deleteByPrimaryKey", id);
//    }
//
//    int updateByPrimaryKeySelective(JyStatisticsSummaryEntity entity){
//        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", entity);
//    }
//
//    int updateByPrimaryKey(JyStatisticsSummaryEntity entity){
//        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", entity);
//    }
}