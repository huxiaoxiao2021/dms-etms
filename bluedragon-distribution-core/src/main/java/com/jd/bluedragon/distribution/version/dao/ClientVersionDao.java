package com.jd.bluedragon.distribution.version.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;

public class ClientVersionDao extends BaseDao<ClientVersion> {
	
	private static String namespace = ClientVersionDao.class.getName();
	
	@SuppressWarnings("unchecked")
	public List<ClientVersion> getAll() {
		return super.getSqlSession().selectList(namespace+".getAll");
	}

	@SuppressWarnings("unchecked")
	public List<ClientVersion> getAllAvailable() {
		return super.getSqlSession().selectList(namespace+".getAllAvailable");
	}

	@SuppressWarnings("unchecked")
	public List<ClientVersion> getByVersionCode(String versionCode) {
		return super.getSqlSession().selectList(namespace+".getByVersionCode",versionCode);
	}

	@SuppressWarnings("unchecked")
	public List<ClientVersion> getByVersionType(Integer versionType) {
		return super.getSqlSession().selectList(namespace+".getByVersionType",versionType);
	}

	public ClientVersion getById(Long id) {
		return get(namespace, id);
	}
	
	public boolean exists(ClientVersion clientVersion){
		Object object= super.getSqlSession().selectOne(namespace+".exists",clientVersion);
		if (null!=object) {
			Integer count=(Integer)object;
			return count>0;
		}
		return false;
	}
	public boolean add(ClientVersion clientVersion) {
		return add(namespace, clientVersion)>0;
	}

	public boolean update(ClientVersion clientVersion) {
		return update(namespace, clientVersion)>0;
	} 

	public boolean delete(Long id) { 
		return super.getSqlSession().update(namespace+".delete", id)>0;
	}
}
