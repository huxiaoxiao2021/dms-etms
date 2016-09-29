package com.jd.bluedragon.distribution.failqueue.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.SerialRuleUtil;

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
	public List<SendDetail> querySendDatailBySendCodes_SELF(List<String> sendCodes){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendCodeList", sendCodes);
		List<Integer> dmsCodes = new ArrayList<Integer>();
		for (String sendCode : sendCodes) {
			Integer createSiteCode  = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
			if(null!=createSiteCode)
				dmsCodes.add(createSiteCode);
		}
		params.put("dmsList", dmsCodes);
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendDatail_SELF", params);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> querySendDatailBySendCodes_3PL(List<String> sendCodes){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendCodeList", sendCodes);
		List<Integer> dmsCodes = new ArrayList<Integer>();
		for (String sendCode : sendCodes) {
			Integer createSiteCode  = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
			if(null!=createSiteCode)
				dmsCodes.add(createSiteCode);
		}
		params.put("dmsList", dmsCodes);
		return super.getSqlSession().selectList(TaskFailQueueDao.namespace + ".querySendDatail_3PL", params);
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
