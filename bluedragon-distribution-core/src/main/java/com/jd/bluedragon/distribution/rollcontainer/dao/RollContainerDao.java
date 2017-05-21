package com.jd.bluedragon.distribution.rollcontainer.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhc
 * @version 1.0
 * @date 2017/5/3
 */
public class RollContainerDao extends BaseDao<RollContainer>{
    public static final String namespace = RollContainerDao.class.getName();

    public List<RollContainer> getRollContainer(Map<String, Object> param){
        return this.getSqlSession().selectList(RollContainerDao.namespace + ".queryRollContainer", param);
    }

    public Integer getRollContainerCount(Map<String, Object> param){
        return (Integer) this.getSqlSession().selectOne(RollContainerDao.namespace + ".queryRollContainerCount", param);
    }

    public List<RollContainer> getRollContainerPage(Map<String, Object> param){
        return this.getSqlSession().selectList(RollContainerDao.namespace + ".findRollContainerList", param);
    }

    public int addRollContainer(RollContainer rollContainer){
        return this.getSqlSession().insert(RollContainerDao.namespace + ".addRollContainer", rollContainer);
    }
    
    public int updateRollContainerByCode(RollContainer rollContainer){
        return this.getSqlSession().update(RollContainerDao.namespace + ".updateRollContainerByCode", rollContainer);
    }
    
    /**
     * 根据周转箱编号查询信息
     * @return
     */
    public RollContainer getRollContainerByCode(String code){
    	return this.getSqlSession().selectOne(RollContainerDao.namespace + ".getRollContainerByCode", code);
    }
}
