package com.jd.bluedragon.distribution.test.patnerWaybill;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.partnerWaybill.domain.PartnerWaybill;
import com.jd.bluedragon.distribution.partnerWaybill.service.PartnerWaybillService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class PatnerWaybillTestCase {
	@Autowired
	private PartnerWaybillService partnerWaybillService;
	    @Test
		public void testdoCreateWayBillCode(){
			PartnerWaybill wayBillCode = new PartnerWaybill();
			
			wayBillCode.setPartnerWaybillCode("33333");
			wayBillCode.setPackageBarcode("1111");
		    wayBillCode.setWaybillCode("22222");
		    wayBillCode.setCreateSiteCode(1);
			wayBillCode.setCreateTime(new Date());
			wayBillCode.setCreateUser("理会");
			wayBillCode.setCreateUserCode(12);
			wayBillCode.setStatus(0);
			wayBillCode.setYn(1);
			wayBillCode.setPartnerSiteCode(34);
			java.util.List<PartnerWaybill> list2=new ArrayList<PartnerWaybill>();
			list2.add(wayBillCode);
			try {
				//boolean result = partnerWaybillService.doCreateWayBillCode(list2);
				System.out.println(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
}
