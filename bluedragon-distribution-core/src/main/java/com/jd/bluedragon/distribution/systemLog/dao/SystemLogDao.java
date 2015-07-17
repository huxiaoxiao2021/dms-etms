package com.jd.bluedragon.distribution.systemLog.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

public class SystemLogDao extends BaseDao<SystemLog> {

	public static final String namespace = SystemLogDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<SystemLog> queryByParams(Map<String, Object> params) {
		return this.getSqlSession().selectList(namespace + ".queryByParams", params);
	}

	public Integer totalSizeByParams(Map<String, Object> params) {
		return (Integer) this.getSqlSession().selectOne(namespace + ".totalSizeByParams", params);
	}
}
