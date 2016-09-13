package com.jd.bluedragon.distribution.seal.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.jd.bluedragon.distribution.seal.domain.SealVehicle;

public class SealVehicleDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SealVehicleDao sealVehicleDao;

    @Test
    public void testAdd2() {
        SealVehicle parameter = new SealVehicle();
        parameter.setVehicleCode("James");
        parameter.setCode("James");
        parameter.setDriverCode("James");
        parameter.setDriver("James");
        parameter.setCreateSiteCode(989);
        parameter.setReceiveSiteCode(817);
        parameter.setCreateUserCode(8);
        parameter.setCreateUser("James");
        parameter.setUpdateUserCode(273);
        parameter.setUpdateUser("James");
        parameter.setCreateTime(new Date());
        parameter.setUpdateTime(new Date());
        parameter.setYn(263);
        parameter.setSendCode("James");
        parameter.setVolume(0.753347065623078);
        parameter.setWeight(0.7062918926343418);
        parameter.setPackageNum(293);
        Assert.assertTrue(sealVehicleDao.add2(SealVehicleDao.class.getName(), parameter) > 0);
    }

    @Test
    public void testAddBatch() {
        List<SealVehicle> parameter = new ArrayList();
        SealVehicle item = new SealVehicle();
        item.setVehicleCode("James");
        item.setCode("James");
        item.setDriverCode("James");
        item.setDriver("James");
        item.setCreateSiteCode(910);
        item.setReceiveSiteCode(910);
        item.setCreateUserCode(910);
        item.setCreateUser("James");
        item.setUpdateUserCode(910);
        item.setUpdateUser("James");
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setYn(1);
        item.setSendCode("James");
        item.setVolume(910d);
        item.setWeight(910d);
        item.setPackageNum(910);
        parameter.add(item);
        Assert.assertTrue(sealVehicleDao.addBatch(parameter) > 0);
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
        Assert.assertTrue(sealVehicleDao.add(SealVehicleDao.class.getName(), parameter) > 0);
    }


	@Test
    public void testUpdateSealVehicle() {
        SealVehicle parameter = new SealVehicle();
        parameter.setDriverCode("James");
        parameter.setDriver("James");
        parameter.setReceiveSiteCode(114);
        parameter.setUpdateUserCode(270);
        parameter.setUpdateUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setCode("James");
        parameter.setVehicleCode("James");
        Assert.assertTrue(sealVehicleDao.updateSealVehicle(parameter) > 0);
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
        parameter.setDriverCode("James");
        parameter.setDriver("James");
        parameter.setReceiveSiteCode(590);
        parameter.setUpdateUserCode(271);
        parameter.setUpdateUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setVehicleCode("James");
        sealVehicleDao.updateBatch(parameter);
    }
	
	@Test
    public void testUpdateSealVehicle2() {
        SealVehicle parameter = new SealVehicle();
        parameter.setDriverCode("James");
        parameter.setDriver("James");
        parameter.setReceiveSiteCode(257);
        parameter.setUpdateUserCode(309);
        parameter.setUpdateUser("James");
        parameter.setUpdateTime(new Date());
        parameter.setSendCode("James");
        parameter.setCode("James");
        parameter.setVehicleCode("James");
       Assert.assertTrue(sealVehicleDao.updateSealVehicle2(parameter) > 0);
    }
	
	@Test
    public void testFindBySealCode() {
        String sealCode = "James";
        Assert.assertNotNull(sealVehicleDao.findBySealCode(sealCode));
    }
	

}
