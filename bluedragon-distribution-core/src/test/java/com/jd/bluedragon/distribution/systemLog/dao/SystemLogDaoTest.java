package com.jd.bluedragon.distribution.systemLog.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

public class SystemLogDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SystemLogDao systemLogDao;
	
	
	@Test
    public void testAdd() {
        systemLogDao.add();
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
        systemLogDao.totalSizeByParams(parameter);
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
        // parameter.getEndIndex(new Object());
        // parameter.getStartIndex(new Object());
        systemLogDao.queryByParams(parameter);
    }
}
