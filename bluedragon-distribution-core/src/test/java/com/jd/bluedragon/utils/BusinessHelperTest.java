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
		System.err.println(BusinessHelper.getWaybillSignTexts("30011000051000020000000000000000000000000000000000", 4,10,31));
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

}
