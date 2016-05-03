package com.jd.bluedragon.distribution.operationLog.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.cassandra.OperationlogCassandra;
import com.jd.bluedragon.distribution.operationLog.dao.OperationLogReadDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.ql.dcam.config.ConfigManager;
import com.jd.ump.annotation.JProfiler;

@Service
public class OperationLogServiceImpl implements OperationLogService {

	private final static Logger logger = Logger.getLogger(OperationLogServiceImpl.class);

	@Autowired
	private OperationlogCassandra logCassandra;
	
	@Autowired
	private OperationLogReadDao operationLogReadDao;
	
	@Autowired
	private ConfigManager configManager;
	
	//cassandra开关
	public static final String CASSANDRA_GLOBAL_ON_KEY = "cassandra.global.on";

	@JProfiler(jKey= "DMSWEB.OperationLogService.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(OperationLog operationLog) {
		try {
			String cassandraOn = configManager.getProperty(CASSANDRA_GLOBAL_ON_KEY);
			if(cassandraOn.equalsIgnoreCase("true")){
				logCassandra.batchInsert(operationLog);
			}
		} catch (Exception e) {
			logger.error("插入操作日志失败，失败信息为：" + e.getMessage(), e);
		}
		return 1;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<OperationLog> queryByParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return operationLogReadDao.queryByParams(params);
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer totalSizeByParams(Map<String, Object> params) {
		return operationLogReadDao.totalSizeByParams(params);
	}
	
	public List<OperationLog> queryByCassandra(String code ,String type ,Pager<OperationLog> pager) {
		return logCassandra.getPage(code ,type , pager);
	}

	@Override
	public int totalSize(String code, String type) {
		return logCassandra.totalSize(code ,type);
	}

}
