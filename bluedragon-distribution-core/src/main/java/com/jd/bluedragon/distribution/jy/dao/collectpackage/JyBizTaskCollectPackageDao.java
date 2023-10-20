package com.jd.bluedragon.distribution.jy.dao.collectpackage;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageEntity;
import com.jd.bluedragon.distribution.jy.collectpackage.JyBizTaskCollectPackageQuery;

import java.util.List;

public class JyBizTaskCollectPackageDao extends BaseDao<JyBizTaskCollectPackageEntity> {
    private final static String NAMESPACE = JyBizTaskCollectPackageDao.class.getName();
    int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyBizTaskCollectPackageEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insert", record);
    }

    public int insertSelective(JyBizTaskCollectPackageEntity record){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }

    JyBizTaskCollectPackageEntity selectByPrimaryKey(Long id){
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }

    public int updateByPrimaryKeySelective(JyBizTaskCollectPackageEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
    }

    int updateByPrimaryKey(JyBizTaskCollectPackageEntity record){
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public JyBizTaskCollectPackageEntity findByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", bizId);
    }

    public List<JyBizTaskCollectPackageEntity> pageQueryTask(JyBizTaskCollectPackageQuery query) {
        return this.getSqlSession().selectList(NAMESPACE + ".pageQueryTask", query);
    }

    public Long queryTaskCount(JyBizTaskCollectPackageQuery query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryTaskCount", query);
    }

    public JyBizTaskCollectPackageEntity findByBoxCode(String boxCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBoxCode", boxCode);
    }
}
