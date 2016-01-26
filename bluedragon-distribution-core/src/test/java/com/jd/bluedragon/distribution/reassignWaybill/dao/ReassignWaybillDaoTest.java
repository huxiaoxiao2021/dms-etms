package com.jd.bluedragon.distribution.reassignWaybill.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;

public class ReassignWaybillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ReassignWaybillDao reassignWaybillDao;
	
	
	@Test
    public void testAdd() {
        ReassignWaybill parameter = new ReassignWaybill();
        parameter.setPackageBarcode("Joe");
        parameter.setAddress("Mary");
        parameter.setReceiveSiteName("Jone");
        parameter.setReceiveSiteCode(722);
        parameter.setChangeSiteName("Jim");
        parameter.setChangeSiteCode(842);
//        parameter.setOperateTime(new Date());
        parameter.setSiteCode(575);
        parameter.setSiteName("Joe");
        parameter.setUserCode(817);
        parameter.setUserName("Stone");
        parameter.setWaybillCode("Mary");
        reassignWaybillDao.add(parameter);
    }
	
	@Test
    public void testQueryByPackageCode() {
        String packageCode = "Jim";
        reassignWaybillDao.queryByPackageCode(packageCode);
    }
	
	@Test
    public void testQueryByWaybillCode() {
        String waybillCode = "Joe";
        reassignWaybillDao.queryByWaybillCode(waybillCode);
    }
}
