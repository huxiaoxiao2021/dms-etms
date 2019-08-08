package com.jd.bluedragon.distribution.receive.dao;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceiveDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReceiveDao receiveDao;
	

	
	@Test
    public void testAdd() {
        Receive parameter = new Receive();
        parameter.setCreateUser("James");
        parameter.setCreateUserCode(260);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(463);
        parameter.setWaybillCode("James");
        parameter.setPackageBarcode("James");
        parameter.setBoxCode("James");
        parameter.setReceiveType((short)14360);
        parameter.setBoxingType((short)31422);
        parameter.setUpdateTime(new Date());
        parameter.setTurnoverBoxCode("James");
        parameter.setQueueNo("James");
//        parameter.setDepartureCarId(1l);
        parameter.setShieldsCarTime(new Date());
        Assert.assertTrue(receiveDao.add(receiveDao.getClass().getName(), parameter) > 0);
    }


    @Test
    public void testFindReceiveJoinTotalCount() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 910);
        parameter.put("startTime", new Date());
        parameter.put("endTime", new Date());
        parameter.put("createUser", "James");
        parameter.put("queueNo", "James");
        receiveDao.findReceiveJoinTotalCount(parameter);
    }

	@Test
    public void testFindReceiveJoinList() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 910);
        parameter.put("startTime", new Date());
        parameter.put("endTime", new Date());
        parameter.put("createUser", "James");
        parameter.put("queueNo", "James");
        parameter.put("pageSize", 2);
        parameter.put("startIndex", 1);
        receiveDao.findReceiveJoinList(parameter);
    }
}
