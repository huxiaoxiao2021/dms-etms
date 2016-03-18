package com.jd.bluedragon.distribution.gantry.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yanghongqiang on 2016/3/11.
 */
public class GantryDeviceConfigDao extends BaseDao<GantryDeviceConfig> {

    public static final String namespace = GantryDeviceConfigDao.class.getName();

    public GantryDeviceConfig findGantryDeviceConfigByOperateTime(Integer machineId,Date packageOperateTime){
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("machineId",machineId);
        hashMap.put("",packageOperateTime);
        return (GantryDeviceConfig) super.getSqlSession().selectOne(GantryDeviceConfigDao.namespace + ".findGantryDeviceConfigByOperateTime", hashMap);
    }

    public GantryDeviceConfig findMaxStartTimeGantryDeviceConfigByMachineId(Integer machineId) {
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("machineId",machineId);
        return (GantryDeviceConfig) super.getSqlSession().selectOne(GantryDeviceConfigDao.namespace + ".findMaxStartTimeGantryDeviceConfigByMachineId", hashMap);
    }

    public List<GantryDeviceConfig> findAllGantryDeviceCurrentConfig(Integer createSiteCode) {
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("createSiteCode",createSiteCode);
        return super.getSqlSession().selectList(GantryDeviceConfigDao.namespace + ".findAllGantryDeviceCurrentConfig", hashMap);
    }

    public Integer updateGantryDeviceConfigStatus(GantryDeviceConfig gantryDeviceConfig) {
        return super.getSqlSession().update(namespace + ".update", gantryDeviceConfig);
    }

    public int updateLockStatus(GantryDeviceConfig gantryDeviceConfig) {
        return super.getSqlSession().update(namespace + ".updateLockStatus", gantryDeviceConfig);
    }

    public int updateBusinessType(GantryDeviceConfig gantryDeviceConfig) {
        return super.getSqlSession().update(namespace + ".updateBusinessType", gantryDeviceConfig);
    }
}
