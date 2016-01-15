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

public class SealBoxDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SealBoxDao sealBoxDao;
	
	
	@Test
    public void testFindBySealCode() {
        String sealCode = "Joe";
        sealBoxDao.findBySealCode(sealCode);
    }
	
	@Test
    public void testAdd() {
        SealBox parameter = new SealBox();
        parameter.setBoxCode("Mary");
        parameter.setCode("Jax");
        parameter.setCreateSiteCode(773);
        parameter.setReceiveSiteCode(474);
        parameter.setCreateUserCode(375);
        parameter.setCreateUser("Joe");
        parameter.setUpdateUserCode(889);
        parameter.setUpdateUser("Jax");
        sealBoxDao.add(parameter);
    }
	
	@Test
    public void testUpdate() {
        SealBox parameter = new SealBox();
        parameter.setReceiveSiteCode(598);
        parameter.setUpdateUserCode(232);
        parameter.setUpdateUser("Jim");
        parameter.setCode("James");
        sealBoxDao.update(parameter);
    }
	
	@Test
    public void testAddSealBox() {
        SealBox parameter = new SealBox();
        parameter.setBoxCode("Stone");
        parameter.setCode("Jax");
        parameter.setCreateSiteCode(15);
        parameter.setReceiveSiteCode(816);
        parameter.setCreateUserCode(6);
        parameter.setCreateUser("James");
        parameter.setUpdateUserCode(372);
        parameter.setUpdateUser("Stone");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        sealBoxDao.addSealBox(parameter);
    }
	
	@Test
    public void testUpdateSealBox() {
        SealVehicle parameter = new SealVehicle();
        parameter.setCreateSiteCode(370);
        parameter.setCreateUserCode(631);
        parameter.setCreateUser("Mary");
        parameter.setReceiveSiteCode(50);
        parameter.setUpdateUserCode(386);
        parameter.setUpdateUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setCode("Jax");
        sealBoxDao.updateSealBox(parameter);
    }
	
	@Test
    public void testFindByBoxCode() {
        String boxCode = "Joe";
        sealBoxDao.findByBoxCode(boxCode);
    }
}
