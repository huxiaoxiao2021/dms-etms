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
public class NoTouchPrintTestCase {
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
	 * 无接触面单测试
	 * @throws Exception
	 */
    @Test
    public void testNoTouch() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,295,'1'),
				UtilsForTestCase.getSignString(500,295,'2'),
				UtilsForTestCase.getSignString(500,295,'3'),
				UtilsForTestCase.getSignString(500,295,'4'),};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				true,
				true,
				true,
				true,};
		String[] waybillSigns = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,33,'9'),
				UtilsForTestCase.getSignString(500,33,'A'),
				UtilsForTestCase.getSignString(500,33,'B'),
				UtilsForTestCase.getSignString(500,33,'C'),
				};
		boolean[] waybillSignChecks ={
				false,
				false,
				false,
				true,
				true,
				true,
				true,};
		for(int i=0 ; i < sendPays.length; i++ ){
			for(int j=0 ; j < waybillSigns.length; j++ ){
				System.err.println(i+"-"+j);
				System.err.println(sendPays[i]);
				System.err.println(waybillSigns[j]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getBigWaybillDto().getWaybill().setWaybillSign(waybillSigns[j]);
				context.getBigWaybillDto().getWaybill().getWaybillExt().setContactlessPlace("运单下传tagContent");
				context.getBasePrintWaybill().setSpecialMark(null);
				context.getBasePrintWaybill().setPrintAddress(null);
				context.getBasePrintWaybill().setPrintAddressRemark(null);
				
				waybillCommonServiceImpl.setBasePrintInfoByWaybill(context.getBasePrintWaybill(), context.getBigWaybillDto().getWaybill());
				//预期验证结果
				boolean isNoTouch = sendPayChecks[i]||waybillSignChecks[j];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isNoTouchService(sendPays[i], waybillSigns[j]);
				Assert.assertEquals(utilsCheck,isNoTouch);
				
				customerAndConsignerInfoHandler.handle(context);
				//打标‘代’验证
				boolean hasFlag = (context.getBasePrintWaybill().getSpecialMark() != null 
						&& context.getBasePrintWaybill().getSpecialMark().contains("代"));
				Assert.assertEquals(hasFlag,isNoTouch);
				
				//地址追加验证
				boolean hasAdressTagContent = (context.getBasePrintWaybill().getPrintAddress() != null 
						&& context.getBasePrintWaybill().getPrintAddress().contains("运单下传tagContent"));
				Assert.assertEquals(hasAdressTagContent,isNoTouch);
				
				context.getBasePrintWaybill().setPrintAddress(null);
				hideInfoComposeServiceImpl.handle(context.getResponse(), 910, 0);
				//地址追加验证2
				boolean hasAdressTagContent2 = (context.getBasePrintWaybill().getPrintAddress() != null 
						&& context.getBasePrintWaybill().getPrintAddress().contains("运单下传tagContent"));
				Assert.assertEquals(hasAdressTagContent2,isNoTouch);
			}
		}
	}
}
