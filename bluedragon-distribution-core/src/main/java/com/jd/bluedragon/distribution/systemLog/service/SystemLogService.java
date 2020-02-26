package com.jd.bluedragon.distribution.systemLog.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

import java.util.List;
import java.util.Map;

/**
 * 不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 * com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 */
@Deprecated
public interface SystemLogService {

	int add(SystemLog systemLog);

	int add(String keyword1, String keyword2, String keyword3, Long keyword4,
			String content, Long type);

	List<SystemLog> queryByParams(Map<String, Object> paramMap);

	Integer totalSizeByParams(Map<String, Object> params);
	
	public Integer totalSize(String code);
	
	public List<SystemLog> queryByCassandra(String code ,Pager<SystemLog> pager);
}
