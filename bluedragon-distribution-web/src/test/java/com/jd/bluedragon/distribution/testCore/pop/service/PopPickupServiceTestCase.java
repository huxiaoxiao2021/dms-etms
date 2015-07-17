package com.jd.bluedragon.distribution.testCore.pop.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.popPickup.domain.PopPickup;
import com.jd.bluedragon.distribution.popPickup.service.PopPickupService;
import com.jd.bluedragon.distribution.task.domain.Task;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class PopPickupServiceTestCase {

	@Autowired
	private PopPickupService popPickupService;
	
	@Test
	public void testAddTask(){
		Task task = new Task();
		task.setBody("[{\"popBusinessCode\":\"111\",\"popBusinessName\":\"popBusinessName\",\"waybillCode\":\"222222222\",\"packageBarcode\":\"\",\"packageNumber\":1,\"isCancel\":0,\"id\":1,\"businessType\":0,\"userCode\":333333,\"userName\":\"jiang\",\"siteCode\":4444444,\"siteName\":\"5555555\",\"operateTime\":\"2013-03-09 00:00:00.000\"}]");
		popPickupService.doPickup(task);
	}
	
	
	@Test
	public void testAddPopPickup(){
		try {
			PopPickup popPickup = new PopPickup();
			popPickup.setBoxCode("boxCode");
			popPickup.setCreateUser("createUser");
			popPickup.setCreateUserCode(001);
			popPickup.setOperateTime(new java.util.Date());
			popPickup.setPackageBarcode("123456789-1-1-");
			popPickup.setPackageNumber(11);
			popPickup.setPickupStatus(1);
			popPickup.setPickupType(2);
			popPickup.setPopBusinessName("popBusinessName");
			popPickup.setPopBusinessCode("popBusinessCode");
			popPickup.setCreateSiteCode(3);
			popPickup.setReceiveSiteCode(4);
			popPickup.setUpdateUser("updateUser");
			popPickup.setWaybillCode("waybillCode");
			popPickup.setYn(1);
			
			popPickupService.insertOrUpdate(popPickup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
