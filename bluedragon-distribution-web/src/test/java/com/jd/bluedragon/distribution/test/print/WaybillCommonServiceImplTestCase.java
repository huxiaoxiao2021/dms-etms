package com.jd.bluedragon.distribution.test.print;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.presort.AoiBindRoadMappingData;
import com.jd.bluedragon.core.jsf.presort.AoiBindRoadMappingQuery;
import com.jd.bluedragon.core.jsf.presort.AoiServiceManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.service.HideInfoComposeServiceImpl;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillFenceDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.bluedragon.dms.utils.SendPayConstants;
import com.sun.el.stream.Stream;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * 
 * @ClassName: CustomerAndConsignerInfoHandlerTestCase
 * @Description: 打印业务-收、寄件人信息处理测试类
 * @author: wuyoude
 * @date: 2020年2月14日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WaybillCommonServiceImplTestCase {
	@InjectMocks
	WaybillCommonServiceImpl waybillCommonServiceImpl;
	@InjectMocks
	CustomerAndConsignerInfoHandler customerAndConsignerInfoHandler;
	@InjectMocks
	HideInfoComposeServiceImpl hideInfoComposeServiceImpl;
    @Mock
    private ProductService productService;
	@Mock
	SysConfigService sysConfigService;
    /**
     * 运单包裹查询
     */
    @Mock
    WaybillPackageApi waybillPackageApi;

    @Mock
    private BaseMajorManager baseMajorManager;

    @Mock
    private OrderWebService orderWebService;
    @Mock
    private BaseService baseService;
    @Mock
    private SiteService siteService;
    @Mock
    private WaybillQueryManager waybillQueryManager;
    @Mock
    private PopPrintService popPrintService;
    @Mock
    private WaybillPrintService waybillPrintService;
    @Mock
    private WaybillPickupTaskApi waybillPickupTaskApi;
    @Mock
    private WaybillService waybillService;
    @Mock
    HideInfoService hideInfoService;

    @Mock
    private BasicSafInterfaceManager basicSafInterfaceManager;

    @Mock
    private PreseparateWaybillManager preseparateWaybillManager;

    @Mock
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
    @Mock
    private CacheService jimdbCacheService;
    
    @Mock
    private AoiServiceManager aoiServiceManager;
	
	public static void main(String[] args) throws Exception{

	}
	/**
	 * 海运标识测试
	 * @throws Exception
	 */
    @Test
    public void testPrintH() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,293,'0'),
				UtilsForTestCase.getSignString(500,293,'1'),
				UtilsForTestCase.getSignString(500,293,'2'),
				UtilsForTestCase.getSignString(500,293,'3'),
				UtilsForTestCase.getSignString(500,293,'4'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				false,
				true,
				false,
				false,
				false,};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBasePrintWaybill().setSpecialMark(null);
				
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//打标‘H’验证
				boolean hasFlag = (context.getBasePrintWaybill().getSpecialMark() != null 
						&& context.getBasePrintWaybill().getSpecialMark().contains("H"));
				Assert.assertEquals(sendPayChecks[i],hasFlag);
			}
		}
	/**
	 * B网打印运单标识BcSign
	 * @throws Exception
	 */
    @Test
    public void testPrintBcSign() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] waybillSigns = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,40,'2'),
				UtilsForTestCase.getSignString(500,97,'1'),
				UtilsForTestCase.getSignString(500,40,'3'),
				UtilsForTestCase.getSignString(500,97,'2'),
				UtilsForTestCase.getSignString(500,97,'3'),};
		boolean[] waybillSignChecks ={
				false,
				false,
				false,
				true,
				true,
				false,
				false,
				false,};
		for(int i=0 ; i < waybillSigns.length; i++ ){
				System.err.println(waybillSigns[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[i]);
				context.getBasePrintWaybill().setSpecialMark(null);
				context.getBasePrintWaybill().setBcSign(null);
				context.getBasePrintWaybill().setWaybillCodeLast("4321");
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//打标‘H’验证
				boolean hasFlag = (context.getBasePrintWaybill().getBcSign() != null 
						&& context.getBasePrintWaybill().getBcSign().equals("B-4321"));
				Assert.assertEquals(waybillSignChecks[i],hasFlag);
			}
		}
	/**
	 * B网打印运单标识BcSign
	 * @throws Exception
	 */
    @Test
    public void testTransportTypeText() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] waybillSigns = {
				UtilsForTestCase.getSignString(500,84,'2'),
				};
		String[] transportTypeTexts ={
				"铁",
				};
		for(int i=0 ; i < waybillSigns.length; i++ ){
				System.err.println(waybillSigns[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[i]);
				context.getBasePrintWaybill().setSpecialMark(null);
				context.getBasePrintWaybill().setSpecialMarkNew(null);
				context.getBasePrintWaybill().setTransportTypeText(null);
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//transportTypeText验证
				Assert.assertEquals(transportTypeTexts[i],context.getBasePrintWaybill().getTransportTypeText());
				//SpecialMark验证
				Assert.assertEquals(true,context.getBasePrintWaybill().getSpecialMark().contains(transportTypeTexts[i]));
			}
    }
    @Test
    public void testisStorageWaybill(){
        waybillCommonServiceImpl.isStorageWaybill("1111");
    }
	/**
	 * B网打印运单标识BcSign
	 * @throws Exception
	 */
    @Test
    public void testSetFreightAndGoodsPayment() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] codMoneys = {
				null,
				"1.00",
				"15.01",
				"15.01",
				"1.00",
				};
		Double[] topayTotalReceivables ={
				2.01111111,
				2.01111111,
				2.0111111,
				null,
				1.0
				};
		String[] checkResults1 = {
				"",
				"代收货款：1.00￥",
				"代收货款：15.01￥",
				"代收货款：15.01￥",
				"代收货款：1.00￥"
			};
		String[] checkResults2 = {
				"运费合计：2.01￥",
				"运费合计：1.01￥",
				"",
				"",				
				""
		};
		for(int i=0 ; i < codMoneys.length; i++ ){
				System.err.println(codMoneys[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setCodMoney(codMoneys[i]);
				context.getBigWaybillDto().getWaybill().setTopayTotalReceivable(topayTotalReceivables[i]);
				context.getBasePrintWaybill().setCodMoneyText(null);
				context.getBasePrintWaybill().setTotalChargeText(null);
				
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//codMoneyText验证
				Assert.assertEquals(checkResults1[i],context.getBasePrintWaybill().getCodMoneyText());
				//totalChargeText验证
				Assert.assertEquals(checkResults2[i],context.getBasePrintWaybill().getTotalChargeText());
			}
    }
	/**
	 * 打印显示预
	 * @throws Exception
	 */
    @Test
    public void testPreSell() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,297,'1'),
				UtilsForTestCase.getSignString(500,297,'2'),
				UtilsForTestCase.getSignString(500,297,'3'),
				UtilsForTestCase.getSignString(500,297,'4'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				true,
				true,
				false,
				false,};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBasePrintWaybill().setBcSign(null);
				
				//预期验证结果
				boolean checkResult = sendPayChecks[i];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isPreSell(sendPays[i]);
				Assert.assertEquals(utilsCheck,checkResult);
				
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//打标‘预’验证
				boolean hasFlag = (context.getBasePrintWaybill().getBcSign() != null 
						&& context.getBasePrintWaybill().getBcSign().equals("预"));
				Assert.assertEquals(hasFlag,checkResult);
		}
    }
	/**
	 * 打印显示预
	 * @throws Exception
	 */
    @Test
    public void testLoadOriginalDmsInfo() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
    	BaseStaffSiteOrgDto pickSite = new BaseStaffSiteOrgDto();
    	pickSite.setCollectionDmsId(50);
    	when(baseMajorManager.getBaseSiteBySiteId(5)).thenReturn(pickSite);
    	
    	BaseStaffSiteOrgDto userSite = new BaseStaffSiteOrgDto();
    	userSite.setCollectionDmsId(390);
    	userSite.setSiteType(4);
    	when(baseMajorManager.getBaseSiteBySiteId(39)).thenReturn(userSite);
    	
    	
    	context.getBigWaybillDto().getWaybill().setWaybillSign(UtilsForTestCase.getSignString(500,29,'8'));
    	context.getBigWaybillDto().getWaybillPickup().setPickupSiteId(5);
    	context.getBasePrintWaybill().setPrepareSiteCode(10);
    	context.getRequest().setSiteCode(39);
    	waybillCommonServiceImpl.loadOriginalDmsInfo(context, context.getBasePrintWaybill(), context.getBigWaybillDto());
    	Assert.assertEquals(new Integer(50),context.getBasePrintWaybill().getOriginalDmsCode());
    }


	/**
	 * 测试打印二维码
	 */
	@Test
	public void testBasePrintInfoByWaybill() throws Exception{
    	//todo 沟通入参怎么获取  帮忙看下逻辑
		//生产两个对象
		BasePrintWaybill basePrintWaybill = new BasePrintWaybill();
		Waybill waybill =  new Waybill();

		waybill.setWaybillSign(UtilsForTestCase.getSignString(500,61,'1'));
		waybill.setSendPay("1");
		waybill.setCustomerCode("010K1750468- for SF outbound");
		waybill.setWaybillCode("22");

		when(siteService.getSiteNameByCode(50)).thenReturn("20");

		BaseStaffSiteOrgDto siteInfo = new BaseStaffSiteOrgDto();
		siteInfo.setCityId(66);
		siteInfo.setCityName("哈尔滨");
		when(baseService.queryDmsBaseSiteByCode("22")).thenReturn(siteInfo);
		 List<String> objects = Lists.newArrayList();
		objects.add("010K1750468- for SF outbound");
		when(sysConfigService.getStringListConfig(Constants.SYS_WAYBILL_PRINT_ADDIOWN_NUMBER_CONF)).thenReturn(objects);


		BaseEntity<Waybill> oldWaybilla=new BaseEntity<Waybill>();
		Waybill waybill1 = new Waybill();
		waybill1.setWaybillCode("666");

		oldWaybilla.setData(waybill1);
		when(waybillQueryManager.getWaybillByReturnWaybillCode(waybill.getWaybillCode())).thenReturn(oldWaybilla);

		BasePrintWaybill basePrintWaybill1 = waybillCommonServiceImpl.setBasePrintInfoByWaybill(basePrintWaybill, waybill);

		System.out.println("basePrintWaybill1 = " + JSON.toJSON(basePrintWaybill1));
	}



	/**
	 * 根据sendpay 146=4 面单产品打印【冷链卡班】占位符 jzdflag
	 * @throws Exception
	 */
	@Test
	public void testJZDFlag() throws Exception{
		WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,146,'1'),
				UtilsForTestCase.getSignString(500,146,'2'),
				UtilsForTestCase.getSignString(500,146,'3'),
				UtilsForTestCase.getSignString(500,146,'4'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				false,
				false,
				false,
				true,};
		for(int i=0 ; i < sendPays.length; i++ ){
			System.err.println(sendPays[i]);
			context.setBasePrintWaybill(context.getResponse());
			context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
			context.getBasePrintWaybill().setjZDFlag(null);

			//预期验证结果
			boolean checkResult = sendPayChecks[i];

			//工具类业务方法校验
			boolean utilsCheck = BusinessUtil.isSignChar(sendPays[i], SendPayConstants.POSITION_146,SendPayConstants.CHAR_146_4);
			Assert.assertEquals(utilsCheck,checkResult);

			waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
			//打标‘冷链卡班’验证
			boolean hasFlag = (context.getBasePrintWaybill().getjZDFlag() != null
					&& context.getBasePrintWaybill().getjZDFlag().equals("冷链卡班"));
			Assert.assertEquals(hasFlag,checkResult);
		}
	}
	/**
	 * 设置aoiCode逻辑测试
	 * @throws Exception
	 */
	@Test
	public void testSetAoiCode() throws Exception{
		WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		context.getBasePrintWaybill().setWaybillCode("JDX000222916956");
		JdResult<List<WaybillFenceDto>> fenceResult = new JdResult<List<WaybillFenceDto>>();
		fenceResult.toSuccess();
		fenceResult.setData(new ArrayList<>());
		WaybillFenceDto fenceData = new WaybillFenceDto();
		fenceResult.getData().add(fenceData);
		fenceData.setDeliveryStage(DmsConstants.WAYBILL_FENCE_DELIVERY_STAGE_2);
		fenceData.setFenceType(DmsConstants.WAYBILL_FENCE_TYPE_AOI);
		fenceData.setFenceId("aoiId001");
		when(waybillQueryManager.getWaybillFenceInfoByWaybillCode(context.getBasePrintWaybill().getWaybillCode())).thenReturn(fenceResult);
		String aoiCode = UUID.randomUUID().toString().toUpperCase().substring(0, 2);
		JdResult<List<AoiBindRoadMappingData>> aoiResult = new JdResult<List<AoiBindRoadMappingData>>();
		aoiResult.toSuccess();
		aoiResult.setData(new ArrayList<>());
		AoiBindRoadMappingData aoiData = new AoiBindRoadMappingData();
		aoiResult.getData().add(aoiData);
		aoiData.setAoiCode(aoiCode);
		when(aoiServiceManager.aoiBindRoadMappingExactSearch(any(AoiBindRoadMappingQuery.class))).thenReturn(aoiResult);
		
		waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
		//检查aoiCode值
		boolean hasFlag = (aoiCode.equals(context.getBasePrintWaybill().getAoiCode()));
		Assert.assertTrue(hasFlag);
		
		context.getBasePrintWaybill().setAoiCode(null);
		context.getBasePrintWaybill().setLocalSchedule(DmsConstants.LOCAL_SCHEDULE);
		waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
		//检查返调度aoiCode值
		hasFlag = (context.getBasePrintWaybill().getAoiCode() == null);
		Assert.assertTrue(hasFlag);
	}
	/**
	 * 设置aoiCode逻辑测试
	 * @throws Exception
	 */
	@Test
	public void testSetRoadCode() throws Exception{
		WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		context.getBasePrintWaybill().setWaybillCode("JDX000222916956");
		String roadCode = "256";
		context.getBasePrintWaybill().setRoadCode(roadCode);
		//检查roadCode值
		boolean hasFlag = (roadCode.equals(context.getBasePrintWaybill().getRoadCode()));
		Assert.assertTrue(hasFlag);
		
		context.getBasePrintWaybill().setLocalSchedule(DmsConstants.LOCAL_SCHEDULE);
		waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
		//检查返调度值
		hasFlag = ("0".equals(context.getBasePrintWaybill().getRoadCode()));
		Assert.assertTrue(!hasFlag);
		
		String unpackClassifyNum="P1";
		context.getBigWaybillDto().getWaybill().getWaybillExt().setUnpackClassifyNum(unpackClassifyNum);
		waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
		//检查拆包场景的值
		hasFlag = (unpackClassifyNum.equals(context.getBasePrintWaybill().getRoadCode()));
		Assert.assertTrue(!hasFlag);		
	}
}
