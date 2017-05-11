package com.jd.bluedragon.distribution.admin.service.impl;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.admin.service.WorkerMonitorService;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;

@Service("workerMonitorService")
public class WorkerMonitorServiceImpl implements WorkerMonitorService {

	private static final Logger logger = Logger.getLogger(WorkerMonitorServiceImpl.class);

	@Autowired
	private TaskDao taskDao;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public List<Task> findPageTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.findPageTask begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findPageTask(params);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public Integer findCountTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.findCountTask begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findCountTask(params);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public List<Integer> findTaskTypeByTableName(String tableName) {
		logger.info("WorkerMonitorServiceImpl.findTaskTypeByTableName begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findTaskTypeByTableName(tableName);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateTaskById(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.updateTaskById begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.updateTaskById(params);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateBatchTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.updateBatchTask begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.updateBatchTask(params);
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

}
