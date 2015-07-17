package com.jd.bluedragon.distribution.operationLog.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;

public class OperationLogDao extends BaseDao<OperationLog> {

	public static final String namespace = OperationLogDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<OperationLog> queryByParams(Map<String, Object> params) {
		return this.getSqlSession().selectList(namespace + ".queryByParams", params);
	}

	public Integer totalSizeByParams(Map<String, Object> params) {
		return (Integer) this.getSqlSession().selectOne(namespace + ".totalSizeByParams", params);
	}
}
