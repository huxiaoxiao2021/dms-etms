package com.jd.bluedragon.distribution.spare.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.spare.domain.Spare;

public class SpareDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SpareDao spareDao;
	
	
	@Test
    public void testAdd() {
        Spare parameter = new Spare();
        parameter.setCode("Joe");
        parameter.setType("Jone");
        parameter.setCreateUserCode(484);
        parameter.setCreateUser("Mary");
        parameter.setCreateUserCode(705);
        parameter.setCreateUser("Stone");
        spareDao.add(parameter);
    }
	
	@Test
    public void testUpdate() {
        Spare parameter = new Spare();
        parameter.setCreateUserCode(860);
        parameter.setCreateUser("Mary");
        parameter.setUpdateUserCode(238);
        parameter.setUpdateUser("Jax");
        parameter.setStatus(545);
        parameter.setCode("Jim");
        spareDao.update(parameter);
    }
	
	@Test
    public void testFindSpares() {
        Spare parameter = new Spare();
        parameter.setType("Mary");
        parameter.setStatus(836);
        parameter.setTimes(191);
        parameter.setQuantity(328);
        spareDao.findSpares(parameter);
    }
	
	@Test
    public void testFindBySpareCode() {
        String code = "Jim";
        spareDao.findBySpareCode(code);
    }
}
