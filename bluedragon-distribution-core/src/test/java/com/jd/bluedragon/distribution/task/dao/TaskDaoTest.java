package com.jd.bluedragon.distribution.task.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import com.jd.bluedragon.distribution.task.domain.Task;

public class TaskDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TaskDao taskDao;
	
	
	@Test
    public void testFindSpecifiedTasks() {
        Map parameter = new HashMap();
        parameter.put("tableName", new Object());
        parameter.put("type", new Object());
        parameter.put("ownSign", new Object());
        parameter.put("fetchNum", new Object());
        taskDao.findSpecifiedTasks(parameter);
    }
	
	@Test
    public void testFindCountTask() {
//        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("taskType", new Object());
        // parameter.put("taskStatus", new Object());
        // parameter.put("executeCount", new Object());
        // parameter.put("keyword1", new Object());
        // parameter.put("keyword2", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        taskDao.findCountTask(parameter);
    }
	
	@Test
    public void testFindLimitedTasks() {
//        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("fetchNum", new Object());
//        taskDao.findLimitedTasks(parameter);
    }
	
	@Test
    public void testUpdateBySelective() {
        Task parameter = new Task();
        parameter.setTableName("Jone");
        parameter.setExecuteCount(85);
//        parameter.setExecuteTime(new Date());
        parameter.setStatus(345);
        parameter.setYn(524);
        parameter.setId((long)1796);
        taskDao.updateBySelective(parameter);
    }
	
	@Test
    public void testFindWaybillSendTask() {
//        Map parameter = new HashMap();
        // parameter.put("body", new Object());
//        taskDao.findWaybillSendTask(parameter);
    }
	
	@Test
    public void testFindFailTasksNumsByType() {
//        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("ownSign", new Object());
        taskDao.findFailTasksNumsByType(parameter);
    }
	
	@Test
    public void testFindPageTask() {
//        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("taskType", new Object());
        // parameter.put("taskStatus", new Object());
        // parameter.put("executeCount", new Object());
        // parameter.put("keyword1", new Object());
        // parameter.put("keyword2", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("startIndex", new Object());
        // parameter.put("pageSize", new Object());
        taskDao.findPageTask(parameter);
    }
	
	@Test
    public void testFindTasksStatusByBoxcode() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("statuses", new Object());
        // parameter.put("boxCode", new Object());
        // parameter.put("createSiteCode", new Object());
        taskDao.findTasksStatusByBoxcode(parameter);
    }
	
	@Test
    public void testFindTasksNumsByType() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("ownSign", new Object());
        taskDao.findTasksNumsByType(parameter);
    }
	
	@Test
    public void testFindTasksByFingerprint() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("fingerprint", new Object());
        taskDao.findTasksByFingerprint(parameter);
    }
	
	@Test
    public void testFindTasks() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("ownSign", new Object());
        taskDao.findTasks(parameter);
    }
	
	@Test
    public void testAddWithStatus() {
        Task parameter = new Task();
        parameter.setTableName("Joe");
        parameter.setSequenceName("Joe");
        parameter.setType(15);
        parameter.setCreateSiteCode(915);
        parameter.setReceiveSiteCode(77);
        parameter.setBoxCode("Mary");
        parameter.setBody("Mary");
        parameter.setKeyword1("Joe");
        parameter.setKeyword2("Joe");
        parameter.setExecuteCount(144);
        parameter.setFingerprint("James");
        parameter.setStatus(912);
        parameter.setOwnSign("Joe");
        taskDao.addWithStatus(parameter);
    }
	
	@Test
    public void testAdd() {
        Task parameter = new Task();
        parameter.setTableName("Stone");
        parameter.setSequenceName("Stone");
        parameter.setType(505);
        parameter.setCreateSiteCode(171);
        parameter.setReceiveSiteCode(879);
        parameter.setBoxCode("Mary");
        parameter.setBody("Stone");
        parameter.setKeyword1("Joe");
        parameter.setKeyword2("Mary");
        parameter.setFingerprint("Joe");
        parameter.setOwnSign("Jim");
        taskDao.add(TaskDao.class.getName(),parameter);
    }
	
	@Test
    public void testFindReverseSendTask() {
        Map parameter = new HashMap();
        // parameter.put("body", new Object());
        taskDao.findReverseSendTask(parameter);
    }
	
	@Test
    public void testFindSendTasks() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("key", new Object());
        // parameter.put("fetchNum", new Object());
        taskDao.findSendTasks(parameter);
    }
	
	@Test
    public void testFindTaskTypeByTableName() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        taskDao.findTaskTypeByTableName(parameter);
    }
	
	@Test
    public void testUpdateTaskById() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("id", new Object());
        taskDao.updateTaskById(parameter);
    }
	
	@Test
    public void testUpdateBatchTask() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("taskType", new Object());
        // parameter.put("taskStatus", new Object());
        // parameter.put("executeCount", new Object());
        // parameter.put("keyword1", new Object());
        // parameter.put("keyword2", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        taskDao.updateBatchTask(parameter);
    }
	
	@Test
    public void testFindLimitedTasksByType() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("ownSign", new Object());
        // parameter.put("fetchNum", new Object());
        taskDao.findLimitedTasksByType(parameter);
    }
}
