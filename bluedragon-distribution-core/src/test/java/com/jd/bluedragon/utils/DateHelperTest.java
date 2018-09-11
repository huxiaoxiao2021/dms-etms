package com.jd.bluedragon.utils;

import java.util.Date;

import org.junit.Test;

public class DateHelperTest {
	
	BusinessHelper helper = new BusinessHelper();

	@Test
	public void testIsBoxcode() {
		String dateStr="2018-01-16 17:21:01";
		Date date = DateHelper.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss.SSS");
		Date date1 = DateHelper.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
	}
}
