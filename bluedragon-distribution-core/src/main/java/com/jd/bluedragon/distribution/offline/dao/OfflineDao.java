package com.jd.bluedragon.distribution.offline.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;

public class OfflineDao extends BaseDao<OfflineLog> {
	public static final String namespace = OfflineDao.class.getName();
	
	public Integer totalSizeByParams(Map<String, Object> params){
		return (Integer)this.getSqlSessionRead().selectOne(namespace + ".totalSizeByParams", params);
	}
	
	@SuppressWarnings("unchecked")
	public List<OfflineLog> queryByParams(Map<String, Object> params){
		return (List<OfflineLog>) this.getSqlSessionRead().selectList(namespace + ".queryByParams", params);
	}
	
	public OfflineLog findByObj(OfflineLog offlineLog) {
		Object obj = this.getSqlSession().selectOne(namespace + ".findByObj", offlineLog);
		offlineLog = (obj == null) ? null : (OfflineLog)obj;
		return offlineLog;
	}
	
	public Integer updateById(OfflineLog offlineLog) {
		return (Integer)this.getSqlSession().update(namespace + ".updateById", offlineLog);
	}
}
