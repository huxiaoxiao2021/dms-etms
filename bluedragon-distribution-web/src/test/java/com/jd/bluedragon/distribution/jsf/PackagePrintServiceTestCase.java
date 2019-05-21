package com.jd.bluedragon.distribution.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintService;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试
@ContextConfiguration(locations = {"classpath:/distribution-web-context-test.xml"})
public class PackagePrintServiceTestCase {
	@Autowired
	PackagePrintService packagePrintService;
	
    @Test
    public void testUseNewTemplate() throws Exception{
    	JdCommand<String> printRequest = new JdCommand<String>();
		printRequest.setBusinessType(1003);
		printRequest.setOperateType(100302);
		printRequest.setSystemCode("dms");
		printRequest.setSecretKey("123456");
		PackagePrintRequest packagePrintRequest = new PackagePrintRequest();
		packagePrintRequest.setUserCode(1000);
		packagePrintRequest.setUserName("testUser");
		packagePrintRequest.setSiteCode(910);
		packagePrintRequest.setSiteName("北京马驹桥分拣中心");
		{/**1、打印运单所有包裹**/
			packagePrintRequest.setBarCode("JDVA00001487940");
		}
		printRequest.setData(JsonHelper.toJson(packagePrintRequest));
		List<JdResult<Map<String,Object>>> results = new ArrayList<JdResult<Map<String,Object>>>();
		List<Boolean> checkList = new ArrayList<Boolean>();
		//系统不存在
		printRequest.setSystemCode("error-systemCode");
		results.add(packagePrintService.getPrintInfo(printRequest));
		checkList.add(false);
		//密钥错误
		printRequest.setSystemCode("dms");
		printRequest.setSecretKey("error-secretKey");
		results.add(packagePrintService.getPrintInfo(printRequest));
		checkList.add(false);
		//操作业务类型错误
		printRequest.setSystemCode("dms");
		printRequest.setSecretKey("123456");
		printRequest.setBusinessType(100300);
		results.add(packagePrintService.getPrintInfo(printRequest));
		checkList.add(false);
		//操作码错误
		printRequest.setSystemCode("dms");
		printRequest.setSecretKey("123456");
		printRequest.setBusinessType(1003);
		printRequest.setOperateType(100301);
		results.add(packagePrintService.getPrintInfo(printRequest));
		checkList.add(false);
		//单号C网，不允许打印
		printRequest.setSystemCode("dms");
		printRequest.setSecretKey("123456");
		printRequest.setBusinessType(1003);
		printRequest.setOperateType(100302);
		packagePrintRequest.setBarCode("JDVA00001487940");
		results.add(packagePrintService.getPrintInfo(printRequest));
		checkList.add(false);
		int count = 0;
		for(JdResult<Map<String,Object>> result:results){
			Assert.assertEquals((Boolean)checkList.get(count++), (Boolean)result.isSucceed());
			System.err.println(result.getMessage());
		}
    }
}
