package com.jd.bluedragon.distribution.basic;

import org.apache.poi.ss.usermodel.Row;

public class ExcelContext {

	public static ThreadLocal<Row> rowContext = new ThreadLocal<Row>();
	
	private ExcelContext(){
		
	}
	
	public static Row getCurrentRow(){
		return rowContext.get();
	}
	
	public static void setCurrentRow(Row row){
		rowContext.set(row);
	}
	
}