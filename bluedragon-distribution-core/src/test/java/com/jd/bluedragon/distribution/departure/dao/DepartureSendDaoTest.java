package com.jd.bluedragon.distribution.departure.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.departure.domain.DepartureSend;

public class DepartureSendDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureSendDao departureSendDao;
	
	
	//@Test
    public void testInsert() {
        DepartureSend parameter = new DepartureSend();
        parameter.setShieldsCarId((long)234);
        parameter.setSendCode("2222");
        parameter.setCreateSiteCode(346);
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(442);
        parameter.setThirdWaybillCode("Jone");
        parameter.setCapacityCode("Jim");
        Assert.assertEquals(1, departureSendDao.insert(parameter));
    }
	
	//@Test
    public void testGetDepartureSendByCarId() {
        Long departureCarId = (long)234;
        List<DepartureSend> list = departureSendDao.getDepartureSendByCarId(departureCarId);
        Assert.assertNotNull(list);
    }
	
	@Test
    public void testGetByThirdWaybillCode() {
        String thirdWaybillCode = "Jone";
        List<DepartureSend> list = departureSendDao.getByThirdWaybillCode(thirdWaybillCode);
        Assert.assertNotNull(list);
    }
}
