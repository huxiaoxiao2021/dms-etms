package com.jd.bluedragon.distribution.spare.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.spare.domain.SpareSale;

public class SpareSaleDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private SpareSaleDao spareSaleDao;
	
	
	@Test
    public void testAdd() {
        SpareSale parameter = new SpareSale();
        parameter.setSpareCode("Jax");
        parameter.setProductId(411);
        parameter.setProductName("Jim");
        parameter.setSaleAmount(0.6356595230603049);
        parameter.setSaleTime(new Date());
        spareSaleDao.add(parameter);
    }
	
	@Test
    public void testUpdate() {
        SpareSale parameter = new SpareSale();
        parameter.setProductId(351);
        parameter.setProductName("Joe");
        parameter.setSaleAmount(0.4732763582798408);
        parameter.setSaleTime(new Date());
        parameter.setYn(447);
        parameter.setId((long)4016);
        spareSaleDao.update(parameter);
    }
}
