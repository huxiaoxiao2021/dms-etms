package com.jd.bluedragon.distribution.abnormalorder.dao;

import java.util.Date;
import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;

import junit.framework.Assert;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.abnormalorder.domain.AbnormalOrder;

public class AbnormalOrderDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private AbnormalOrderDao abnormalOrderDao;


    @Test
    public void testAdd() {
        AbnormalOrder parameter = new AbnormalOrder();
        parameter.setFingerprint("Jax");
        parameter.setOrderId("11111");
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
        int i = abnormalOrderDao.insert(parameter);
        Assert.assertEquals(1, i);
    }


    @Test
    public void testGet() {
        String orderId = "11111";
        AbnormalOrder abnormalOrder = abnormalOrderDao.query(orderId);
        Assert.assertNotNull(abnormalOrder);
    }
	
	@Test
    public void testUpdateResult() {
        AbnormalOrder parameter = new AbnormalOrder();
        parameter.setMemo("Joe");
        parameter.setIsCancel(2);
        parameter.setOrderId("11111");
        int i = abnormalOrderDao.updateResult(parameter);
        Assert.assertEquals(1, i);
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
        parameter.setIsCancel(1);
        parameter.setOrderId("11111");
        int i = abnormalOrderDao.updateSome(parameter);
        Assert.assertEquals(1, i);
    }
	

}
