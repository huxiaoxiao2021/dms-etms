package com.jd.bluedragon.distribution.rest.inspection;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.etms.waybill.domain.DeliveryPackageD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-web-context.xml" })
public class WaybillTest {

	@Autowired
	private WaybillPackageBarcodeService waybillPackageBarcodeService;
	
	@Test
	public void testGetWaybillPackageBarcode(){
		List<DeliveryPackageD> list = waybillPackageBarcodeService.getPackageBarcodeByWaybillCode("117961926");
		System.out.println("======="+list.size());
		
	}
	
	public void testGetpakcageByWaybill(){
		WaybillResponse response = waybillPackageBarcodeService.getWaybillPackageBarcode("650000000001", 1000,10);
		System.out.println(response);
	}
}
