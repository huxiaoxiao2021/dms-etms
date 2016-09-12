package com.jd.bluedragon.distribution.task.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.jd.bluedragon.distribution.task.domain.Task;

public class TaskDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TaskDao taskDao;

    private String table_name="task_offline";
	
	@Test
    public void testFindSpecifiedTasks() {
        taskDao.findSpecifiedTasks(1120,50,"DMS");
    }
	
	@Test
    public void testFindCountTask() {
        Map parameter = new HashMap();
        parameter.put("tableName", table_name);
        parameter.put("taskType", 1120);
        parameter.put("taskStatus", 0);
        parameter.put("executeCount", 0);
        parameter.put("keyword1", "James");
        parameter.put("keyword2","James");
        parameter.put("startTime", new Date(100,1,1,1,1));
        parameter.put("endTime", new Date());
        taskDao.findCountTask(parameter);
    }
	
	@Test
    public void testFindLimitedTasks() {
        taskDao.findLimitedTasks(10);
    }
	
	@Test
    public void testUpdateBySelective() {
        Task parameter = new Task();
        parameter.setTableName(table_name);
        parameter.setExecuteCount(1);
        parameter.setExecuteTime(new Date());
        parameter.setStatus(1);
        parameter.setYn(1);
        parameter.setId((long) 1);
        parameter.setType(1120);
        taskDao.updateBySelective(parameter);
    }
	
	@Test
    public void testFindWaybillSendTask() {
        Map parameter = new HashMap();
        parameter.put("body", new Object());
        taskDao.findWaybillSendTask("James");
    }
	
	@Test
    public void testFindFailTasksNumsByType() {
        Map parameter = new HashMap();
        parameter.put("tableName", new Object());
        parameter.put("type", new Object());
        parameter.put("ownSign", new Object());
        taskDao.findFailTasksNumsByType(1120,"James");
    }
	
	@Test
    public void testFindPageTask() {
        Map parameter = new HashMap();
        parameter.put("tableName",table_name);
        parameter.put("taskType", 1120);
        parameter.put("taskStatus", 1);
        parameter.put("executeCount", 1);
        parameter.put("keyword1", "James");
        parameter.put("keyword2", "James");
        parameter.put("startTime", new Date(100,1,1,1,1));
        parameter.put("endTime", new Date());
        parameter.put("startIndex", 1);
        parameter.put("pageSize",2);
        taskDao.findPageTask(parameter);
    }
	
	@Test
    public void testFindTasksStatusByBoxcode() {
//        Map parameter = new HashMap();
//        parameter.put("tableName", new Object());
//        parameter.put("statuses", new Object());
//        parameter.put("boxCode", new Object());
//        parameter.put("createSiteCode", new Object());
//        taskDao.findTasksStatusByBoxcode(parameter);
    }
	
	@Test
    public void testFindTasksNumsByType() {
        Map parameter = new HashMap();
        parameter.put("tableName", new Object());
        parameter.put("type", new Object());
        parameter.put("ownSign", new Object());
        taskDao.findTasksNumsByType(1120,"DMS");
    }
	
	@Test
    public void testFindTasksByFingerprint() {
        Task task=new Task();
        task.setType(1120);
        task.setTableName(table_name);
        task.setOwnSign("DMS");
        taskDao.findTasksByFingerprint(task);
    }
	
	@Test
    public void testFindTasks() {
        Task task=new Task();
        task.setType(1120);
        task.setTableName(table_name);
        task.setOwnSign("DMS");
        task.setStatuses("'3,4'");
        taskDao.findTasks(task);
    }
	
	@Test
    public void testAddWithStatus() {
        Task parameter = new Task();
        parameter.setTableName(table_name);
        parameter.setSequenceName("Joe");
        parameter.setType(1120);
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setBoxCode("James");
        parameter.setBody("James");
        parameter.setKeyword1("James");
        parameter.setKeyword2("James");
        parameter.setExecuteCount(1);
        parameter.setFingerprint("James");
        parameter.setStatus(1);
        parameter.setOwnSign("DMS");
        taskDao.addWithStatus(parameter);
    }
	
	@Test
    public void testAdd() {
        Task parameter = new Task();
        parameter.setTableName(table_name);
        parameter.setSequenceName("James");
        parameter.setType(1120);
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setBoxCode("James");
        parameter.setBody("James");
        parameter.setKeyword1("James");
        parameter.setKeyword2("James");
        parameter.setFingerprint("James");
        parameter.setOwnSign("James");
        taskDao.add(TaskDao.class.getName(),parameter);
    }
	
	@Test
    public void testFindReverseSendTask() {
        Map parameter = new HashMap();
        parameter.put("body", new Object());
        taskDao.findReverseSendTask("James");
    }
	
	@Test
    public void testFindSendTasks() {
        Map parameter = new HashMap();
        parameter.put("tableName", table_name);
        parameter.put("type", 1120);
        parameter.put("key", new Object());
        parameter.put("fetchNum", new Object());
        taskDao.findSendTasks(1120,10,"910");
    }
	
	@Test
    public void testFindTaskTypeByTableName() {
        Map parameter = new HashMap();
        parameter.put("tableName", new Object());
        taskDao.findTaskTypeByTableName(table_name);
    }
	
	@Test
    public void testUpdateTaskById() {
        Map parameter = new HashMap();
        parameter.put("tableName", table_name);
        parameter.put("id", 1);
        taskDao.updateTaskById(parameter);
    }
	
	@Test
    public void testUpdateBatchTask() {
        Map parameter = new HashMap();
        parameter.put("tableName",table_name);
        parameter.put("taskType", 1120);
        parameter.put("taskStatus",1);
        parameter.put("executeCount",1);
        parameter.put("keyword1", table_name);
        parameter.put("keyword2",table_name);
        parameter.put("startTime", new Date(100,1,1,1,1));
        parameter.put("endTime", new Date());
        taskDao.updateBatchTask(parameter);
    }
	
	@Test
    public void testFindLimitedTasksByType() {
//        Map parameter = new HashMap();
//        parameter.put("tableName", new Object());
//        parameter.put("type", new Object());
//        parameter.put("ownSign", new Object());
//        parameter.put("fetchNum", new Object());
//        taskDao.findLimitedTasksByType(parameter);
    }
}
