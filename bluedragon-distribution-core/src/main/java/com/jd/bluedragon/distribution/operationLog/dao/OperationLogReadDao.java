package com.jd.bluedragon.distribution.operationLog.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;

public class OperationLogReadDao extends BaseDao<OperationLog> {

	public static final String namespace = OperationLogReadDao.class.getName();

	@SuppressWarnings("unchecked")
	public List<OperationLog> queryByParams(Map<String, Object> params) {
		return (List<OperationLog>) this.getSqlSessionRead().selectList(namespace + ".queryByParams", params);
	}

	public Integer totalSizeByParams(Map<String, Object> params) {
		return (Integer) this.getSqlSessionRead().selectOne(namespace + ".totalSizeByParams", params);
	}
}
