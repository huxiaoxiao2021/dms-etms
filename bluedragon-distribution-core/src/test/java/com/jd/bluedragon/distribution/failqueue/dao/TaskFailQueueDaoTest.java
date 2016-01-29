package com.jd.bluedragon.distribution.failqueue.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskFailQueueDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TaskFailQueueDao taskFailQueueDao;
	
	
	@Test
    public void testQuery() {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("fetchNum", 2);
		param.put("busiType", 12);
        taskFailQueueDao.query(param);
    }
	
	@Test
    public void testQuerySendDatail_SELF() {
		List<String> param =new ArrayList<String>();
		param.add("1111");
        taskFailQueueDao.querySendDatail_SELF(param);
    }
	
	@Test
    public void testQuerySendCodeByBusiId() {
		List<Long> param = new ArrayList<Long>();
		param.add((long)3437);
        taskFailQueueDao.querySendCodeByBusiId(param);
    }
	
	@Test
    public void testUpdateSuccess() {
        Long id = (long)1;
        taskFailQueueDao.updateSuccess(id);
    }
	
	@Test
    public void testQueryBatch() {
		List<Long> param = new ArrayList<Long>();
		param.add((long)3437);
        taskFailQueueDao.queryBatchByBusiId(param);
    }
	
	@Test
    public void testUpdateLock() {
        Long id = (long)1;
        taskFailQueueDao.updateLock(id);
    }
	
	@Test
    public void testAdd() {
        TaskFailQueue parameter = new TaskFailQueue();
        parameter.setBusiId((long)3437);
        parameter.setBusiType(4);
        parameter.setBody("James");
        taskFailQueueDao.add(TaskFailQueueDao.namespace ,parameter);
    }
	
	@Test
    public void testUpdate() {
        TaskFailQueue parameter = new TaskFailQueue();
        parameter.setBusiType(12);
        parameter.setBody("Jone");
        parameter.setBusiId((long)3437);
        taskFailQueueDao.update(TaskFailQueueDao.namespace ,parameter);
    }
	
	@Test
    public void testQuerySendDatail_3PL() {
        List parameter = new ArrayList();
        parameter.add("James");
        taskFailQueueDao.querySendDatail_3PL(parameter);
    }
	
	@Test
    public void testUpdateFail() {
        Long id = (long)1;
        taskFailQueueDao.updateFail(id);
    }
}
