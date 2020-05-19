package com.jd.bluedragon.distribution.reverse.service;

import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.service.AbnormalWayBillService;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.reverse.domain.GuestBackSubTypeEnum;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.test.utils.UtilsForTestCase;

@RunWith(MockitoJUnitRunner.class)
public class ReverseSendServiceImplTestCase {
	@Mock
	ReverseSendServiceImpl reverseSendServiceImpl;
	@Mock
	JsfSortingResourceService jsfSortingResourceService;
	@Mock
    private WaybillTraceManager waybillTraceManager;
	@Mock
    AbnormalWayBillService abnormalWayBillService;
	/**
	 * 逆向报文测试
	 * @throws Exception
	 */
    @Test
    public void testReverseSubType() throws Exception{
    	
		String[] waybillCodes = {
			"V000000000011",
			"V000000000022",
			"V000000000033",
			"V000000000044",
			"V000000000055",
			"V000000000066",
		};
		String[] tWaybillCodes ={
				"V000000000011",
				"V000000000022",
				"V000000000033",
				"V000000000044",
				"V000000000055",
				"V000000000066",
		};
		GuestBackSubTypeEnum[] guestBackSubTypes ={
				GuestBackSubTypeEnum.SICK_01,
				GuestBackSubTypeEnum.CUSTOMER_01,
				GuestBackSubTypeEnum.CUSTOMER_02,
				GuestBackSubTypeEnum.CUSTOMER_03,
				GuestBackSubTypeEnum.PRE_SELL_01,
				GuestBackSubTypeEnum.PRE_SELL_01
		};
		when(waybillTraceManager.isWaybillRejected("V000000000022")).thenReturn(true);
		when(jsfSortingResourceService.getWaybillCancelByWaybillCode("V000000000033")).thenReturn(1000);
		AbnormalWayBill abnormalWayBillForPreSell = new AbnormalWayBill();
		abnormalWayBillForPreSell.setQcType(2);
		abnormalWayBillForPreSell.setQcName("24-预售未付全款退仓");
		when(abnormalWayBillService.queryAbnormalWayBillByWayBillCode("V000000000066")).thenReturn(abnormalWayBillForPreSell);
		for(int i=0 ; i < waybillCodes.length; i++ ){
			String wayBillCode = waybillCodes[i];
			String tWayBillCode = tWaybillCodes[i];
			System.err.println(waybillCodes[i]);
			GuestBackSubTypeEnum subType = reverseSendServiceImpl.getGuestBackSubType(getReverseSendWms(wayBillCode),wayBillCode, tWayBillCode);
			Assert.assertEquals(guestBackSubTypes[i],subType);
		}
    }
    private ReverseSendWms getReverseSendWms(String waybillCode){
    	ReverseSendWms reverseSendWms = new ReverseSendWms();
    	if("V000000000011".equals(waybillCode)){
    		reverseSendWms.setSickWaybill(true);
    	}
    	if("V000000000055".equals(waybillCode)){
    		reverseSendWms.setSendPay(UtilsForTestCase.getSignString(500,297,'1'));
    	}
    	if("V000000000066".equals(waybillCode)){
    		reverseSendWms.setSendPay(UtilsForTestCase.getSignString(500,297,'2'));
    	}
    	return reverseSendWms;
    }
    
}
