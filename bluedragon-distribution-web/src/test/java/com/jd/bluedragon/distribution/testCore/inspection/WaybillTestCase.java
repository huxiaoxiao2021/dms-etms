package com.jd.bluedragon.distribution.testCore.inspection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class WaybillTestCase {
	@Autowired
	private WaybillPackageBarcodeService waybillPackageBarcodeService;
	
	@Test
	public void testGetPackageNumbersByWaybill(){
		Integer number = waybillPackageBarcodeService.getPackageNumbersByWaybill("514267655");
		System.out.println("number: "+number);
	}
}
