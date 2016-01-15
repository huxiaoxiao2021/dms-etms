package com.jd.bluedragon.distribution.abnormalorder.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;

public class AbnormalOrderDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private AbnormalOrderDao abnormalOrderDao;
	
	
	@Test
    public void testUpdateResult() {
        AbnormalOrder parameter = new AbnormalOrder();
        parameter.setMemo("Joe");
        parameter.setIsCancel(834);
        parameter.setOrderId("Stone");
        abnormalOrderDao.updateResult(parameter);
    }
	
	@Test
    public void testUpdate() {
        AbnormalOrder parameter = new AbnormalOrder();
        parameter.setFingerprint("Jim");
        parameter.setAbnormalCode1(482);
        parameter.setAbnormalReason1("Mary");
        parameter.setAbnormalCode2(430);
        parameter.setAbnormalReason2("Jim");
        parameter.setCreateUserCode(911);
        parameter.setCreateUserErp("Stone");
        parameter.setCreateUser("Joe");
        parameter.setCreateSiteCode(283);
        parameter.setCreateSiteName("Mary");
        parameter.setOperateTime(new Date());
        parameter.setMemo("Jax");
        parameter.setIsCancel(902);
        parameter.setOrderId("Jim");
        abnormalOrderDao.update(parameter);
    }
	
	@Test
    public void testGet() {
        String orderId = "Mary";
        abnormalOrderDao.get(orderId);
    }
	
	@Test
    public void testAdd() {
        AbnormalOrder parameter = new AbnormalOrder();
        parameter.setFingerprint("Jax");
        parameter.setOrderId("James");
        parameter.setAbnormalCode1(366);
        parameter.setAbnormalReason1("Jax");
        parameter.setAbnormalCode2(797);
        parameter.setAbnormalReason2("Joe");
        parameter.setCreateUserCode(151);
        parameter.setCreateUserErp("Joe");
        parameter.setCreateUser("James");
        parameter.setOperateTime(new Date());
        parameter.setCreateSiteCode(589);
        parameter.setCreateSiteName("Stone");
        parameter.setMemo("Jim");
        abnormalOrderDao.add(parameter);
    }
}
