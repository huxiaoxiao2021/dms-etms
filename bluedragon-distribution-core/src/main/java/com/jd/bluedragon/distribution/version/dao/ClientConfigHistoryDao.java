package com.jd.bluedragon.distribution.version.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

public class ClientConfigHistoryDao extends BaseDao<ClientConfigHistory> {

	private static String namespace = ClientConfigHistoryDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<ClientConfigHistory> getAll() {
		return super.getSqlSession().selectList(ClientConfigHistoryDao.namespace + ".getAll");
	}

	@SuppressWarnings("unchecked")
	public List<ClientConfigHistory> getBySiteCode(String siteCode) {
		return super.getSqlSession().selectList(
		        ClientConfigHistoryDao.namespace + ".getBySiteCode", siteCode);
	}

	@SuppressWarnings("unchecked")
	public List<ClientConfigHistory> getByProgramType(Integer programType) {
		return super.getSqlSession().selectList(
		        ClientConfigHistoryDao.namespace + ".getByProgramType", programType);
	}

	public boolean add(ClientConfigHistory clientConfigHistory) {
		return add(ClientConfigHistoryDao.namespace, clientConfigHistory) > 0;
	}
}
