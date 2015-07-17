package com.jd.bluedragon.distribution.offline.service.impl;

import java.util.List;
import java.util.Map;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.offline.dao.OfflineDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
@Service("offlineLogService")
public class OfflineLogServiceImpl implements OfflineLogService{
	@Autowired
	private OfflineDao offlineDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer addOfflineLog(OfflineLog offlineLog) {
		return offlineDao.add(OfflineDao.namespace, offlineLog);
	}

	@Override
	@Profiled(tag = "offlineLogService.totalSizeByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer totalSizeByParams(Map<String, Object> params){
		return offlineDao.totalSizeByParams(params);
	}
	
	@Override
	@Profiled(tag = "offlineLogService.queryByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<OfflineLog> queryByParams(Map<String, Object> params) {
		return offlineDao.queryByParams(params);
	}
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public OfflineLog findByObj(OfflineLog offlineLog) {
		return offlineDao.findByObj(offlineLog);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(OfflineLog offlineLog) {
		return offlineDao.updateById(offlineLog);
	}
	
}
