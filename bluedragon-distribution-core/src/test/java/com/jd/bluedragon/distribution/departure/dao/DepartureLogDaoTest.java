package com.jd.bluedragon.distribution.departure.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.departure.domain.DepartureLog;


public class DepartureLogDaoTest extends AbstractDaoIntegrationTest{
    //此类暂时没有业务使用，忽略测试用例
	@Autowired
	private DepartureLogDao departureLogDao;
	
	
	@Test
    public void testFindByFingerPrint() {
        String fingerPrint = "Joe";
        departureLogDao.findByFingerPrint(fingerPrint);
    }
	
	@Test
    public void testInsert() {
        DepartureLog parameter = new DepartureLog();
        parameter.setDistributeCode(751);
        parameter.setDistributeName("Jim");
        parameter.setOperatorCode(681);
        parameter.setOperatorName("James");
        parameter.setDepartureCarID((long)6316);
        parameter.setCapacityCode("Jim");
        parameter.setFingerPrint("Joe");
        departureLogDao.insert(parameter);
    }
}
