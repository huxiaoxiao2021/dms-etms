package com.jd.bluedragon.distribution.operationLog.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;

import java.util.List;
import java.util.Map;

/**
 * 1.不要删除此类；删除前请确认 实现类中 logEngine接口保存的日志是否还需要或者迁移走。
 *
 * 2.不要使用此接口保存日志了。请使用统一的日志日志接口com.jd.bluedragon.distribution.log.impl.LogEngineImpl。
 *  com.jd.bluedragon.distribution.log.impl.LogEngineImpl 此接口保存的日志会存储到business.log.jd.com 中;
 */
@Deprecated
public interface OperationLogService {
	
	int add(OperationLog operationLog);

	List<OperationLog> queryByParams(Map<String, Object> paramMap);

	Integer totalSizeByParams(Map<String, Object> params);
	
	List<OperationLog> queryByCassandra(String code,String type ,Pager<OperationLog> pager);
	
	int totalSize(String code,String type);
}
