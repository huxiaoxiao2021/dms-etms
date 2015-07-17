package com.jd.bluedragon.distribution.offline.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.offline.domain.OfflineLog;

public interface OfflineLogService {
	
	Integer addOfflineLog(OfflineLog offlineLog);
	Integer totalSizeByParams(Map<String, Object> params);
	List<OfflineLog> queryByParams(Map<String, Object> paramMap);
	OfflineLog findByObj(OfflineLog offlineLog);
	Integer update(OfflineLog offlineLog); 
}
