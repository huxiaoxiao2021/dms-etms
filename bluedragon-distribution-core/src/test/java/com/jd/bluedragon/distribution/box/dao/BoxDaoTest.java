package com.jd.bluedragon.distribution.box.dao;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private BoxDao boxDao;
	
	
	@Test
    public void testAdd() {
        Box parameter = new Box();
        parameter.setCode("123");
        parameter.setType("1");
        parameter.setCreateSiteCode(10);
        parameter.setCreateSiteName("Mary");
        parameter.setReceiveSiteCode(288);
        parameter.setReceiveSiteName("Jax");
        parameter.setCreateUserCode(510);
        parameter.setCreateUser("Jim");
        parameter.setCreateUserCode(781);
        parameter.setCreateUser("Jim");
        parameter.setTransportType(3);
        parameter.setMixBoxType(60);
        Assert.assertEquals(new Integer(1), boxDao.add(BoxDao.namespace, parameter));
    }
	
	@Test
    public void testFindBoxByCode() {
        String code = "123";
        Box box = boxDao.findBoxByCode(code);
        Assert.assertNotNull(box);
    }
	
	@Test
    public void testFindBoxesBySite() {
        Box parameter = new Box();
        parameter.setType("1");
        parameter.setCreateSiteCode(691);
        parameter.setReceiveSiteCode(288);
        parameter.setQuantity(1);
        List<Box> list = boxDao.findBoxesBySite(parameter);
        Assert.assertNotNull(list);
    }
	
	@Test
    public void testReprint() {
        Box parameter = new Box();
        parameter.setUpdateUserCode(10);
        parameter.setUpdateUser("James22");
        parameter.setCode("James");
        Assert.assertEquals(new Integer(0), boxDao.reprint(parameter));
    }
	
	@Test
    public void testUpdateStatusByCodes() {
        Box parameter = new Box();
        parameter.setStatus(3);
        parameter.setUpdateUser("123aa");
        parameter.setUpdateUserCode(352);
        parameter.setCodes("'"+123+"'");
        parameter.setCreateSiteCode(10);
    }
	
	@Test
    public void testFindBoxByBoxCode() {
        Box parameter = new Box();
        parameter.setCode("123");
        parameter.setCreateSiteCode(10);
        Box box = boxDao.findBoxByBoxCode(parameter);
        Assert.assertNotNull(box);
    }

    @Test
    public void testUpdateVolumeByCode() {
        Box paramater = new Box();
        paramater.setCode("123");
        paramater.setHeight(3f);
        paramater.setWidth(4f);
        paramater.setLength(5f);
        Assert.assertEquals(new Integer(1), boxDao.updateVolumeByCode(paramater));
    }


    @Test
    public void testBatchUpdateStatus() {
        Box paramater = new Box();
        paramater.setCode("('123')");
        paramater.setStatus(10);
    }

}
