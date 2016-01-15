package com.jd.bluedragon.distribution.departure.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.departure.domain.DepartureSend;

public class DepartureSendDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private DepartureSendDao departureSendDao;
	
	
	@Test
    public void testInsert() {
        DepartureSend parameter = new DepartureSend();
        parameter.setShieldsCarId((long)8435);
        parameter.setSendCode("Jax");
        parameter.setCreateSiteCode(719);
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(442);
        parameter.setThirdWaybillCode("Jone");
        parameter.setCapacityCode("Jim");
        departureSendDao.insert(parameter);
    }
	
	@Test
    public void testGetDepartureSendByCarId() {
        Long departureCarId] = (long)3288;
        departureSendDao.getDepartureSendByCarId(departureCarId]);
    }
	
	@Test
    public void testGetByThirdWaybillCode() {
        String thirdWaybillCode] = "Jim";
        departureSendDao.getByThirdWaybillCode(thirdWaybillCode]);
    }
}
