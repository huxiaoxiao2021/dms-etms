package com.jd.bluedragon.distribution.operationLog.service.impl;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.operationLog.dao.OperationLogReadDao;
import com.jd.bluedragon.distribution.operationLog.dao.OperationlogCassandra;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OperationLogServiceImpl implements OperationLogService {

	private final static Logger log = LoggerFactory.getLogger(OperationLogServiceImpl.class);

	@Autowired
	private OperationlogCassandra logCassandra;
	
	@Autowired
	private OperationLogReadDao operationLogReadDao;
	
	@Resource
	private UccPropertyConfiguration uccPropertyConfiguration;

	/**
	 * 记录log到cassandra中
	 * @param operationLog
	 * @return int
     */
	public int add(OperationLog operationLog) {
		//增加侵入式监控开始
		CallerInfo info = null;
		try {
			info = Profiler.registerInfo("DMSWEB.OperationLogService.add", false, true);
			if(uccPropertyConfiguration.getCassandraGlobalSwitch()){
				logCassandra.batchInsert(operationLog);
			}
		} catch (Throwable e) {
			log.error("插入操作日志失败，失败信息为：{}" , e.getMessage(), e);
			Profiler.functionError(info);
		}finally {
			Profiler.registerInfoEnd(info);
		}
		//监控结束
		return 1;
	}

	public List<OperationLog> queryByParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return operationLogReadDao.queryByParams(params);
	}

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
