package com.jd.bluedragon.distribution.operationLog.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;

public class OperationLogDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private OperationLogDao operationLogDao;
	
	
	@Test
    public void testTotalSizeByParams() {
        OperationLog parameter = new OperationLog();
        parameter.setLogId((long)7186);
        parameter.setBoxCode("Jim");
        parameter.setWaybillCode("Stone");
        parameter.setPickupCode("Joe");
        parameter.setPackageCode("Jax");
        parameter.setLogType(710);
        parameter.setSendCode("Stone");
        parameter.setCreateUserCode(322);
        parameter.setCreateUser("Jim");
        parameter.setCreateSiteCode(217);
        parameter.setCreateSiteName("Mary");
        parameter.setReceiveSiteCode(3);
        parameter.setReceiveSiteName("Joe");
        parameter.setRemark("Mary");
        operationLogDao.totalSizeByParams(parameter);
    }
	
	@Test
    public void testQueryByParams() {
        OperationLog parameter = new OperationLog();
        parameter.setLogId((long)2589);
        parameter.setBoxCode("Jax");
        parameter.setWaybillCode("Mary");
        parameter.setPickupCode("Stone");
        parameter.setPackageCode("James");
        parameter.setLogType(207);
        parameter.setSendCode("Jim");
        parameter.setCreateUserCode(69);
        parameter.setCreateUser("Jax");
        parameter.setCreateSiteCode(838);
        parameter.setCreateSiteName("Jim");
        parameter.setReceiveSiteCode(721);
        parameter.setReceiveSiteName("James");
        parameter.setRemark("Mary");
        // parameter.getEndIndex(new Object());
        // parameter.getStartIndex(new Object());
        operationLogDao.queryByParams(parameter);
    }
	
	@Test
    public void testAdd() {
        operationLogDao.add();
    }
}
