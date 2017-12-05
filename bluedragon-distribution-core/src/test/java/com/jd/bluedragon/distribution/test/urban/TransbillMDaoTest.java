package com.jd.bluedragon.distribution.test.urban;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import com.jd.bluedragon.distribution.urban.dao.TransbillMDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;

public class TransbillMDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private TransbillMDao transbillMDao;
	
	private static String wallbillCode = "VC1000000001";
	private static String scheduleBillCode = "SC100000001";
	
	@Test
    public void testAdd() {
		TransbillM data = new TransbillM();
		data.setWaybillCode(wallbillCode);
		data.setTransbillCode(wallbillCode);
		data.setScheduleBillCode(scheduleBillCode);
		data.setCreateTime(new Date());
		data.setCreateUser("22222");
		data.setUpdateTime(new Date());
		data.setUpdateUser("22222");
		data.setRequireTransMode(1);
		data.setPartitionTime(new Date());
		data.setTsM(new Date().getTime());
		Integer res = transbillMDao.insert(data);
		Assert.assertEquals(res, new Integer(1));
		TransbillM obj = transbillMDao.findByWaybillCode(wallbillCode);
		Assert.assertNotNull(obj);
		//obj.setTransbillState("300");
		res = transbillMDao.updateBySelective(obj);
		Assert.assertEquals(res, new Integer(1));
        List<String> list = transbillMDao.findEffectWaybillCodesByScheduleBillCode(scheduleBillCode);
		Assert.assertNotNull(list);
    }
	
}
