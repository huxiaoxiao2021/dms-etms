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

	@Test
	public void testAdjustTime() {
		String date1 = "2020-03-03 16:31:04";
		System.out.println(DateHelper.adjustTimeToNow(DateHelper.parseAllFormatDateTime(date1), 24));
		String date2 = "2020-03-04 16:57:13";
		System.out.println(DateHelper.adjustTimeToNow(DateHelper.parseAllFormatDateTime(date2), 24));
	}


	@Test
	public void testGetDateFormat() {
		String[] dateStrList = new String[]{
				"2020-03-06 11:16:15",
				"2020-03-06 11:16:15.124",
				"2020/03/06 11:16:15",
				"2020/03/06 11:16:15.123",
				"2020-3-6 11:16:15",
				""};
		for (String dateStr : dateStrList) {
			String dateFormat = DateHelper.getDateFormat(dateStr);
			if (StringHelper.isNotEmpty(dateFormat)) {
				String newOperateTime = DateHelper.formatDate(
						DateHelper.adjustTimeToNow(
								DateHelper.parseDate(dateStr,dateFormat),
								24),
						dateFormat
				);
				System.out.println(newOperateTime);
			} else {
				System.out.println("未知的离线任务时间格式【{}】，请注意代码适配问题。"+ dateStr);
			}
		}
		System.out.println("----:" + DateHelper.getDateFormat(null));
	}
}
