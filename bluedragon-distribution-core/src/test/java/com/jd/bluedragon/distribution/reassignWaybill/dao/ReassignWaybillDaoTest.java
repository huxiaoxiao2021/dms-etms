package com.jd.bluedragon.distribution.reassignWaybill.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;

import java.util.Date;

public class ReassignWaybillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReassignWaybillDao reassignWaybillDao;
	
	
	@Test
    public void testAdd() {
        ReassignWaybill parameter = new ReassignWaybill();
        parameter.setPackageBarcode("James");
        parameter.setAddress("James");
        parameter.setReceiveSiteName("James");
        parameter.setReceiveSiteCode(910);
        parameter.setChangeSiteName("James");
        parameter.setChangeSiteCode(910);
        parameter.setOperateTime(new Date());
        parameter.setSiteCode(910);
        parameter.setSiteName("James");
        parameter.setUserCode(910);
        parameter.setUserName("James");
        parameter.setWaybillCode("James");
       Assert.assertTrue(reassignWaybillDao.add(parameter));
    }
	
	@Test
    public void testQueryByPackageCode() {
        String packageCode = "James";
        Assert.assertNotNull(reassignWaybillDao.queryByPackageCode(packageCode));
    }
	
	@Test
    public void testQueryByWaybillCode() {
        String waybillCode = "James";
        Assert.assertNotNull(reassignWaybillDao.queryByWaybillCode(waybillCode));
    }
}
