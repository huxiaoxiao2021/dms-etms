package com.jd.bluedragon.distribution.failqueue.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;

import java.util.List;
import java.util.Map;

public class TaskFailQueueDao extends BaseDao<TaskFailQueue> {
	public static final String namespace = TaskFailQueueDao.class.getName();
	
	@SuppressWarnings("unchecked")
	public List<TaskFailQueue> query(Map<String,Object> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".query", param);
	}
	
	public void updateFail(Long id){
		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateFail", id);
	}
	
	public void updateSuccess(Long id){
		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateSuccess", id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> queryBatchByBusiId(List<Long> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".queryBatch", param);
	}
	
	@SuppressWarnings("unchecked")
	public void updateLock(Long id){
		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateLock", id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> querySendCodeByBusiId(List<Long> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendCodeByBusiId", param);
	}

}
