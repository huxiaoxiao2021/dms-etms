package com.jd.bluedragon.distribution.reverse.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.reverse.domain.LossOrder;

public class LossOrderDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private LossOrderDao lossOrderDao;
	
	
	@Test
    public void testAdd() {
        LossOrder parameter = new LossOrder();
        parameter.setOrderId((long)6192);
        parameter.setLossType(281);
        parameter.setLossCode(176);
        parameter.setProductId((long)6588);
        parameter.setProductName("Stone");
        parameter.setProductQuantity(250);
        parameter.setUserErp("Stone");
        parameter.setUserName("Jax");
        parameter.setLossQuantity(209);
        parameter.setLossTime(new Date());
        lossOrderDao.add(parameter);
    }
}
