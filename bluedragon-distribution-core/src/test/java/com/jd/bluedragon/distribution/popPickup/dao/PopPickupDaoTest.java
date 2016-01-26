package com.jd.bluedragon.distribution.popPickup.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;

public class PopPickupDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PopPickupDao popPickupDao;
	
	
	@Test
    public void testUpdate() {
		 PopPickup parameter = new PopPickup();
	        parameter.setWaybillCode("Joe");
	        parameter.setBoxCode("Jax1");
	        parameter.setPackageBarcode("Mary1");
	        parameter.setPickupType(1);
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
	        parameter.setWaybillType(1);
        popPickupDao.update(PopPickupDao.namespace ,parameter);
    }
	
	@Test
    public void testFindPopPickupList() {
		Map parameter = new HashMap();
        parameter.put("createSiteCode", 49);
         parameter.put("startTime", new Date());
         parameter.put("endTime", new Date());
         parameter.put("startIndex", 1);
         parameter.put("endIndex", 2);
        popPickupDao.findPopPickupList(parameter);
    }
	
	@Test
    public void testAdd() {
        PopPickup parameter = new PopPickup();
        parameter.setWaybillCode("Joe");
        parameter.setBoxCode("Jax");
        parameter.setPackageBarcode("Mary");
        parameter.setPickupType(1);
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
        parameter.setWaybillType(1);
        popPickupDao.add(PopPickupDao.namespace ,parameter);
    }
	
	@Test
    public void testFindPopPickupTotalCount() {
        Map parameter = new HashMap();
        parameter.put("createSiteCode", 49);
         parameter.put("startTime", new Date());
         parameter.put("endTime", new Date());
         parameter.put("startIndex", 1);
         parameter.put("endIndex", 2);
        popPickupDao.findPopPickupTotalCount(parameter);
    }
}
