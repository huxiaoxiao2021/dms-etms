package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.PickWare;

import java.util.Date;
import java.util.Map;

public class PickWareDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PickWareDao pickWareDao;
	
	
	@Test
    public void testFindByFingerprint() {
        PickWare parameter=new PickWare();

        Assert.assertTrue(pickWareDao.findByFingerprint(parameter)>0);
    }
	
	@Test
    public void testAdd() {
        PickWare parameter = new PickWare();
        parameter.setOrgId(338);
        parameter.setPackageCode("Jax");
        parameter.setPickwareCode("Jim");
        parameter.setOrderId((long)7102);
        parameter.setOperateType(351);
        parameter.setOperator("Jone");
        parameter.setPickwareTime(new Date());
        parameter.setCanReceive(792);
        parameter.setFingerprint("Mary");
        Assert.assertTrue(pickWareDao.add(PickWareDao.class.getName(), parameter) > 0);
    }
}
