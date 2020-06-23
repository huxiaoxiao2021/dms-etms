package com.jd.bluedragon.distribution.test.print;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.BasicSafInterfaceManager;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.service.HideInfoService;
import com.jd.bluedragon.distribution.print.service.PreSortingSecondService;
import com.jd.bluedragon.distribution.print.service.SpecialMarkComposeServiceImpl;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.BasicWaybillPrintHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.PromiseWaybillHandler;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;
import com.jd.bluedragon.distribution.testCore.base.EntityUtil;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
/**
 * 
 * @ClassName: WrcpsPrintTestCase
 * @Description: 无人车业务测试类
 * @author: wuyoude
 * @date: 2020年6月11日 下午4:47:58
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WrcpsPrintTestCase {
	@InjectMocks
	WaybillCommonServiceImpl waybillCommonServiceImpl;
	@InjectMocks
	SpecialMarkComposeServiceImpl specialMarkComposeServiceImpl;
	@InjectMocks
	PromiseWaybillHandler promiseWaybillHandler;
	@InjectMocks
	BasicWaybillPrintHandler basicWaybillPrintHandler;
    @Mock
    private ProductService productService;
    @Mock
    private PreSortingSecondService preSortingSecondService;

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
    private BaseMinorManager baseMinorManager;    
    @Mock
    private PopPrintService popPrintService;
    @Mock
    private WaybillPrintService waybillPrintService;
    @Mock
    private WaybillPickupTaskApi waybillPickupTaskApi;
    @Mock
    private WaybillCommonService waybillCommonService;
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
    public void testWrcps() throws Exception{
    	WaybillPrintContext context = EntityUtil.getInstance(WaybillPrintContext.class);
		String[] sendPays = {
				null,
				"",
				"000",
				UtilsForTestCase.getSignString(500,307,'1'),
				UtilsForTestCase.getSignString(500,307,'2')
				};
		boolean[] sendPayChecks ={
				false,
				false,
				false,
				true,
				false};
		for(int i=0 ; i < sendPays.length; i++ ){
				System.err.println(sendPays[i]);
				context.setBasePrintWaybill(context.getResponse());
				context.getBigWaybillDto().getWaybill().setSendPay(sendPays[i]);
				context.getWaybill().setSendPay(sendPays[i]);
				context.getResponse().setSendPay(sendPays[i]);
				//预期验证结果
				boolean isWrcps = sendPayChecks[i];
				
				//工具类业务方法校验
				boolean utilsCheck = BusinessUtil.isWrcps(sendPays[i]);
				Assert.assertEquals(utilsCheck,isWrcps);
				//打标‘车’验证
				context.getBasePrintWaybill().setSpecialMark(null);
				specialMarkComposeServiceImpl.handle(context.getResponse(), 910, 0);
				boolean hasFlag = (context.getBasePrintWaybill().getSpecialMark() != null 
						&& context.getBasePrintWaybill().getSpecialMark().contains(TextConstants.WRCPS_FLAG));
				Assert.assertEquals(hasFlag,isWrcps);
				
				context.getBasePrintWaybill().setPromiseText(null);
				context.getBigWaybillDto().getWaybill().setRequireStartTime(DateHelper.parseAllFormatDateTime("2019-09-10 09:10:56.456"));
				context.getBigWaybillDto().getWaybill().setRequireTime(DateHelper.parseAllFormatDateTime("2019-09-11 23:10:56.456"));
				promiseWaybillHandler.dealJZD(sendPays[i], "", context.getBigWaybillDto().getWaybill(), context.getBasePrintWaybill());
				//时效显示验证
				String promiseTextResult = "2019-09-10 09:10-2019-09-11 23:10";
				boolean hasPromise = (promiseTextResult.equals(context.getBasePrintWaybill().getPromiseText()));
				Assert.assertEquals(hasPromise,isWrcps);
				//时效显示验证1
				boolean hasPromise1 = (context.getBasePrintWaybill().getPromiseTextC()!=null 
						&& context.getBasePrintWaybill().getPromiseTextC().contains(promiseTextResult));
				Assert.assertEquals(hasPromise1,isWrcps);
						
				String targetAdress = "测试自提点地址"+i;
				context.getBasePrintWaybill().setPrintAddress(null);
				BaseEntity<BigWaybillDto> waybillReturn =new BaseEntity<BigWaybillDto>();
				waybillReturn.setResultCode(Constants.RESULT_SUCCESS);
				waybillReturn.setData(context.getBigWaybillDto());
				when(waybillQueryManager.getWaybillDataForPrint(Mockito.anyString())).thenReturn(waybillReturn);
				JdResult<CrossPackageTagNew> crossPackageTag = new JdResult<CrossPackageTagNew>();
				crossPackageTag.toSuccess();
				crossPackageTag.setData(new CrossPackageTagNew());
				crossPackageTag.getData().setPrintAddress(targetAdress);
				when(baseMinorManager.queryCrossPackageTagForPrint(Mockito.any(BaseDmsStore.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(crossPackageTag);
				when(waybillCommonService.convWaybillWS(context.getBigWaybillDto(), true, true,true,false)).thenReturn(context.getWaybill());
				when(preSortingSecondService.preSortingAgain(context)).thenReturn(null);
				basicWaybillPrintHandler.handle(context);
				//地址显示验证
				boolean hasAdress = (targetAdress.equals(context.getBasePrintWaybill().getPrintAddress()));
				Assert.assertEquals(hasAdress,isWrcps);
		}
	}
}
