package com.jd.bluedragon.distribution.operationLog.service;

import java.util.List;
import java.util.Map;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;

public interface OperationLogService {
	
	int add(OperationLog operationLog);

	List<OperationLog> queryByParams(Map<String, Object> paramMap);

	Integer totalSizeByParams(Map<String, Object> params);
	
	List<OperationLog> queryByCassandra(String code,String type);
}
