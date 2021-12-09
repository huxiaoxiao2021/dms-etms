package com.jd.bluedragon.distribution.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.print.request.SiteTerminalPrintCompleteRequest;
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
@ContextConfiguration(locations = {"classpath:bak/distribution-web-context-test.xml"})
public class PackagePrintServiceTestCase {
	@Autowired
	PackagePrintService packagePrintService;

	@Autowired
	private UccPropertyConfiguration uccPropertyConfiguration;
	
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

	@Test
	public void testHideStartSitePrint() throws Exception{
		JdCommand<String> printRequest = new JdCommand<String>();
		printRequest.setSystemCode("dms");
		printRequest.setBusinessType(1001);
		printRequest.setOperateType(100103);

		PackagePrintRequest packagePrintRequest = new PackagePrintRequest();
		packagePrintRequest.setUserCode(1000);
		packagePrintRequest.setUserName("testUser");
		packagePrintRequest.setSiteCode(910);
		packagePrintRequest.setSiteName("北京马驹桥分拣中心");
		packagePrintRequest.setBarCode("JD0003358717772");

		printRequest.setData(JsonHelper.toJson(packagePrintRequest));

		uccPropertyConfiguration.setHideSpecialStartSitPrintDestinationSiteList(null);
		JdResult<Map<String, Object>> printResult = packagePrintService.getPrintInfo(printRequest);
		Map<String, Object> printDataMap = printResult.getData();
		String originalDmsName = (String) printDataMap.get("originalDmsName");
		Assert.assertEquals(originalDmsName, "");
		String consigner = (String) printDataMap.get("consigner");
		Assert.assertEquals(consigner, "");
		String consignerTel = (String) printDataMap.get("consignerTel");
		Assert.assertEquals(consignerTel, "");
		String consignerMobile = (String) printDataMap.get("consignerMobile");
		Assert.assertEquals(consignerMobile, "");
		String consignerAddress = (String) printDataMap.get("consignerAddress");
		Assert.assertEquals(consignerAddress, "");
		String routerNode1 = (String) printDataMap.get("routerNode1");
		Assert.assertEquals(routerNode1, "");
		String routerNode2 = (String) printDataMap.get("routerNode2");
		Assert.assertEquals(routerNode2, "");
		String routerNode3 = (String) printDataMap.get("routerNode3");
		Assert.assertEquals(routerNode3, "");
		String routerNode4 = (String) printDataMap.get("routerNode4");
		Assert.assertEquals(routerNode4, "");
		String routerNode5 = (String) printDataMap.get("routerNode5");
		Assert.assertEquals(routerNode5, "");
		String routerNode6 = (String) printDataMap.get("routerNode6");
		Assert.assertEquals(routerNode6, "");
		String routerNode7 = (String) printDataMap.get("routerNode7");
		Assert.assertEquals(routerNode7, "");
		String routerNode8 = (String) printDataMap.get("routerNode8");
		Assert.assertEquals(routerNode8, "");
	}

	@Test
    public void packagePrintCompleteTest() {
        JdCommand<SiteTerminalPrintCompleteRequest> jdCommandJsfDto = new JdCommand<>();
        jdCommandJsfDto.setSystemCode("dms");
        jdCommandJsfDto.setSecretKey("123456");
        jdCommandJsfDto.setProgramType(1003);
        jdCommandJsfDto.setVersionCode("11");
        jdCommandJsfDto.setBusinessType(1003);
        jdCommandJsfDto.setOperateType(100310);
        SiteTerminalPrintCompleteRequest packagePrintDto = new SiteTerminalPrintCompleteRequest();
        jdCommandJsfDto.setData(packagePrintDto);
        packagePrintDto.setBarCode("JDVF00001404211");
        packagePrintDto.setSiteCode(910);
        packagePrintDto.setWaybillSign("");
        packagePrintDto.setOperatorName("邢松");
        packagePrintDto.setOperatorErp("bjxings");
        System.out.println(packagePrintService.packagePrintComplete(jdCommandJsfDto));
    }
}
