package com.jd.bluedragon.distribution.admin.service.impl;

import com.jd.bluedragon.distribution.admin.service.WorkerMonitorService;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service("workerMonitorService")
public class WorkerMonitorServiceImpl implements WorkerMonitorService {

	private static final Logger log = LoggerFactory.getLogger(WorkerMonitorServiceImpl.class);

	@Autowired
	private TaskDao taskDao;

	@Override
	public List<Task> findPageTask(Map<String, Object> params) {
		log.info("WorkerMonitorServiceImpl.findPageTask begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findPageTask(params);
	}

	@Override
	public Integer findCountTask(Map<String, Object> params) {
		log.info("WorkerMonitorServiceImpl.findCountTask begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findCountTask(params);
	}

	@Override
	public List<Integer> findTaskTypeByTableName(String tableName) {
		log.info("WorkerMonitorServiceImpl.findTaskTypeByTableName begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.findTaskTypeByTableName(tableName);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateTaskById(Map<String, Object> params) {
		log.info("WorkerMonitorServiceImpl.updateTaskById begin...");
		TaskDao routerDao = taskDao;    	
		return routerDao.updateTaskById(params);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateBatchTask(Map<String, Object> params) {
		log.info("WorkerMonitorServiceImpl.updateBatchTask begin...");
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
