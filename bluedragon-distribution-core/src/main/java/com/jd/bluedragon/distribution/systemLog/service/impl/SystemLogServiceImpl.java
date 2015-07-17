package com.jd.bluedragon.distribution.systemLog.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.systemLog.dao.SystemLogDao;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.systemLog.service.SystemLogService;

/**
 * 系统日志记录工具
 * @author huangliang
 *
 */
@Service("SystemLogService")
public class SystemLogServiceImpl implements SystemLogService {

	private final static Logger logger = Logger.getLogger(SystemLogServiceImpl.class);

	@Autowired
	private SystemLogDao systemLogDao;

	@Profiled(tag = "SystemLogService.add")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(SystemLog systemLog) {
		try {
			byte[] contentBytes = systemLog.getContent().getBytes();
			if(contentBytes.length>4000) systemLog.setContent(new String(contentBytes, 0, 4000));
			return systemLogDao.add(SystemLogDao.namespace, systemLog);
		} catch (Exception e) {
			logger.error("插入操作日志失败，失败信息为：" + e.getMessage(), e);
			return 0;
		}
	}

	@Override
	@Profiled(tag = "SystemLogService.addByDetail")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int add(String keyword1, String keyword2, String keyword3, Long keyword4, String content, Long type) {
		SystemLog systemLog = new SystemLog();
		systemLog.setKeyword1(keyword1);
		systemLog.setKeyword2(keyword2);
		systemLog.setKeyword3(keyword3);
		systemLog.setKeyword4(keyword4);
		systemLog.setContent(content);
		systemLog.setType(type);
		
		return add(systemLog);
	}
	
	@Profiled(tag = "SystemLogService.queryByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<SystemLog> queryByParams(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return systemLogDao.queryByParams(params);
	}

	@Profiled(tag = "SystemLogService.totalSizeByParams")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer totalSizeByParams(Map<String, Object> params) {
		return systemLogDao.totalSizeByParams(params);
	}

}
