package com.jd.bluedragon.utils;

import static org.junit.Assert.*;

import org.junit.Test;

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
		String[] codes = {"wms-6-1","wms-61-2","wwwwms-622-3","wmsw-622-44"};
		for(String code:codes){
			Integer storeId = SerialRuleUtil.getStoreIdFromStoreCode(code);
			System.err.println(code+"->"+storeId);
		}
	}
	@Test
	public void testIsReverseSpareCode() {
		String[] codes = {"un1234567890123456","null1234567890123456",
				"zA1234567890123456","Az1234567890123456",
				"1234567890123456","A11234567890123456",
				"null2017122600001004","^#2017122600001004"};
		for(String code:codes){
			boolean storeId = BusinessHelper.isReverseSpareCode(code);
			System.err.println(code+"->"+storeId);
		}
	}
	@Test
	public void testIsSopOrExternal() {
		System.err.println("根据waybillSign判断是否SOP和纯外单");
		String[] codes = {"40000000000000301000000000000000000000000000000000","20000000000000301000000000000000000000000000000000",
				"K0000000000000301000000000000000000000000000000000","60000000000000301000000000000000000000000000000000",
				"T0000000000000301000000000000000000000000000000000","k11234567890123456",
				"30000000000000301000000000000000000000000000000000","17122600001004"};
		for(String code:codes){
			boolean storeId = BusinessHelper.isSopOrExternal(code);
			System.err.println(code+"->"+storeId);
		}
	}
}
