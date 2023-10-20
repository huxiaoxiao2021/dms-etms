package com.jd.bluedragon.distribution.jy.dao.collectpackage;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageFlowEntity;

import java.util.List;

public class JyBizTaskCollectPackageFlowDao extends BaseDao<JyBizTaskCollectPackageFlowEntity> {
    private final static String NAMESPACE = JyBizTaskCollectPackageFlowDao.class.getName();
    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyBizTaskCollectPackageFlowEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    int insertSelective(JyBizTaskCollectPackageFlowEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyBizTaskCollectPackageFlowEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyBizTaskCollectPackageFlowEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyBizTaskCollectPackageFlowEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public int batchInsert(List<JyBizTaskCollectPackageFlowEntity> recordList){
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", recordList);
    }

    public List<JyBizTaskCollectPackageFlowEntity> queryListByBizId(String bizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryListByBizId", bizId);
    }
}
