package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;

public class ReverseSpareDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReverseSpareDao reverseSpareDao;
	
	
	@Test
    public void testQueryByWayBillCode() {
        ReverseSpare parameter = new ReverseSpare();
        parameter.setWaybillCode("Jax");
        parameter.setSendCode("Mary");
        reverseSpareDao.queryByWayBillCode(parameter);
    }
	
	@Test
    public void testUpdate() {
        ReverseSpare parameter = new ReverseSpare();
        parameter.setSendCode("James");
        parameter.setProductId("Jone");
        parameter.setProductCode("Stone");
        parameter.setProductName("Stone");
        parameter.setProductPrice(0.48272537373809965);
        parameter.setArrtCode1(956);
        parameter.setArrtDesc1("Jim");
        parameter.setArrtCode2(279);
        parameter.setArrtDesc2("James");
        parameter.setArrtCode3(820);
        parameter.setArrtDesc3("Jim");
        parameter.setArrtCode4(944);
        parameter.setArrtDesc4("Mary");
        parameter.setYn(873);
        parameter.setSpareTranCode("Jone");
        parameter.setSystemId((long)2078);
        reverseSpareDao.update(parameter);
    }
	
	@Test
    public void testFindByWayBillCode() {
        ReverseSpare parameter = new ReverseSpare();
        parameter.setWaybillCode("James");
        reverseSpareDao.findByWayBillCode(parameter);
    }
	
	@Test
    public void testAdd() {
        ReverseSpare parameter = new ReverseSpare();
        parameter.setSpareCode("Mary");
        parameter.setSendCode("Jone");
        parameter.setWaybillCode("Jone");
        parameter.setProductId("Jax");
        parameter.setProductCode("Jax");
        parameter.setProductName("Joe");
        parameter.setArrtCode1(146);
        parameter.setArrtDesc1("Jim");
        parameter.setArrtCode2(87);
        parameter.setArrtDesc2("Stone");
        parameter.setArrtCode3(201);
        parameter.setArrtDesc3("Jone");
        parameter.setArrtCode4(325);
        parameter.setArrtDesc4("Jone");
        parameter.setProductPrice(0.736938607382397);
        parameter.setSpareTranCode("Jax");
        reverseSpareDao.add(parameter);
    }
	
	@Test
    public void testQueryBySpareCode() {
        String spareCode = "Stone";
        reverseSpareDao.queryBySpareCode(spareCode);
    }
	
	@Test
    public void testQueryBySpareTranCode() {
        ReverseSpare parameter = new ReverseSpare();
        parameter.setSpareTranCode("Jax");
        reverseSpareDao.queryBySpareTranCode(parameter);
    }
}
