package com.jd.bluedragon.distribution.base.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Ice.SyscallException;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.base.domain.SysConfig;

public class SysConfigDao extends BaseDao<SysConfig> {

	 public static final String namespace = SysConfigDao.class.getName();

	 public int del(Long pk){
		 return super.getSqlSession().update(SysConfigDao.namespace + ".del", pk);
	 }

	@SuppressWarnings("unchecked")
	public List<SysConfig> queryByKey(Map<String, Object> params) {
		 return super.getSqlSession().selectList(SysConfigDao.namespace + ".queryByKey", params);
	 }
	@SuppressWarnings("unchecked")
	public List<SysConfig> getSwitchList(){
		return getSqlSession().selectList(SysConfigDao.namespace + ".getSwitchList");
	}

	public SysConfig findConfigContentByConfigName(String configName){
        SysConfig  result= getSqlSession().selectOne(SysConfigDao.namespace+".findConfigContentByConfigName",configName);
        return result;
		//return getSqlSession().selectOne(SysConfigDao.namespace+".findConfigContentByConfigName",configName);
	}
	public List<SysConfig> getList(SysConfig sysConfig){
		return getSqlSession().selectList(SysConfigDao.namespace + ".getList",sysConfig);
	}

	public List<SysConfig> getListByConName(String conName){
		return getSqlSession().selectList(SysConfigDao.namespace + ".getListByConName",conName);
	}
	
	/**
	 * 根据参数查询 基础参数设置的总数据量
	 * @param map
	 * @return INTEGET
	 */
	public Integer totalSysconfigSizeByParams(String key){
		 return (Integer)super.getSqlSession().selectOne(SysConfigDao.namespace + ".totalSysconfigSizeByParams", key);
	}
}
