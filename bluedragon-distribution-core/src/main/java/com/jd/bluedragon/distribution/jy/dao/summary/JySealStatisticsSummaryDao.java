package com.jd.bluedragon.distribution.jy.dao.summary;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JySealStatisticsSummaryEntity;

import java.util.List;

public class JySealStatisticsSummaryDao extends BaseDao<JySealStatisticsSummaryEntity> {

    private final static String NAMESPACE = JySealStatisticsSummaryDao.class.getName();

//    int deleteByPrimaryKey(Long id){
//        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", id);
//    }
//
    public int insert(JySealStatisticsSummaryEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int insertSelective(JySealStatisticsSummaryEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public List<JySealStatisticsSummaryEntity> queryByBusinessKeysAndType(JySealStatisticsSummaryCondition condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByBusinessKeysAndType", condition);
    }

    public JySealStatisticsSummaryEntity queryByBusinessKeyAndType(JySealStatisticsSummaryEntity entity) {
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