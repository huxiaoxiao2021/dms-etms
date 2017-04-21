package com.jd.bluedragon.distribution.test.urban;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.urban.dao.UrbanWaybillDao;
import com.jd.bluedragon.distribution.urban.domain.UrbanWaybill;

public class UrbanWaybillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private UrbanWaybillDao urbanWaybillDao;
	
	private static String wallbillCode = "VC1000000001";
	private static String scheduleBillCode = "SC100000001";
	
	@Test
    public void testAdd() {
		UrbanWaybill data = new UrbanWaybill();
		data.setWaybillCode(wallbillCode);
		data.setScheduleBillCode(scheduleBillCode);
		Integer res = urbanWaybillDao.insert(data);
		Assert.assertEquals(res, new Integer(1));
		UrbanWaybill obj = urbanWaybillDao.findByWaybillCode(wallbillCode);
		Assert.assertNotNull(obj);
		obj.setStatus(39);
		res = urbanWaybillDao.updateBySelective(obj);
		Assert.assertEquals(res, new Integer(1));
		List<UrbanWaybill> list = urbanWaybillDao.findByScheduleBillCode(scheduleBillCode);
		Assert.assertNotNull(list);
		res = urbanWaybillDao.deleteById(obj.getId());
		Assert.assertEquals(res, new Integer(1));
    }
	
}
