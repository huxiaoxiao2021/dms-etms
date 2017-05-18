package com.jd.bluedragon.distribution.rollcontainer.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhc
 * @version 1.0
 * @date 2017/5/3
 */
public class ContainerRelationDao extends BaseDao<ContainerRelation>{
    public static final String namespace = ContainerRelationDao.class.getName();

    public int addContainerRelation(ContainerRelation containerRelation){
        return this.getSqlSession().insert(ContainerRelationDao.namespace + ".insertContainerRelation", containerRelation);
    }
    
    public ContainerRelation getContainerRelation(String containerCode){
    	return this.getSqlSession().selectOne(ContainerRelationDao.namespace + ".getContainerRelation", containerCode);
    }
    
    public int updateContainerRelationByCode(ContainerRelation containerRelation){
        return this.getSqlSession().update(ContainerRelationDao.namespace + ".updateContainerRelation", containerRelation);
    }
}
