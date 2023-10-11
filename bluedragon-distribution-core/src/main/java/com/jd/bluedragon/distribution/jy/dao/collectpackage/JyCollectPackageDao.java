package com.jd.bluedragon.distribution.jy.dao.collectpackage;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.collectpackage.JyCollectPackageEntity;

public class JyCollectPackageDao extends BaseDao<JyCollectPackageEntity> {
    private final static String NAMESPACE = JyBizTaskCollectPackageDao.class.getName();
    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    int insert(JyCollectPackageEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    int insertSelective(JyCollectPackageEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyCollectPackageEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    int updateByPrimaryKeySelective(JyCollectPackageEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyCollectPackageEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }
}