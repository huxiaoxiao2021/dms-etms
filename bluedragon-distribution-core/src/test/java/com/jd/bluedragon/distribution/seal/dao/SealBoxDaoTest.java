package com.jd.bluedragon.distribution.seal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;

import java.util.Date;

public class SealBoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SealBoxDao sealBoxDao;
	
	
	@Test
    public void testFindBySealCode() {
        String sealCode = "James";
        Assert.assertTrue(sealBoxDao.findBySealCode(sealCode)!=null);
    }
	
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
        parameter.setBoxCode("James");
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
        String boxCode = "James";
        Assert.assertTrue(sealBoxDao.findByBoxCode(boxCode) != null);
    }
}
