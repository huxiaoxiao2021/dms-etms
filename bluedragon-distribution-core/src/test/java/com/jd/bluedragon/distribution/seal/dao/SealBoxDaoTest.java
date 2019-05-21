package com.jd.bluedragon.distribution.seal.dao;

import com.jd.bluedragon.distribution.seal.domain.SealBox;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-core-dao.xml")
public class SealBoxDaoTest{
	
	@Autowired
	private SealBoxDao sealBoxDao;
	
	
	@Test
    public void testAdd() {
        SealBox parameter = new SealBox();
        parameter.setBoxCode("James");
        parameter.setCode("James");
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setCreateUserCode(910);
        parameter.setCreateUser("James");
        parameter.setUpdateUserCode(910);
        parameter.setUpdateUser("James");
        Assert.assertTrue(sealBoxDao.add(SealBoxDao.class.getName(), parameter)>0);
    }
	
	@Test
    public void testUpdate() {
        SealBox parameter = new SealBox();
        parameter.setReceiveSiteCode(910);
        parameter.setUpdateUserCode(910);
        parameter.setUpdateUser("James");
        parameter.setCode("James");
        Assert.assertTrue(sealBoxDao.update(SealBoxDao.class.getName(), parameter) > 0);
    }
	
	@Test
    public void testAddSealBox() {
        SealBox parameter = new SealBox();
        parameter.setBoxCode("James11111111111");
        parameter.setCode("James");
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setCreateUserCode(910);
        parameter.setCreateUser("James");
        parameter.setUpdateUserCode(910);
        parameter.setUpdateUser("James");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        Assert.assertTrue(sealBoxDao.addSealBox(parameter) > 0);
    }
	
	@Test
    public void testUpdateSealBox() {
        SealBox parameter = new SealBox();
        parameter.setCreateSiteCode(910);
        parameter.setCreateUserCode(910);
        parameter.setCreateUser("James");
        parameter.setReceiveSiteCode(910);
        parameter.setUpdateUserCode(910);
        parameter.setUpdateUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setCode("James");
        Assert.assertTrue(sealBoxDao.updateSealBox(parameter) > 0);
    }
	
	@Test
    public void testFindByBoxCode() {
        String boxCode = "BC010F002010Y10800002005";
        Assert.assertTrue(sealBoxDao.findByBoxCode(boxCode) != null);
    }

    @Test
    public void testFindBySealCode() {
        String sealCode = "James";
        sealBoxDao.findBySealCode(sealCode);
    }

}
