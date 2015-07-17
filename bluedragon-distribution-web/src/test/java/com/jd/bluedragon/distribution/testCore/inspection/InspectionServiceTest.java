package com.jd.bluedragon.distribution.testCore.inspection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context.xml" })
public class InspectionServiceTest {

	@Autowired
	private InspectionExceptionService inspectionExceptionService;
	
	//@Test
	public void testQueryExceptions(){
		int s = inspectionExceptionService.queryExceptions(1006, 10, "BC010F001000003900004015");
		System.out.println("=="+s);
	}
	
	//@Test
	public void testQueryThirdByParams(){
		Map paramMap = new HashMap<String,String>();
		paramMap.put("receiveSiteCode", 10);
		paramMap.put("endIndex", 100);
		paramMap.put("startIndex", 1);
		List<InspectionEC> inspectionECs = inspectionExceptionService.queryThirdByParams(paramMap);
		System.out.println("=========="+inspectionECs.size());
	}
	
	@Test
	public void testTotalThirdByParams(){
		Map paramMap = new HashMap<String,String>();
		paramMap.put("receiveSiteCode", 10);
		
		int count = inspectionExceptionService.totalThirdByParams(paramMap);
		System.out.println("==================="+count);
	}
}
