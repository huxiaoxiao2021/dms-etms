package com.jd.bluedragon.distribution.receive.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.receive.domain.CenConfirm;

public class CenConfirmDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private CenConfirmDao cenConfirmDao;
	
	
	@Test
    public void testAdd() {
        CenConfirm parameter = new CenConfirm();
        parameter.setSendCode("Jim");
        parameter.setReceiveUser("Jax");
        parameter.setReceiveUserCode(212);
        parameter.setCreateTime(new Date());
        parameter.setCreateSiteCode(774);
        parameter.setWaybillCode("Jim");
        parameter.setBoxCode("Stone");
        parameter.setPackageBarcode("Jim");
        parameter.setType((short)5985);
        parameter.setUpdateTime(new Date());
        parameter.setThirdWaybillCode("Mary");
        parameter.setReceiveSiteCode(817);
        parameter.setInspectionUser("Jax");
        parameter.setInspectionUserCode(407);
        parameter.setInspectionTime(new Date());
        parameter.setOperateType(727);
        parameter.setPickupCode("Joe");
        parameter.setOperateTime(new Date());
        parameter.setReceiveTime(new Date());
        parameter.setOperateUser("Stone");
        parameter.setOperateUserCode(505);
        cenConfirmDao.add(parameter);
    }
	
	@Test
    public void testUpdateYnByPackage() {
        cenConfirmDao.updateYnByPackage();
    }
	
	@Test
    public void testQueryHandoverInfo() {
        CenConfirm parameter = new CenConfirm();
        parameter.setType((short)15953);
        parameter.setCreateSiteCode(114);
        parameter.setReceiveSiteCode(178);
        parameter.setWaybillCode("Joe");
        parameter.setCreateTime(new Date());
        parameter.setInspectionTime(new Date());
        cenConfirmDao.queryHandoverInfo(parameter);
    }
	
	@Test
    public void testUpdateFillField() {
        CenConfirm parameter = new CenConfirm();
        parameter.setReceiveUser("James");
        parameter.setReceiveUserCode(127);
        parameter.setReceiveTime(new Date());
        parameter.setReceiveSiteCode(986);
        parameter.setInspectionUser("Mary");
        parameter.setInspectionUserCode(674);
        parameter.setInspectionTime(new Date());
        parameter.setOperateTime(new Date());
        parameter.setOperateUser("James");
        parameter.setOperateUserCode(109);
        parameter.setConfirmId((long)3);
        cenConfirmDao.updateFillField(parameter);
    }
}
