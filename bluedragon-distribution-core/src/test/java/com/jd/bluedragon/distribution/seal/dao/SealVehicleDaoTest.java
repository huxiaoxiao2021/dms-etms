package com.jd.bluedragon.distribution.seal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;

public class SealVehicleDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SealVehicleDao sealVehicleDao;
	
	
	@Test
    public void testUpdateSealVehicle() {
        SealVehicle parameter = new SealVehicle();
        parameter.setDriverCode("Jax");
        parameter.setDriver("Jim");
        parameter.setReceiveSiteCode(114);
        parameter.setUpdateUserCode(270);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateTime(new Date());
        parameter.setCode("Joe");
        parameter.setVehicleCode("Mary");
        sealVehicleDao.updateSealVehicle(parameter);
    }
	
	@Test
    public void testUpdateDisable() {
        SealVehicle parameter = new SealVehicle();
        parameter.setId((long)2689);
        sealVehicleDao.updateDisable(parameter);
    }
	
	@Test
    public void testUpdateBatch() {
        SealVehicle parameter = new SealVehicle();
        parameter.setDriverCode("Jax");
        parameter.setDriver("Stone");
        parameter.setReceiveSiteCode(590);
        parameter.setUpdateUserCode(271);
        parameter.setUpdateUser("Stone");
        parameter.setUpdateTime(new Date());
        parameter.setVehicleCode("James");
        sealVehicleDao.updateBatch(parameter);
    }
	
	@Test
    public void testUpdateSealVehicle2() {
        SealVehicle parameter = new SealVehicle();
        parameter.setDriverCode("Stone");
        parameter.setDriver("Jone");
        parameter.setReceiveSiteCode(257);
        parameter.setUpdateUserCode(309);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateTime(new Date());
        parameter.setSendCode("Jim");
        parameter.setCode("Joe");
        parameter.setVehicleCode("James");
        sealVehicleDao.updateSealVehicle2(parameter);
    }
	
	@Test
    public void testFindBySealCode() {
        String sealCode = "Jax";
        sealVehicleDao.findBySealCode(sealCode);
    }
	
	@Test
    public void testAdd2() {
        SealVehicle parameter = new SealVehicle();
        parameter.setVehicleCode("Joe");
        parameter.setCode("Jax");
        parameter.setDriverCode("Mary");
        parameter.setDriver("Jax");
        parameter.setCreateSiteCode(989);
        parameter.setReceiveSiteCode(817);
        parameter.setCreateUserCode(8);
        parameter.setCreateUser("Stone");
        parameter.setUpdateUserCode(273);
        parameter.setUpdateUser("Jax");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(263);
        parameter.setSendCode("Jax");
        parameter.setVolume(0.753347065623078);
        parameter.setWeight(0.7062918926343418);
        parameter.setPackageNum(293);
        sealVehicleDao.add2(parameter);
    }
	
	@Test
    public void testAddBatch() {
        List parameter = new ArrayList();
        //set property for item.vehicleCode
        //set property for item.code
        //set property for item.driverCode
        //set property for item.driver
        //set property for item.createSiteCode
        //set property for item.receiveSiteCode
        //set property for item.createUserCode
        //set property for item.createUser
        //set property for item.updateUserCode
        //set property for item.updateUser
        //set property for item.createTime
        //set property for item.updateTime
        //set property for item.yn
        //set property for item.sendCode
        //set property for item.volume
        //set property for item.weight
        //set property for item.packageNum
        sealVehicleDao.addBatch(parameter);
    }
	
	@Test
    public void testAdd() {
        SealVehicle parameter = new SealVehicle();
        parameter.setVehicleCode("Mary");
        parameter.setCode("Stone");
        parameter.setDriverCode("Stone");
        parameter.setDriver("Joe");
        parameter.setCreateSiteCode(297);
        parameter.setReceiveSiteCode(853);
        parameter.setCreateUserCode(690);
        parameter.setCreateUser("Jone");
        parameter.setUpdateUserCode(326);
        parameter.setUpdateUser("Jim");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(709);
        sealVehicleDao.add(parameter);
    }
}
