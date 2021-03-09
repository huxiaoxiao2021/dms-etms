package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SqlUtilsTest {
	@Test
	public void testGenOrderBySql() {
		List<Map<String,String>> orderByList = new ArrayList<Map<String,String>>();
		Map<String,String> col1 = new HashMap<String,String>();
		col1.put("orderColumn", "column1");
		col1.put("orderState", "descending");
		orderByList.add(col1);
		Map<String,String> col2 = new HashMap<String,String>();
		col2.put("orderColumn", "column2");
		col2.put("orderState", "asc");
		orderByList.add(col2);

		Map<String,String> col3 = new HashMap<String,String>();
		col3.put("orderColumn", "column3");
		col3.put("orderState", "");
		orderByList.add(col3);
		
		Map<String,String> columNameMap = new HashMap<String,String>();
		columNameMap.put("column1", "db_column1");
		
		String orderBy = SqlUtils.genOrderBySql(orderByList, columNameMap);
		Assert.assertTrue("db_column1 desc,column2 asc".equals(orderBy));
		System.out.println("----:" + orderBy);
	}
}
