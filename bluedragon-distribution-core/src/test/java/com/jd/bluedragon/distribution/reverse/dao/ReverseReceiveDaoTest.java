package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;

public class ReverseReceiveDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReverseReceiveDao reverseReceiveDao;
	
	
	@Test
    public void testFindByPackageCode() {
        String packageCode = "Joe";
        reverseReceiveDao.findByPackageCode(packageCode);
    }
	
	@Test
    public void testAdd() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOperatorId("Stone");
        parameter.setOperatorName("Jax");
        parameter.setReceiveTime(new Date());
        parameter.setSendCode("Mary");
        parameter.setPackageCode("Joe");
        parameter.setCanReceive(604);
        parameter.setRejectCode(768);
        parameter.setRejectMessage("Jone");
        parameter.setReceiveType(531);
        reverseReceiveDao.add(parameter);
    }
	
	@Test
    public void testFindByPackageCodeAndSendCode() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setPackageCode("Joe");
        parameter.setSendCode("Mary");
        parameter.setReceiveType(945);
        reverseReceiveDao.findByPackageCodeAndSendCode(parameter);
    }
	
	@Test
    public void testFindByWaybillCodeAndSendCode() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOrderId("Joe");
        parameter.setSendCode("Jone");
        reverseReceiveDao.findByWaybillCodeAndSendCode(parameter);
    }
	
	@Test
    public void testUpdate() {
        ReverseReceive parameter = new ReverseReceive();
        parameter.setOperatorId("Mary");
        parameter.setOperatorName("Joe");
        parameter.setReceiveTime(new Date());
        parameter.setSendCode("Stone");
        parameter.setPackageCode("Jone");
        parameter.setCanReceive(238);
        parameter.setRejectCode(55);
        parameter.setRejectMessage("Jax");
        parameter.setReceiveType(212);
        parameter.setId((long)4713);
        reverseReceiveDao.update(parameter);
    }
}
