package com.jd.bluedragon.distribution.popReveice.dao;

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
import com.jd.bluedragon.distribution.popReveice.domain.TaskPopRecieveCount;

public class TaskPopRecieveCountDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TaskPopRecieveCountDao taskPopRecieveCountDao;
	
	
	@Test
    public void testFindLimitedTasksByType() {
        Map parameter = new HashMap();
        // parameter.put("tableName", new Object());
        // parameter.put("type", new Object());
        // parameter.put("ownSign", new Object());
        // parameter.put("fetchNum", new Object());
//        taskPopRecieveCountDao.findLimitedTasksByType(parameter);
    }
	
	@Test
    public void testUpdate() {
        TaskPopRecieveCount parameter = new TaskPopRecieveCount();
        parameter.setWaybillCode("James");
        parameter.setThirdWaybillCode("Mary");
        parameter.setExpressCode("Jax");
        parameter.setExpressName("James");
        parameter.setActualNum(634);
        parameter.setOperateTime(new Date());
        parameter.setExecuteTime(new Date());
        parameter.setTaskStatus(10);
        parameter.setTaskType(446);
        parameter.setExecuteCount(118);
        parameter.setTaskId((long)7385);
        taskPopRecieveCountDao.update(parameter);
    }
	
	@Test
    public void testGetTaskPopRevieveCountById() {
        Long taskId = 883l;
        taskPopRecieveCountDao.getTaskPopRevieveCountById(taskId);
    }
	
	@Test
    public void testGetTaskPopRevieveCountByWaybillCode() {
        String waybillCode = "Mary";
        taskPopRecieveCountDao.getTaskPopRevieveCountByWaybillCode(waybillCode);
    }
	
	@Test
    public void testInsert() {
        TaskPopRecieveCount parameter = new TaskPopRecieveCount();
        parameter.setWaybillCode("Mary");
        parameter.setThirdWaybillCode("Jim");
        parameter.setExpressCode("Jone");
        parameter.setExpressName("James");
        parameter.setActualNum(194);
        parameter.setOperateTime(new Date());
        parameter.setTaskStatus(899);
        parameter.setTaskType(499);
        parameter.setExecuteCount(206);
        parameter.setOwnSign("Stone");
        taskPopRecieveCountDao.insert(parameter);
    }
}
