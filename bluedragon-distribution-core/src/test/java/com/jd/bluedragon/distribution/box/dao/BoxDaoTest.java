package com.jd.bluedragon.distribution.box.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.box.domain.Box;

public class BoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BoxDao boxDao;
	
	
	@Test
    public void testAdd() {
        Box parameter = new Box();
        parameter.setId((long)7626);
        parameter.setCode("James");
        parameter.setType("James");
        parameter.setCreateSiteCode(691);
        parameter.setCreateSiteName("Mary");
        parameter.setReceiveSiteCode(288);
        parameter.setReceiveSiteName("Jax");
        parameter.setCreateUserCode(510);
        parameter.setCreateUser("Jim");
        parameter.setCreateUserCode(781);
        parameter.setCreateUser("Jim");
        parameter.setTransportType(667);
        parameter.setMixBoxType(60);
        boxDao.add(parameter);
    }
	
	@Test
    public void testFindBoxByCode() {
        String code = "James";
        boxDao.findBoxByCode(code);
    }
	
	@Test
    public void testFindBoxesBySite() {
        Box parameter = new Box();
        parameter.setType("Jone");
        parameter.setCreateSiteCode(297);
        parameter.setReceiveSiteCode(958);
        parameter.setQuantity(641);
        boxDao.findBoxesBySite(parameter);
    }
	
	@Test
    public void testReprint() {
        Box parameter = new Box();
        parameter.setUpdateUserCode(10);
        parameter.setUpdateUser("James");
        parameter.setCode("Jax");
        boxDao.reprint(parameter);
    }
	
	@Test
    public void testPrint() {
        Box parameter = new Box();
        parameter.setUpdateUserCode(159);
        parameter.setUpdateUser("Mary");
        parameter.setCode("Joe");
        boxDao.print(parameter);
    }
	
	@Test
    public void testBatchUpdateStatus() {
        Box parameter = new Box();
        parameter.setStatus(957);
        parameter.setCode("Jim");
        parameter.setCreateSiteCode(682);
        boxDao.batchUpdateStatus(parameter);
    }
	
	@Test
    public void testUpdateStatusByCodes() {
        Box parameter = new Box();
        parameter.setStatus(594);
        parameter.setUpdateUser("Mary");
        parameter.setUpdateUserCode(352);
        parameter.setCodes("Joe");
        parameter.setStatus(463);
        boxDao.updateStatusByCodes(parameter);
    }
	
	@Test
    public void testFindBoxByBoxCode() {
        Box parameter = new Box();
        parameter.setCode("Jim");
        parameter.setCreateSiteCode(879);
        boxDao.findBoxByBoxCode(parameter);
    }
}
