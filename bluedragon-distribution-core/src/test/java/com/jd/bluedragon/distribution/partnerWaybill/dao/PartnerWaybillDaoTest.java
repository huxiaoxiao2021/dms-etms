package com.jd.bluedragon.distribution.partnerWaybill.dao;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import com.jd.bluedragon.distribution.partnerWaybill.domain.PartnerWaybill;

public class PartnerWaybillDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private PartnerWaybillDao partnerWaybillDao;
	
	
	@Test
    public void testCheckHas() {
        PartnerWaybill parameter = new PartnerWaybill();
        parameter.setUpdateTime(new Date());
        parameter.setPackageBarcode("Stone");
        parameter.setPartnerSiteCode(653);
        parameter.setWaybillCode("Jim");
        parameter.setPartnerWaybillCode("Jim");
        partnerWaybillDao.checkHas(parameter);
    }
	
	@Test
    public void testAdd() {
        List parameter = new ArrayList();
        // parameter.getPartnerWaybillCode(new Object());
        // parameter.getWaybillCode(new Object());
        // parameter.getPackageBarcode(new Object());
        // parameter.getPartnerSiteCode(new Object());
        // parameter.getCreateUser(new Object());
        // parameter.getCreateUserCode(new Object());
        // parameter.getCreateTime(new Object());
        // parameter.getCreateSiteCode(new Object());
        // parameter.getUpdateUser(new Object());
        // parameter.getUpdateUserCode(new Object());
        // parameter.getUpdateTime(new Object());
        partnerWaybillDao.add(parameter);
    }
}
