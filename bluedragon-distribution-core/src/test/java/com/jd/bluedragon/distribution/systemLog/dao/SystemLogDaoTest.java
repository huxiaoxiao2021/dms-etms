package com.jd.bluedragon.distribution.systemLog.dao;

import com.jd.bluedragon.utils.ObjectMapHelper;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

import java.util.Date;
import java.util.Map;

public class SystemLogDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SystemLogDao systemLogDao;
	
	
	@Test
    public void testAdd() {
        SystemLog systemLog = new SystemLog();
        systemLog.setContent("test");
        systemLog.setCreateTime(new Date());
        systemLog.setKeyword1("12343");
        systemLog.setKeyword2("fdsfdsaf");
        systemLog.setKeyword3("fdsafd");
        systemLog.setKeyword4(123456789l);
        systemLog.setType(40l);
        Integer i = systemLogDao.add(SystemLogDao.class.getName(), systemLog);
        Assert.assertTrue("插入系统日志", i > 0);
    }
	
	@Test
    public void testTotalSizeByParams() {
        SystemLog parameter = new SystemLog();
        parameter.setId((long)8528);
        parameter.setKeyword1("Stone");
        parameter.setKeyword2("Jax");
        parameter.setKeyword3("Jim");
        parameter.setKeyword4((long)1577);
        parameter.setContent("Mary");
        parameter.setType((long)3043);
        Map<String,Object> param = ObjectMapHelper.makeObject2Map(parameter);
        systemLogDao.totalSizeByParams(param);
    }
	
	@Test
    public void testQueryByParams() {
        SystemLog parameter = new SystemLog();
        parameter.setId((long)1118);
        parameter.setKeyword1("Jax");
        parameter.setKeyword2("Jone");
        parameter.setKeyword3("James");
        parameter.setKeyword4((long)9180);
        parameter.setContent("Jax");
        parameter.setType((long)7715);
        Map<String,Object> param = ObjectMapHelper.makeObject2Map(parameter);
        param.put("startIndex",1L);
        param.put("pageSize", 2L);
        systemLogDao.queryByParams(param);
    }
}
