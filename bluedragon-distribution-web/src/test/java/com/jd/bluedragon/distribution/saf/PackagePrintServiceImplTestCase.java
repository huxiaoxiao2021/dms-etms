package com.jd.bluedragon.distribution.saf;

import static org.mockito.Mockito.when;

import java.util.Map;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.rest.packageMake.PackageResource;
import com.jd.bluedragon.distribution.rest.reverse.ReversePrintResource;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.print.engine.TemplateFactory;

@RunWith(MockitoJUnitRunner.class)
public class PackagePrintServiceImplTestCase {
	@InjectMocks
	PackagePrintServiceImpl packagePrintService = new PackagePrintServiceImpl();
    @Mock
    private JdCommandService jdCommandService;
	@Mock
    SortingService sortingService;
	@Mock
    private CancelWaybillJsfManager cancelWaybillJsfManager;
    
	@Mock
    private TemplateFactory templateFactory;

    @Mock
    private SysConfigService sysConfigService;
    
    @Mock
    ReversePrintResource reversePrintResource;
	@Mock
	ReassignWaybillService reassignWaybillService;
	@Mock
	private WaybillService waybillService;
	@Mock
	private PackageResource packageResource;
	@Mock
	private UccPropertyConfiguration uccPropertyConfiguration;
	/**
	 * 测试打印数据接口
	 * @throws Exception
	 */
    @Test
    public void testGetPrintInfo() throws Exception{
    	JdCommand<String> printRequest = new JdCommand<String>();
		when(sortingService.findBoxPack(Mockito.anyInt(),Mockito.anyString())).thenReturn(10);
		Box boxInfo = new Box();
		boxInfo.setCreateSiteCode(910);
		JdResult<String> printResult = new JdResult<String>();
		printResult.toFail("拦截测试");
		when(jdCommandService.execute(Mockito.anyString())).thenReturn(JsonHelper.toJson(printResult));
		JdResult<Map<String, Object>> result = packagePrintService.getPrintInfo(printRequest);
		System.err.println("打印结果"+JsonHelper.toJson(result));
//		Assert.assertTrue(result.isSucceed() && result.getData().intValue() == 10);
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

		printRequest.setData(com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(packagePrintRequest));

		JdResult<String> printExecuteResult = new JdResult<String>();
		printExecuteResult.toFail("拦截测试");
		when(jdCommandService.execute(Mockito.anyString())).thenReturn(JsonHelper.toJson(printExecuteResult));
		when(uccPropertyConfiguration.getHideSpecialStartSitPrintDestinationSiteStrList()).thenReturn(null);

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
}
