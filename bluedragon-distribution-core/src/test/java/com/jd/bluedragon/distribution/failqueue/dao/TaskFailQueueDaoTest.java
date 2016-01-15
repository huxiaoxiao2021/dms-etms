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
import java.util.List;

public class TaskFailQueueDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TaskFailQueueDao taskFailQueueDao;
	
	
	@Test
    public void testQuery() {
        Map parameter = new HashMap();
        // parameter.put("fetchNum", new Object());
        // parameter.put("busiType", new Object());
        taskFailQueueDao.query(parameter);
    }
	
	@Test
    public void testBatchInsert() {
        List parameter = new ArrayList();
        //set property for item.busiId
        //set property for item.busiType
        //set property for item.body
        taskFailQueueDao.batchInsert(parameter);
    }
	
	@Test
    public void testQuerySendDatail_SELF() {
        List parameter = new ArrayList();
        // parameter.getItems(new Object());
        taskFailQueueDao.querySendDatail_SELF(parameter);
    }
	
	@Test
    public void testQuerySendCodeByBusiId() {
        List parameter = new ArrayList();
        // parameter.getItems(new Object());
        taskFailQueueDao.querySendCodeByBusiId(parameter);
    }
	
	@Test
    public void testUpdateSuccess() {
        Long id = (long)8385;
        taskFailQueueDao.updateSuccess(id);
    }
	
	@Test
    public void testQueryBatch() {
        List parameter = new ArrayList();
        // parameter.getItems(new Object());
        taskFailQueueDao.queryBatch(parameter);
    }
	
	@Test
    public void testUpdateLock() {
        Long id = (long)5282;
        taskFailQueueDao.updateLock(id);
    }
	
	@Test
    public void testAdd() {
        TaskFailQueue parameter = new TaskFailQueue();
        parameter.setBusiId((long)3437);
        parameter.setBusiType(554);
        parameter.setBody("James");
        taskFailQueueDao.add(parameter);
    }
	
	@Test
    public void testUpdate() {
        TaskFailQueue parameter = new TaskFailQueue();
        parameter.setBusiType(378);
        parameter.setBody("Jone");
        parameter.setBusiId((long)4247);
        taskFailQueueDao.update(parameter);
    }
	
	@Test
    public void testQuerySendDatail_3PL() {
        List parameter = new ArrayList();
        // parameter.getItems(new Object());
        taskFailQueueDao.querySendDatail_3PL(parameter);
    }
	
	@Test
    public void testUpdateFail() {
        Long id = (long)9694;
        taskFailQueueDao.updateFail(id);
    }
}
