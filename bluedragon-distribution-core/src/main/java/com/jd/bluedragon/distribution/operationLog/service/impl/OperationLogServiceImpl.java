package com.jd.bluedragon.distribution.operationLog.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.cassandra.OperationlogCassandra;
import com.jd.bluedragon.distribution.operationLog.dao.OperationLogDao;
import com.jd.bluedragon.distribution.operationLog.dao.OperationLogReadDao;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.utils.StringHelper;

@Service
public class OperationLogServiceImpl implements OperationLogService {

	private final static Logger logger = Logger.getLogger(OperationLogServiceImpl.class);

	@Autowired
	private OperationLogDao operationLogDao;
	
	@Autowired
	private OperationlogCassandra logCassandra;
	
	@Autowired
	private OperationLogReadDao operationLogReadDao;
	
	@Autowired
	private BaseService baseService;
	
	//cassandra开关
	public static final String CASSANDRA_SWITCH = "CASSANDRA_SWITCH";

	@Profiled(tag = "OperationLogService.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(OperationLog operationLog) {
		try {
			
			List<SysConfig> configs=baseService.queryConfigByKeyWithCache(CASSANDRA_SWITCH);
			for(SysConfig sys : configs){
				if(StringHelper.matchSiteRule(sys.getConfigContent(), "CASSANDRA_ON")){
					logCassandra.batchInsert(operationLog);
				}
			}
			
			return operationLogDao.add(OperationLogDao.namespace, operationLog);
			
		} catch (Exception e) {
			logger.error("插入操作日志失败，失败信息为：" + e.getMessage(), e);
			return 0;
		}
	}

	@Profiled(tag = "OperationLogService.queryByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<OperationLog> queryByParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return operationLogReadDao.queryByParams(params);
	}

	@Profiled(tag = "OperationLogService.totalSizeByParams")
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
