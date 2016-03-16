package com.jd.bluedragon.distribution.gantry.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/10
 */
public class GantryDeviceDao extends BaseDao<GantryDevice>{
    public static final String namespace = GantryDeviceDao.class.getName();

    public List<GantryDevice> getGantry(Map<String, Object> param){
        return this.getSqlSession().selectList(GantryDeviceDao.namespace + ".queryGantry", param);
    }

    public Integer getGantryCount(Map<String, Object> param){
        return (Integer) this.getSqlSession().selectOne(GantryDeviceDao.namespace + ".queryGantryCount", param);
    }

    public List<GantryDevice> getGantryPage(Map<String, Object> param){
        return this.getSqlSession().selectList(GantryDeviceDao.namespace + ".queryGantryPage", param);
    }

    public int addGantry(GantryDevice device){
        return this.getSqlSession().insert(GantryDeviceDao.namespace + ".addGantry", device);
    }

    public int delGantry(Map<String, Object> param){
        return this.getSqlSession().delete(GantryDeviceDao.namespace + ".delGantry", param);
    }

    public int updateGantryById(GantryDevice device){
        return this.getSqlSession().update(GantryDeviceDao.namespace + ".updateGantryById", device);
    }
}
