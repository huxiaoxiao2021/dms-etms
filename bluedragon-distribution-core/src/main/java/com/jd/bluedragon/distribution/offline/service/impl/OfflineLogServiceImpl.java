package com.jd.bluedragon.distribution.offline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.offline.dao.OfflineDao;
import com.jd.bluedragon.distribution.offline.domain.OfflineLog;
import com.jd.bluedragon.distribution.offline.service.OfflineLogService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Service("offlineLogService")
public class OfflineLogServiceImpl implements OfflineLogService{

	private final Logger log = LoggerFactory.getLogger(OfflineLogServiceImpl.class);

	@Autowired
	private OfflineDao offlineDao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer addOfflineLog(OfflineLog offlineLog) {
		if (offlineLog.getBoxCode() != null && offlineLog.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT) {
			log.warn("箱号超长，无法插入任务，参数：{}" , JsonHelper.toJson(offlineLog));
			return -1;
		}
		return offlineDao.add(OfflineDao.namespace, offlineLog);
	}

	@Override
	public Integer totalSizeByParams(Map<String, Object> params){
		return offlineDao.totalSizeByParams(params);
	}
	
	@Override
	public List<OfflineLog> queryByParams(Map<String, Object> params) {
		return offlineDao.queryByParams(params);
	}
	
	@Override
	public OfflineLog findByObj(OfflineLog offlineLog) {
		return offlineDao.findByObj(offlineLog);
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(OfflineLog offlineLog) {
		return offlineDao.updateById(offlineLog);
	}
	
}
