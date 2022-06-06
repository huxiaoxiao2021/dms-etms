package com.jd.bluedragon.distribution.saf;

import static org.mockito.Mockito.when;

import java.util.Map;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import junit.framework.Assert;
import org.apache.commons.lang3.StringUtils;
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
		printExecuteResult.setData("{\"waybillCode\":\"JDVA00000493361\",\"originalDmsCode\":910,\"originalDmsName\":\"\",\"busiId\":30937,\"busiCode\":\"10K20848\",\"busiName\":\"生鲜专送+取件下单\",\"originalCityCode\":72,\"originalCityName\":\"朝阳区\",\"originalCrossType\":2,\"transportMode\":\"特快送\",\"priceProtectFlag\":0,\"priceProtectText\":\"\",\"signBackText\":\"\",\"distributTypeText\":\"\",\"consigner\":\"测试名称\",\"consignerTel\":\"1243354\",\"consignerMobile\":\"543543\",\"consignerAddress\":\"测试名称\",\"busiOrderCode\":\"221121\",\"specialMark\":\"航\",\"specialMarkNew\":\"\",\"consigneeCompany\":\"测试名称\",\"jZDFlag\":\"\",\"senderCompany\":\"寄件公司\",\"road\":\"0\",\"sopOrExternalFlg\":true,\"printTime\":\"2021-05-29 19:27:42\",\"bjCheckFlg\":false,\"muslimSignText\":\"\",\"templateName\":\"dms-haspaper15-m\",\"templateVersion\":0,\"templateVersionStr\":\"1\",\"freightText\":\"\",\"goodsPaymentText\":\"\",\"remark\":\"备注信息测试;商家订单号：221121\",\"printAddress\":\"北京朝阳区三环到四环之间北辰世纪8号测试地址误删\",\"customerName\":\"林小玉\",\"customerContacts\":\"12222222222,010-3333333\",\"mobileFirst\":\"1222222\",\"mobileLast\":\"2222\",\"telFirst\":\"010-333\",\"telLast\":\"3333\",\"jdLogoImageKey\":\"JDLogo.gif\",\"popularizeMatrixCode\":\"https://mp.weixin.qq.com/a/~bdyFWnK5nG7Ly5w-xXbAYg~~\",\"popularizeMatrixCodeDesc\":\"扫码寄快递\",\"waybillVasSign\":\"\",\"examineFlag\":\"[已验视]\",\"securityCheck\":\"[已安检]\",\"deliveryMethod\":\"【送】\",\"waybillCodeFirst\":\"JDVA0000049\",\"waybillCodeLast\":\"3361\",\"routerNode1\":\"测试名称\",\"routerNode2\":\"测试名称\",\"routerNode3\":\"测试名称\",\"routerNode4\":\"测试名称\",\"routerNode5\":\"测试名称\",\"routerNode6\":\"测试名称\",\"routerNode7\":\"测试名称\",\"routerNode8\":\"测试名称\",\"printSiteName\":\"石景山营业部\",\"destinationCrossCode\":\"2002\",\"destinationDmsName\":\"北京马驹桥分拣中心\",\"destinationTabletrolleyCode\":\"106\",\"originalTabletrolleyCode\":\"106\",\"waybillSignText\":\"航\",\"roadCode\":\"0\",\"templatePaperSizeCode\":\"1005\",\"useNewTemplate\":true,\"templateGroupCode\":\"C\",\"consignerPrefixText\":\"\",\"consignerTelText\":\"\",\"specialMark1\":\"\",\"additionalComment\":\"http://www.jdwl.com   客服电话：950616\",\"willPrintPackageIndex\":0,\"prepareSiteCode\":39,\"prepareSiteName\":\"石景山营业部\",\"purposefulDmsCode\":910,\"purposefulDmsName\":\"北京马驹桥分拣中心\",\"originalCrossCode\":\"2002\",\"purposefulCrossCode\":\"2002\",\"originalTabletrolley\":\"106\",\"purposefulTableTrolley\":\"106\",\"timeCategory\":\"\",\"collectionAddress\":\"\",\"transportTypeText\":\"航\",\"codMoneyText\":\"\",\"totalChargeText\":\"\",\"transportModeFlag\":\"T\",\"type\":10000,\"statusCode\":200,\"statusMessage\":\"OK\",\"quantity\":1,\"popSupId\":30937,\"popSupName\":\"王月\",\"normalText\":\"无\",\"userLevel\":\"\",\"packagePrice\":\"在线支付\",\"sendPay\":\"00000000000000000000000000000000000000000000000000\",\"waybillSign\":\"30000000010000000000000000002010000000000002000000001000010000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\",\"packList\":[{\"packageCode\":\"JDVA00000493361-1-1-\",\"weight\":0,\"isPrintPack\":false,\"packageIndex\":\"1/1\",\"packageWeight\":\"\",\"packageSuffix\":\"-1-1-\",\"packageIndexNum\":1,\"printPack\":false}],\"hasPrintInvoice\":false,\"featherLetterWaybill\":false,\"needPrintFlag\":true,\"longPack\":false,\"isAir\":false,\"printInvoice\":false,\"isSelfService\":false}");
		printExecuteResult.toSuccess();
		when(jdCommandService.execute(Mockito.anyString())).thenReturn(JsonHelper.toJson(printExecuteResult));
		when(uccPropertyConfiguration.getHidePrintSpecialStartSiteNameSwitchOn()).thenThrow(new RuntimeException("mock exception"));

		JdResult<Map<String, Object>> printResult = packagePrintService.getPrintInfo(printRequest);
		Map<String, Object> printDataMap = printResult.getData();
		String originalDmsName = (String) printDataMap.get("originalDmsName");
		Assert.assertNotNull(originalDmsName);
		String consigner = (String) printDataMap.get("consigner");
		Assert.assertTrue(StringUtils.isNotBlank(consigner));
		String consignerTel = (String) printDataMap.get("consignerTel");
		Assert.assertTrue(StringUtils.isNotBlank(consignerTel));
		String consignerMobile = (String) printDataMap.get("consignerMobile");
		Assert.assertTrue(StringUtils.isNotBlank(consignerMobile));
		String consignerAddress = (String) printDataMap.get("consignerAddress");
		Assert.assertTrue(StringUtils.isNotBlank(consignerAddress));
		String routerNode1 = (String) printDataMap.get("routerNode1");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode1));
		String routerNode2 = (String) printDataMap.get("routerNode2");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode2));
		String routerNode3 = (String) printDataMap.get("routerNode3");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode3));
		String routerNode4 = (String) printDataMap.get("routerNode4");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode4));
		String routerNode5 = (String) printDataMap.get("routerNode5");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode5));
		String routerNode6 = (String) printDataMap.get("routerNode6");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode6));
		String routerNode7 = (String) printDataMap.get("routerNode7");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode7));
		String routerNode8 = (String) printDataMap.get("routerNode8");
		Assert.assertTrue(StringUtils.isNotBlank(routerNode8));
	}
}
