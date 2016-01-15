package com.jd.bluedragon.distribution.popPickup.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;

public class PopPickupDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopPickupDao popPickupDao;
	
	
	@Test
    public void testUpdate() {
        Inspection parameter = new Inspection();
        parameter.setBoxCode("Mary");
        // parameter.getPickupType(new Object());
        // parameter.getPackageNumber(new Object());
        // parameter.getPopBusinessName(new Object());
        // parameter.getCarCode(new Object());
        parameter.setReceiveSiteCode(212);
        parameter.setUpdateUser("Jone");
        parameter.setUpdateUserCode(319);
        // parameter.getOperateTime(new Object());
        parameter.setWaybillCode("Joe");
        // parameter.getPopBusinessCode(new Object());
        parameter.setCreateSiteCode(312);
        popPickupDao.update(parameter);
    }
	
	@Test
    public void testFindPopPickupList() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("waybillCode", new Object());
        // parameter.put("packageBarcode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popBusinessCode", new Object());
        // parameter.put("popBusinessName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        // parameter.put("endIndex", new Object());
        // parameter.put("startIndex", new Object());
        popPickupDao.findPopPickupList(parameter);
    }
	
	@Test
    public void testAdd() {
        PopPickup parameter = new PopPickup();
        parameter.setPickupId((long)6428);
        parameter.setWaybillCode("Joe");
        parameter.setBoxCode("Jax");
        parameter.setPackageBarcode("Mary");
        parameter.setPickupType(256);
        parameter.setPackageNumber(434);
        parameter.setPopBusinessCode("Stone");
        parameter.setPopBusinessName("Jax");
        parameter.setCarCode("Jone");
        parameter.setCreateUser("Joe");
        parameter.setCreateUserCode(478);
        parameter.setCreateSiteCode(49);
        parameter.setReceiveSiteCode(405);
        parameter.setUpdateUser("Mary");
        parameter.setUpdateUserCode(493);
        parameter.setOperateTime(new Date());
        parameter.setWaybillType(92);
        popPickupDao.add(parameter);
    }
	
	@Test
    public void testFindPopPickupTotalCount() {
        Map parameter = new HashMap();
        // parameter.put("createSiteCode", new Object());
        // parameter.put("waybillCode", new Object());
        // parameter.put("packageBarcode", new Object());
        // parameter.put("waybillType", new Object());
        // parameter.put("startTime", new Object());
        // parameter.put("endTime", new Object());
        // parameter.put("popBusinessCode", new Object());
        // parameter.put("popBusinessName", new Object());
        // parameter.put("createUserCode", new Object());
        // parameter.put("createUser", new Object());
        popPickupDao.findPopPickupTotalCount(parameter);
    }
}
