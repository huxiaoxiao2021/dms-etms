package com.jd.bluedragon.distribution.gantry.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;

import java.util.List;
import java.util.Map;

/**
 * @author hanjiaxing
 * @version 1.0
 * @date 2016/12/8
 */
public class GantryExceptionDao extends BaseDao<GantryException>{
    public static final String namespace = GantryExceptionDao.class.getName();

    public List<GantryException> getGantryException(Map<String, Object> param){
        return this.getSqlSession().selectList(GantryExceptionDao.namespace + ".queryGantryException", param);
    }

    public Integer getGantryExceptionCount(Map<String, Object> param){
        return (Integer) this.getSqlSession().selectOne(GantryExceptionDao.namespace + ".queryGantryExceptionCount", param);
    }

    public List<GantryException> getGantryExceptionPage(Map<String, Object> param){
        return this.getSqlSession().selectList(GantryExceptionDao.namespace + ".queryGantryExceptionPage", param);
    }

    public int addGantryException(GantryException gantryException){
        return this.getSqlSession().insert(GantryExceptionDao.namespace + ".addGantryException", gantryException);
    }

    public int updateSendStatus(Map<String, Object> param){
        return this.getSqlSession().update(GantryExceptionDao.namespace + ".updateSendStatus", param);
    }

}
