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

    public List<GantryDeviceConfig> findGantryDeviceConfigByOperateTime(Integer machineId,Date packageOperateTime){
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("machineId",machineId);
        hashMap.put("operateTime",packageOperateTime);
        return super.getSqlSession().selectList(GantryDeviceConfigDao.namespace + ".findGantryDeviceConfigByOperateTime", hashMap);
    }

    public Integer addUseJavaTime(GantryDeviceConfig entity){
        return this.getSqlSession().insert(namespace + ".addUseJavaTime", entity);
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

    public GantryDeviceConfig checkSendCode(String sendCode) {
        HashMap<String,Object> hashMap=new HashMap<String, Object>();
        hashMap.put("sendCode",sendCode);
        return (GantryDeviceConfig) super.getSqlSession().selectOne(GantryDeviceConfigDao.namespace + ".checkSendCode", hashMap);
    }



    public int updateLockStatus(GantryDeviceConfig gantryDeviceConfig) {
        return super.getSqlSession().update(namespace + ".updateLockStatus", gantryDeviceConfig);
    }

    public int updateBusinessType(GantryDeviceConfig gantryDeviceConfig) {
        return super.getSqlSession().update(namespace + ".updateBusinessType", gantryDeviceConfig);
    }

    public int unlockDevice(GantryDeviceConfig gantryDeviceConfig){
        return  super.getSqlSession().update(namespace + ".unlockDevice", gantryDeviceConfig);
    }

    public int lockDevice(GantryDeviceConfig gantryDeviceConfig){
        return  super.getSqlSession().update(namespace + ".lockDevice", gantryDeviceConfig);
    }

}
