package com.jd.bluedragon.distribution.test.print;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.service.HideInfoComposeServiceImpl;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.CustomerAndConsignerInfoHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
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
    HideInfoService hideInfoService;

    @Mock
    private BasicSafInterfaceManager basicSafInterfaceManager;

    @Mock
    private PreseparateWaybillManager preseparateWaybillManager;

    @Mock
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
	
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
}
