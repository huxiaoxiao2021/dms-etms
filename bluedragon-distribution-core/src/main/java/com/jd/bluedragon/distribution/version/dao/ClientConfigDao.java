package com.jd.bluedragon.distribution.version.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;
import com.jd.bluedragon.distribution.version.domain.VersionEntity;

public class ClientConfigDao extends BaseDao<ClientConfig> {
	
	private static String namespace = ClientConfigDao.class.getName();
	
	@SuppressWarnings("unchecked")
	public List<ClientConfig> getAll() {
		return super.getSqlSession().selectList(namespace+".getAll");
	}
	
	@SuppressWarnings("unchecked")
	public List<ClientConfig> getBySiteCode(String siteCode) {
		return super.getSqlSession().selectList(namespace+".getBySiteCode",siteCode);
	}

	public VersionEntity getVersionEntity(VersionEntity versionEntity) {
		Object object= super.getSqlSession().selectOne(namespace+".getVersionEntity",versionEntity);
		if (null!=object) {
			VersionEntity entity=(VersionEntity)object;
			return entity;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<ClientConfig> getByProgramType(Integer programType) {
		return super.getSqlSession().selectList(namespace+".getByProgramType",programType);
	}

	public ClientConfig getById(Long id) {
		return get(namespace, id);
	}
	
	public boolean exists(ClientConfig clientConfig){
		Object object= super.getSqlSession().selectOne(namespace+".exists",clientConfig);
		if (null!=object) {
			Integer count=(Integer)object;
			return count>0;
		}
		return false;
	}

	public boolean add(ClientConfig clientConfig) {
		return add(namespace, clientConfig)>0;
	}

	public boolean update(ClientConfig clientConfig) {
		return update(namespace, clientConfig)>0;
	}
	
	public boolean delete(Long id) {
		return super.getSqlSession().update(namespace+".delete", id)>0;
	}
}
