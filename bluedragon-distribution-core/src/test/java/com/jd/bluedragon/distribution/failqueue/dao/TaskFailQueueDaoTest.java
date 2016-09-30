package com.jd.bluedragon.distribution.failqueue.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.failqueue.domain.TaskFailQueue;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/spring/distribution-core-context.xml"})
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
		param.add("James");
		List<SendDetail> result = taskFailQueueDao.querySendDatailBySendCodes_SELF(param);
		Assert.assertEquals(0, result.size());
		
		param.clear();
		param.add("910-311-20160126202034466");
		result = taskFailQueueDao.querySendDatailBySendCodes_SELF(param);
		Assert.assertEquals("910-311-20160126202034466", result.get(0).getSendCode());
    }
	
	@Test
    public void testQuerySendDatail_3PL() {
        List parameter = new ArrayList();
        parameter.add("James");
        List<SendDetail> result = taskFailQueueDao.querySendDatailBySendCodes_3PL(parameter);
        Assert.assertEquals(0, result.size());
        
        parameter.clear();
        parameter.add("511-1-20120925100935659");
		result = taskFailQueueDao.querySendDatailBySendCodes_3PL(parameter);
		Assert.assertEquals("511-1-20120925100935659", result.get(0).getSendCode());
		
        parameter.clear();
        parameter.add("511201203280756360");
		result = taskFailQueueDao.querySendDatailBySendCodes_3PL(parameter);
		Assert.assertEquals("511201203280756360", result.get(0).getSendCode());
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
    public void testUpdateFail() {
        Long id = (long)1;
        taskFailQueueDao.updateFail(id);
    }
}
