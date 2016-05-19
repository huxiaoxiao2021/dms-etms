package com.jd.bluedragon.distribution.fBarCode.dao;

import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;

public class FBarCodeDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private FBarCodeDao fBarCodeDao;
	
	
	@Test
    public void testAdd() {
        FBarCode parameter = new FBarCode();
        parameter.setCode("Joe");
        parameter.setCreateSiteCode(323);
        parameter.setCreateSiteName("Mary");
        parameter.setCreateUserCode(445);
        parameter.setCreateUser("Jax");
        parameter.setCreateUserCode(91);
        parameter.setCreateUser("James");
        fBarCodeDao.add(FBarCodeDao.namespace , parameter);
    }
}
