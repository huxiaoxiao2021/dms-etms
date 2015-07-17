package com.jd.bluedragon.distribution.test;

import java.math.BigDecimal;

import org.junit.Test;



public class GuavaTestCase {
	
	@Test
	public void test_lists() {
		BigDecimal d1 = new BigDecimal("0");
		String d2 = "0.00000";
	
		System.out.println(d1.compareTo(new BigDecimal(d2))  == 0);
	}
}
