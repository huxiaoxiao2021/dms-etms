package com.jd.bluedragon.distribution.failqueue.dao;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

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
	public List<SendDetail> querySendDatail_SELF(List<String> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendDatail_SELF", param);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> querySendDatail_3PL(List<String> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendDatail_3PL", param);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> queryBatchByBusiId(List<Long> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".queryBatch", param);
	}
	
	@SuppressWarnings("unchecked")
    public Integer add(List<TaskFailQueue> param) {
        return this.getSqlSession().insert(TaskFailQueueDao.namespace + ".batchInsert", param);
    }
	
	@SuppressWarnings("unchecked")
	public void updateLock(Long id){
		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateLock", id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> querySendCodeByBusiId(List<Long> param){
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendCodeByBusiId", param);
	}
	
	
	
//	public void updateSendDatailSuccess(Long id){
//		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateSendDatailSuccess", id);
//	}
//	
//	public void updateSendDatailFail(Long id){
//		super.getSqlSession().update(TaskFailQueueDao.namespace + ".updateSendDatailFail", id);
//	}
	
//	public void udpateSendDatailToFinance_self(String send_code){
//		super.getSqlSession().update(TaskFailQueueDao.namespace + ".udpateSendDatailToFinance_self", send_code);
//	}
}
