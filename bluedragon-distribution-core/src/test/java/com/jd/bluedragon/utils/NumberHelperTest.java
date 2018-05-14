package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NumberHelperTest {
	
	@Test
	public void test_gt0() {
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
		for(Number number:numbers){
			boolean rest = NumberHelper.gt0(number);
			String msg = ""+number+":"+rest;
			if(rest){
				System.out.println(msg);
			}else{
				System.err.println(msg);
			}
		}
	}
}
