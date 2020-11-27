package com.jd.bluedragon.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.junit.Test;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.print.domain.BasePrintWaybill;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;

public class BusinessHelperTest {
	
	BusinessHelper helper = new BusinessHelper();
	@Test
	public void testIsBoxcode() {
		String boxCode="TC010A001010F00500039001";
		boolean isBoxCode = helper.isBoxcode(boxCode).booleanValue();
		assertTrue(isBoxCode);
		
		String waybillCode="T18655150305";
		isBoxCode = helper.isBoxcode(waybillCode).booleanValue();
		assertFalse(isBoxCode);
	}

	@Test
	public void testIsWaybillCode() {
		String waybillCodeT="T18655150305";
		boolean isWaybillCode = WaybillUtil.isWaybillCode(waybillCodeT);
		assertTrue(isWaybillCode);
		
		String waybillCodeF="F18655150305";
		isWaybillCode = WaybillUtil.isWaybillCode(waybillCodeF);
		assertTrue(isWaybillCode);
		
		String packageCode="T18655150305-1-1-1";
		isWaybillCode = WaybillUtil.isWaybillCode(packageCode);
		assertFalse(isWaybillCode);
		
		String boxCode="TC010A001010F00500039001";
		isWaybillCode = WaybillUtil.isWaybillCode(boxCode);
		assertFalse(isWaybillCode);
		//大件运单规则 老
		String waybillCodeLD="LD1234567890";
		isWaybillCode = WaybillUtil.isWaybillCode(waybillCodeLD);
		assertTrue(isWaybillCode);
		//大件运单规则 新
		String waybillCodeLD1="JDLA12345678901";
		isWaybillCode = WaybillUtil.isWaybillCode(waybillCodeLD1);
		assertTrue(isWaybillCode);

		assertFalse(WaybillUtil.isWaybillCode("12345678901-1-0-1"));
	}

	@Test
	public void testIsPackageCode(){
		assertTrue(WaybillUtil.isPackageCode("12345678901-1-0-1"));
		//大件包裹号规则 老
		assertTrue( WaybillUtil.isPackageCode("LD1234567890-1-3"));
		//大件包裹号规则 新
		assertTrue( WaybillUtil.isPackageCode("JDLD12345678901-1-3"));

		assertTrue( WaybillUtil.isPackageCode("T18655150305-1-1-"));

		assertTrue(WaybillUtil.isPackageCode("T18655150305-1-1-3"));

		assertTrue(WaybillUtil.isPackageCode("F18655150305-1-1-"));

		assertTrue(WaybillUtil.isPackageCode("F18655150305-1-1-2"));

		assertTrue(WaybillUtil.isPackageCode("VA66679375345-1-1-2"));
	}

	@Test
	public void testgetWaybillCode(){
		assertTrue(WaybillUtil.getWaybillCode("VA00084590155").equals("VA00084590155"));
		assertTrue(WaybillUtil.getWaybillCode("LD1234567890-1-3").equals("LD1234567890"));
		assertTrue(WaybillUtil.getWaybillCode("JDLD12345678901-1-3").equals("JDLD12345678901"));

		assertTrue(WaybillUtil.isPackageCode("VA00041831580-1-3-"));
		assertTrue(SerialRuleUtil.getWaybillCode("VA00041831580-1-3-").equals("VA00041831580"));
	}

	@Test
	public void testGetPackNumByPackCode(){
		assertTrue(WaybillUtil.getPackNumByPackCode("12345678901-1-9-1")==9);
		assertTrue(WaybillUtil.getPackNumByPackCode("LD1234567890-1-9")==9);
		assertTrue(WaybillUtil.getPackNumByPackCode("JDLD1234567890-1-9")==9);
		assertTrue(WaybillUtil.getPackNumByPackCode("JDJ072196021451-1-9")==9);
	}

	@Test
	public void testGetStoreId() {
		assertTrue(SerialRuleUtil.getStoreIdFromStoreCode("wms-6-1")==1);
		assertTrue(SerialRuleUtil.getStoreIdFromStoreCode("wms-61-1")==1);
		assertTrue(SerialRuleUtil.getStoreIdFromStoreCode("wwwwms-622-1")==1);
		assertTrue(SerialRuleUtil.getStoreIdFromStoreCode("wmsw-622-1")==1);
	}
	@Test
	public void testIsReverseSpareCode() {
		assertTrue(WaybillUtil.isReverseSpareCode("un1234567890123456"));
		assertTrue(WaybillUtil.isReverseSpareCode("null1234567890123456"));
		assertTrue(WaybillUtil.isReverseSpareCode("zA1234567890123456"));
		assertTrue(WaybillUtil.isReverseSpareCode("Az1234567890123456"));
		assertFalse(WaybillUtil.isReverseSpareCode("1234567890123456"));
		assertFalse(WaybillUtil.isReverseSpareCode("A11234567890123456"));
		assertTrue(WaybillUtil.isReverseSpareCode("null2017122600001004"));
		assertFalse(WaybillUtil.isReverseSpareCode("^#2017122600001004"));
	}
	@Test
	public void testIsSopOrExternal() {
		assertFalse(BusinessUtil.isSopOrExternal("40000000000000301000000000000000000000000000000000"));
		assertTrue(BusinessUtil.isSopOrExternal("20000000000000301000000000000000000000000000000000"));
		assertTrue(BusinessUtil.isSopOrExternal("KA0000000000000301000000000000000000000000000000000"));
		assertTrue(BusinessUtil.isSopOrExternal("60000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessUtil.isSopOrExternal("T0000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessUtil.isSopOrExternal("k11234567890123456"));
		assertTrue(BusinessUtil.isSopOrExternal("30000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessUtil.isSopOrExternal("17122600001004"));
	}
	@Test
	public void testIsSendCode(){
		assertTrue(BusinessHelper.isSendCode("910-39-20181108163558736"));
	}

	@Test
	public void testGetHashKeyByPackageCode() {
		String[] codes = {null,
				"",
				"72945262907N3S5H30",
				"72945262907N200S500H30",
				"72945262907N201S500H30",
				"72945262907N400S300H30",
				"72945262907N500S300H30",
				"72945262907-1-500-",
				"72945262907-200-500-",
				"72945262907-201-500-",
				"72945262907-400-500-",
				"72945262907-500-500-",
				"WW72990321844-1-1-4"
				};
		for(String code:codes){
			String[] keys = BusinessHelper.getHashKeysByPackageCode(code);
			if(keys!=null){
				System.err.println(code+" "+WaybillUtil.getWaybillCode(code)+"->{"+keys[0]+":"+keys[1]+"}");
			}else{
				System.err.println(code+"->无效包裹号");
			}
		}
	}

	@Test
	public void test() {
		System.out.println(String.format("%03d",-1));
		System.out.println(String.format("%03d",0));
		System.out.println(String.format("%03d",1));
		System.out.println(String.format("%03d",100));
		System.out.println(String.format("%03d",10));
		System.out.println(String.format("%03d",10101));
	}
}
