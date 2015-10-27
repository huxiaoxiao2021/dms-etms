package com.jd.bluedragon.distribution.admin.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.admin.service.WorkerMonitorService;
import com.jd.bluedragon.distribution.task.dao.TaskDao;
import com.jd.bluedragon.distribution.task.domain.Task;

public class WorkerMonitorServiceImpl implements WorkerMonitorService {

	private static final Logger logger = Logger.getLogger(WorkerMonitorServiceImpl.class);

	private TaskDao taskDao;

    private TaskDao mysqlTaskDao;
    
    private Set mysqlTableSet;
    
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Task> findPageTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.findPageTask begin...");
		String tableName = params.get("tableName").toString();
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(tableName)){
    		routerDao = mysqlTaskDao;
    	}
		return routerDao.findPageTask(params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Integer findCountTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.findCountTask begin...");
		String tableName = params.get("tableName").toString();
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(tableName)){
    		routerDao = mysqlTaskDao;
    	}
		return routerDao.findCountTask(params);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Integer> findTaskTypeByTableName(String tableName) {
		logger.info("WorkerMonitorServiceImpl.findTaskTypeByTableName begin...");
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(tableName)){
    		routerDao = mysqlTaskDao;
    	}
		return routerDao.findTaskTypeByTableName(tableName);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateTaskById(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.updateTaskById begin...");
		String tableName = params.get("tableName").toString();
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(tableName)){
    		routerDao = mysqlTaskDao;
    	}
		return routerDao.updateTaskById(params);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer updateBatchTask(Map<String, Object> params) {
		logger.info("WorkerMonitorServiceImpl.updateBatchTask begin...");
		String tableName = params.get("tableName").toString();
		TaskDao routerDao = taskDao;    	
    	if(mysqlTableSet.contains(tableName)){
    		routerDao = mysqlTaskDao;
    	}
		return routerDao.updateBatchTask(params);
	}

	public TaskDao getTaskDao() {
		return taskDao;
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public TaskDao getMysqlTaskDao() {
		return mysqlTaskDao;
	}

	public void setMysqlTaskDao(TaskDao mysqlTaskDao) {
		this.mysqlTaskDao = mysqlTaskDao;
	}

	public Set getMysqlTableSet() {
		return mysqlTableSet;
	}

	public void setMysqlTableSet(Set mysqlTableSet) {
		this.mysqlTableSet = mysqlTableSet;
	}

}
