package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DateHelperTest {
	
	BusinessHelper helper = new BusinessHelper();

	@Test
	public void testIsBoxcode() {
		String dateStr="2018-01-16 17:21:01";
		Date date = DateHelper.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss.SSS");
		Date date1 = DateHelper.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
	}

	@Test
	public void testDateFormat() {
		String[] dateStrs = {
				"2019-01-01 12:00:00",
				"2019/01/01 12:00:00",
				"2019/01/01 12:00:00.000",
				"2019-01-01 12:00:00.000",
				"20190101120000",
				"20190101120000000"
		};
		for (String dateStr : dateStrs) {
			Date date1 = DateHelper.parseAllFormatDateTime(dateStr);
			String reDateStr1 = DateHelper.formatDate(DateHelper.add(date1, Calendar.MILLISECOND, Constants.DELIVERY_DELAY_TIME), Constants.DATE_TIME_MS_FORMAT);
			System.out.println("测试 -- 原时间：" + dateStr + "，现时间：" + reDateStr1);
		}
	}
}
