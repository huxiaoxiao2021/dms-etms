package com.jd.bluedragon.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		boolean isWaybillCode = helper.isWaybillCode(waybillCodeT).booleanValue();
		assertTrue(isWaybillCode);
		
		String waybillCodeF="F18655150305";
		isWaybillCode = helper.isWaybillCode(waybillCodeF).booleanValue();
		assertTrue(isWaybillCode);
		
		String packageCode="T18655150305-1-1-1";
		isWaybillCode = helper.isWaybillCode(packageCode).booleanValue();
		assertFalse(isWaybillCode);
		
		String boxCode="TC010A001010F00500039001";
		isWaybillCode = helper.isWaybillCode(boxCode).booleanValue();
		assertFalse(isWaybillCode);
		//大件运单规则 老
		String waybillCodeLD="LD1234567890";
		isWaybillCode = helper.isWaybillCode(waybillCodeLD).booleanValue();
		assertTrue(isWaybillCode);
		//大件运单规则 新
		String waybillCodeLD1="JDLA12345678901";
		isWaybillCode = helper.isWaybillCode(waybillCodeLD1).booleanValue();
		assertTrue(isWaybillCode);

		assertFalse(helper.isWaybillCode("12345678901-1-0-1").booleanValue());
	}

	@Test
	public void testIsPackageCode(){
		assertTrue(helper.isPackageCode("12345678901-1-0-1").booleanValue());
		//大件包裹号规则 老
		assertTrue( helper.isPackageCode("LD1234567890-1-3").booleanValue());
		//大件包裹号规则 新
		assertTrue( helper.isPackageCode("JDLD12345678901-1-3").booleanValue());

		assertTrue( helper.isPackageCode("T18655150305-1-1-").booleanValue());

		assertTrue(helper.isPackageCode("T18655150305-1-1-3").booleanValue());

		assertTrue(helper.isPackageCode("F18655150305-1-1-").booleanValue());

		assertTrue(helper.isPackageCode("F18655150305-1-1-2").booleanValue());

		assertTrue(helper.isPackageCode("VA66679375345-1-1-2").booleanValue());
	}

	@Test
	public void testgetWaybillCode(){
		assertTrue(WaybillUtil.getWaybillCode("VA00084590155-3-10-").equals("VA00084590155"));
		assertTrue(WaybillUtil.getWaybillCode("LD1234567890-1-3").equals("LD1234567890"));
		assertTrue(WaybillUtil.getWaybillCode("JDLD12345678901-1-3").equals("JDLD12345678901"));
	}

	@Test
	public void testGetPackNumByPackCode(){
		assertTrue(helper.getPackageNum("12345678901-1-9-1")==9);
		assertTrue(helper.getPackageNum("LD1234567890-1-9")==9);
		assertTrue(helper.getPackageNum("JDLD1234567890-1-9")==9);
		assertTrue(helper.getPackageNum("JDJ072196021451-1-9")==9);
	}


	@Test
	public void testIsECLPCode() {
		boolean shoudFalse = false;
		boolean shoudTrue = true;

		String sourceCodeNUll=null;
		shoudFalse = helper.isECLPCode(sourceCodeNUll);
		assertFalse(shoudFalse);

		String sourceCodeMMC="mmc";
		shoudFalse = helper.isECLPCode(sourceCodeMMC);
		assertFalse(shoudFalse);

		String sourceCodeECLP="ECLP";
		shoudTrue = helper.isECLPCode(sourceCodeECLP);
		assertTrue(shoudTrue);
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
		assertTrue(BusinessHelper.isReverseSpareCode("un1234567890123456"));
		assertTrue(BusinessHelper.isReverseSpareCode("null1234567890123456"));
		assertTrue(BusinessHelper.isReverseSpareCode("zA1234567890123456"));
		assertTrue(BusinessHelper.isReverseSpareCode("Az1234567890123456"));
		assertFalse(BusinessHelper.isReverseSpareCode("1234567890123456"));
		assertFalse(BusinessHelper.isReverseSpareCode("A11234567890123456"));
		assertTrue(BusinessHelper.isReverseSpareCode("null2017122600001004"));
		assertFalse(BusinessHelper.isReverseSpareCode("^#2017122600001004"));
	}
	@Test
	public void testIsSopOrExternal() {
		assertFalse(BusinessHelper.isReverseSpareCode("40000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessHelper.isReverseSpareCode("20000000000000301000000000000000000000000000000000"));
		assertTrue(BusinessHelper.isReverseSpareCode("K0000000000000301000000000000000000000000000000000"));
		assertTrue(BusinessHelper.isReverseSpareCode("60000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessHelper.isReverseSpareCode("T0000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessHelper.isReverseSpareCode("k11234567890123456"));
		assertTrue(BusinessHelper.isReverseSpareCode("30000000000000301000000000000000000000000000000000"));
		assertFalse(BusinessHelper.isReverseSpareCode("17122600001004"));
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
				System.err.println(code+" "+BusinessHelper.getWaybillCode(code)+"->{"+keys[0]+":"+keys[1]+"}");
			}else{
				System.err.println(code+"->无效包裹号");
			}
		}
	}
}
