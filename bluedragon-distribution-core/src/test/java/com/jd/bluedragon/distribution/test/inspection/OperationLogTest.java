package com.jd.bluedragon.distribution.test.inspection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/distribution-core-context-test.xml" })
public class OperationLogTest {

	@Autowired
	private OperationLogService operationLogService;
	
	@Test
	public void testAdd(){
		OperationLog operationLog = new OperationLog();
		operationLog.setBoxCode("box");
		operationLog.setWaybillCode("waybill");
		int result = operationLogService.add(operationLog);
		System.out.println(result);
	}
	
}
