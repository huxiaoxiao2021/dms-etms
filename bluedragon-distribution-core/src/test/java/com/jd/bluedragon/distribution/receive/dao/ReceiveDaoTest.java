package com.jd.bluedragon.distribution.receive.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import com.jd.bluedragon.distribution.receive.domain.Receive;

public class ReceiveDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReceiveDao receiveDao;
	
	
	@Test
    public void testFindReceiveJoinTotalCount() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        receiveDao.findReceiveJoinTotalCount(parameter);
    }
	
	@Test
    public void testAdd() {
        Receive parameter = new Receive();
        parameter.setCreateUser("Mary");
        parameter.setCreateUserCode(260);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(463);
        parameter.setWaybillCode("Joe");
        parameter.setPackageBarcode("Jone");
        parameter.setBoxCode("Mary");
        parameter.setReceiveType((short)14360);
        parameter.setBoxingType((short)31422);
        parameter.setUpdateTime(new Date());
        parameter.setTurnoverBoxCode("Joe");
        parameter.setQueueNo("Jone");
        parameter.setDepartureCarId((long)7537);
        parameter.setShieldsCarTime(new Date());
        receiveDao.add(parameter);
    }
	
	@Test
    public void testFindReceiveJoinList() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("queueNo", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        receiveDao.findReceiveJoinList(parameter);
    }
}
