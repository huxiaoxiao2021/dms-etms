package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.LossOrder;

import java.util.Date;

public class LossOrderDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LossOrderDao lossOrderDao;
	
	
	@Test
    public void testAdd() {
        LossOrder parameter = new LossOrder();
        parameter.setOrderId((long)6192);
        parameter.setLossType(1);
        parameter.setLossCode(1);
        parameter.setProductId((long)1);
        parameter.setProductName("James");
        parameter.setProductQuantity(1);
        parameter.setUserErp("James");
        parameter.setUserName("James");
        parameter.setLossQuantity(1);
        parameter.setLossTime(new Date());
        Assert.assertTrue(lossOrderDao.add(LossOrderDao.class.getName(), parameter)>0);
    }
}
