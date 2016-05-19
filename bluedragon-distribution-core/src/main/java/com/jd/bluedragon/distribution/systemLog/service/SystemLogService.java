package com.jd.bluedragon.distribution.systemLog.service;

import java.util.List;
import java.util.Map;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

public interface SystemLogService {

	int add(SystemLog systemLog);

	int add(String keyword1, String keyword2, String keyword3, Long keyword4,
			String content, Long type);

	List<SystemLog> queryByParams(Map<String, Object> paramMap);

	Integer totalSizeByParams(Map<String, Object> params);
	
	public Integer totalSize(String code);
	
	public List<SystemLog> queryByCassandra(String code ,Pager<SystemLog> pager);
}
