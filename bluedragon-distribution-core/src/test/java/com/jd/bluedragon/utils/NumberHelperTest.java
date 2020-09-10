package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class NumberHelperTest {

	// @Test
	public void testGt0Number() {
		List<Number> numbers = new ArrayList<Number>();
		numbers.add(null);
		numbers.add(0);
		numbers.add(1);
		numbers.add(-1);
		numbers.add(-1.0d);
		numbers.add(-0.0d);
		numbers.add(-0.01d);
		numbers.add(0.01d);
		numbers.add(new Float(1));
		numbers.add(new Float(-0.01));
		numbers.add(new Float(-1));
		for (Number number : numbers) {
			boolean rest = NumberHelper.gt0(number);
			String msg = "" + number + ":" + rest;
			if (rest) {
				System.out.println(msg);
			} else {
				System.err.println(msg);
			}
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIsStringNumber() {
		System.out.println("========testIsStringNumber========");
		String[][] numbers = new String[][]{{null,"+","-","w"," ","1..","1..1","1.1.",},
				{"0.0","0.00","0.01","0","9","91","1","-1","-1.0","-0.0","-0.01","+1","+1.","+1.0","+0.0","+00.00","001","009","001.999"}
		};
		int cn = 0;
		while(cn<numbers.length){
			for (String number : numbers[cn]) {
				boolean rest = NumberHelper.isBigDecimal(number);
				String msg = "" + number + ":" + rest;
				if (rest) {
					System.out.println(msg);
				} else {
					System.err.println(msg);
				}
				if(cn==0){
					Assert.assertFalse(rest);
				}else{
					Assert.assertTrue(rest);
				}
			}
			++cn;
		}
	}

	@Test
	public void testGt0Str() {
		System.out.println("========testGt0Str========");
		String[][] numbers = new String[][]{
				{null,"0","+","-","",".","w","1..","1..1","-1.0","-0.0","-0.01","-1","+0.0","+00.00","0.0","0.00","-0.0","-0.00",},
				{"0.01","1.","9","91","1","+1","+1.","+1.0","001","009","001.999"}
		};
		int cn = 0;
		while(cn<numbers.length){
			for (String number : numbers[cn]) {
				boolean rest = NumberHelper.gt0(number);
				String msg = "" + number + ":" + rest;
				if (rest) {
					System.out.println(msg);
				} else {
					System.err.println(msg);
				}
				if(cn==0){
					Assert.assertFalse(rest);
				}else{
					Assert.assertTrue(rest);
				}
			}
			++cn;
		}
	}
	@Test
	public void testConvertToInteger() {
		System.out.println("========testGt0Str========");
		String[][] numbers = new String[][]{
				{null,"1.","+","+1","-","","0.0","0.00","0.01","-1.0","-0.0","-0.01","001.999","+1.","+1.0","+0.0","+00.00",}
				,{"0","9","91","1","-1","001","009"}
			};
		int cn = 0;
		while(cn<numbers.length){
			for (String number : numbers[cn]) {
				Integer rest = NumberHelper.convertToInteger(number);
				String msg = "" + number + ":" + rest;
				if (rest!=null) {
					System.out.println(msg);
				} else {
					System.err.println(msg);
				}
				if(cn==0){
					Assert.assertFalse(rest!=null);
				}else{
					Assert.assertTrue(rest!=null);
				}
			}
			++cn;
		}
	}
	@Test
	public void testFormatMoney() {
		System.out.println("========NumberHelper.formatMoney()========");
		Double[] numbers = new Double[]{
				0.11111,
				0.2,
				.2555,
				1.2222,
				00.5555,
				10.525655,
				1100.555545
			};
		String[] strs = new String[]{
				"0.11111",
				"0.2",
				"0.2555",
				"1.2222",
				"00.5555",
				"10.525655",
				"1100.555545322222222222222222222222222222"
			};
		String[] results = new String[]{
				"0.11",
				"0.20",
				"0.26",
				"1.22",
				"0.56",
				"10.53",
				"1100.56"
			};
		int cn = 0;
		for (Double number : numbers) {
			System.err.println("数字："+number+":"+NumberHelper.formatMoney(number));
			System.err.println("字符串："+number+":"+NumberHelper.formatMoney(strs[cn]));
			Assert.assertEquals(results[cn], NumberHelper.formatMoney(number));
			Assert.assertEquals(results[cn], NumberHelper.formatMoney(strs[cn]));
			cn++;
		}
			
	}
}
