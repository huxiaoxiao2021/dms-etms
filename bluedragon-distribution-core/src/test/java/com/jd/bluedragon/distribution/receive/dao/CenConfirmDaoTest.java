package com.jd.bluedragon.distribution.receive.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;

import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

public class CenConfirmDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private CenConfirmDao cenConfirmDao;
	
	
	@Test
    public void testAdd() {
        CenConfirm parameter = new CenConfirm();
        parameter.setSendCode("James");
        parameter.setReceiveUser("James");
        parameter.setReceiveUserCode(910);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(910);
        parameter.setWaybillCode("James");
        parameter.setBoxCode("James");
        parameter.setPackageBarcode("James");
        parameter.setType((short)10);
        parameter.setUpdateTime(new Date());
        parameter.setThirdWaybillCode("James");
        parameter.setReceiveSiteCode(910);
        parameter.setInspectionUser("James");
        parameter.setInspectionUserCode(910);
        parameter.setInspectionTime(new Date());
        parameter.setOperateType(10);
        parameter.setPickupCode("James");
        parameter.setOperateTime(new Date());
        parameter.setReceiveTime(new Date());
        parameter.setOperateUser("James");
        parameter.setOperateUserCode(910);
        Assert.assertTrue(cenConfirmDao.add(cenConfirmDao.getClass().getName(), parameter) > 0);
    }
	
	@Test
    public void testUpdateYnByPackage() {
        CenConfirm parameter = new CenConfirm();
        parameter.setSendCode("James");
        parameter.setReceiveUser("James");
        parameter.setReceiveUserCode(910);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(910);
        parameter.setWaybillCode("James");
        parameter.setBoxCode("James");
        parameter.setPackageBarcode("James");
        parameter.setType((short)10);
        parameter.setUpdateTime(new Date());
        parameter.setThirdWaybillCode("James");
        parameter.setReceiveSiteCode(910);
        parameter.setInspectionUser("Jax");
        parameter.setInspectionUserCode(910);
        parameter.setInspectionTime(new Date());
        parameter.setOperateType(10);
        parameter.setPickupCode("James");
        parameter.setOperateTime(new Date());
        parameter.setReceiveTime(new Date());
        parameter.setOperateUser("James");
        parameter.setOperateUserCode(910);
        Assert.assertTrue(cenConfirmDao.updateYnByPackage(parameter) > 0);
    }
	
	@Test
    public void testQueryHandoverInfo() {
        CenConfirm parameter = new CenConfirm();
        parameter.setType((short)10);
        parameter.setCreateSiteCode(910);
        parameter.setReceiveSiteCode(910);
        parameter.setWaybillCode("James");
        parameter.setCreateTime(new Date(100,1,1,1,1,1));
        parameter.setInspectionTime(new java.util.Date());
        cenConfirmDao.queryHandoverInfo(parameter);
    }
	
	@Test
    public void testUpdateFillField() {
        CenConfirm parameter = new CenConfirm();
        parameter.setReceiveUser("James");
        parameter.setReceiveUserCode(910);
        parameter.setReceiveTime(new Date());
        parameter.setReceiveSiteCode(910);
        parameter.setInspectionUser("James");
        parameter.setInspectionUserCode(910);
        parameter.setInspectionTime(new Date());
        parameter.setOperateTime(new Date());
        parameter.setOperateUser("James");
        parameter.setOperateUserCode(910);
        parameter.setConfirmId((long)3);
        cenConfirmDao.updateFillField(parameter);
    }
}
