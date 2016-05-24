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

}
