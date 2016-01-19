package com.jd.bluedragon.distribution.box.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;

public class BoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BoxDao boxDao;
	
	
	@Test
    public void testAdd() {
        Box parameter = new Box();
        parameter.setCode("123");
        parameter.setType("1");
        parameter.setCreateSiteCode(691);
        parameter.setCreateSiteName("Mary");
        parameter.setReceiveSiteCode(288);
        parameter.setReceiveSiteName("Jax");
        parameter.setCreateUserCode(510);
        parameter.setCreateUser("Jim");
        parameter.setCreateUserCode(781);
        parameter.setCreateUser("Jim");
        parameter.setTransportType(3);
        parameter.setMixBoxType(60);
        boxDao.add(BoxDao.namespace, parameter);
    }
	
	@Test
    public void testFindBoxByCode() {
        String code = "123";
        boxDao.findBoxByCode(code);
    }
	
	@Test
    public void testFindBoxesBySite() {
        Box parameter = new Box();
        parameter.setType("1");
        parameter.setCreateSiteCode(691);
        parameter.setReceiveSiteCode(288);
        parameter.setQuantity(1);
        boxDao.findBoxesBySite(parameter);
    }
	
	@Test
    public void testReprint() {
        Box parameter = new Box();
        parameter.setUpdateUserCode(10);
        parameter.setUpdateUser("James1");
        parameter.setCode("123");
        boxDao.reprint(parameter);
    }
	
	@Test
    public void testPrint() {
        Box parameter = new Box();
        parameter.setUpdateUserCode(159);
        parameter.setUpdateUser("Mary1");
        parameter.setCode("123");
        boxDao.print(parameter);
    }
	
	@Test
    public void testBatchUpdateStatus() {
        Box parameter = new Box();
        parameter.setStatus(4);
        parameter.setCode("123");
        parameter.setCreateSiteCode(691);
        boxDao.batchUpdateStatus(parameter);
    }
	
	@Test
    public void testUpdateStatusByCodes() {
        Box parameter = new Box();
        parameter.setStatus(3);
        parameter.setUpdateUser("123");
        parameter.setUpdateUserCode(352);
        parameter.setCodes("123");
        boxDao.updateStatusByCodes(parameter);
    }
	
	@Test
    public void testFindBoxByBoxCode() {
        Box parameter = new Box();
        parameter.setCode("123");
        parameter.setCreateSiteCode(691);
        boxDao.findBoxByBoxCode(parameter);
    }
}
