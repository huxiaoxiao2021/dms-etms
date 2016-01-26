package com.jd.bluedragon.distribution.partnerWaybill.dao;

import org.junit.Test;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
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
		PartnerWaybill parameter = new PartnerWaybill();
        parameter.setUpdateTime(new Date());
        parameter.setPackageBarcode("Stone");
        parameter.setPartnerSiteCode(653);
        parameter.setWaybillCode("Jim");
        parameter.setPartnerWaybillCode("Jim");
        partnerWaybillDao.add(PartnerWaybillDao.namespace,parameter);
    }
}
