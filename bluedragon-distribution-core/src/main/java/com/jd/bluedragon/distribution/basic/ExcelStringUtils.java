package com.jd.bluedragon.distribution.basic;

public class ExcelStringUtils {

	public static boolean isNull(String val){
		boolean rs = false;
		if(val == null || "".equals(val.trim()) || "null".equals(val.toLowerCase())){
			rs = true;
		}
		return rs;
	}
	
	public static boolean isNotNull(String val){
		return !isNull(val);
	}
}
