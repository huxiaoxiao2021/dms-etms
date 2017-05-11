package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.distribution.offline.dao.OfflineDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public Integer totalSizeByParams(Map<String, Object> params){
		return offlineDao.totalSizeByParams(params);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public List<OfflineLog> queryByParams(Map<String, Object> params) {
		return offlineDao.queryByParams(params);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public OfflineLog findByObj(OfflineLog offlineLog) {
		return offlineDao.findByObj(offlineLog);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(OfflineLog offlineLog) {
		return offlineDao.updateById(offlineLog);
	}
	
}
