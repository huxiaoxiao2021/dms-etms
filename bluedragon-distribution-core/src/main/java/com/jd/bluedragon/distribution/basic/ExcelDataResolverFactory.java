package com.jd.bluedragon.distribution.basic;

public class ExcelDataResolverFactory {

	public static final int EXCEL_2003 = 1;
	public static final int EXCEL_2007 = 2;
	
	public static DataResolver getDataResolver(int type){
		DataResolver dataResolver = null;
		if(type == EXCEL_2003){
			dataResolver = new Excel03DataResolver();
		}else if(type == EXCEL_2007){
			dataResolver = new Excel07DataResolver();
		}else{
			throw new IllegalArgumentException("ExcelDataResolverFactory没有找到匹配的类型,type:" + type);
		}
		return dataResolver;
	}
}
