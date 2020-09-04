package com.jd.bluedragon.common.router;

/**
 * 本项目所需要的所有的数据库实例的句柄
 * basic基础库
 * waybill运单分库前缀按照10 hash
 * waybill_main运单主库，按照128 hash
 */
public class Partition {
	//基础库
	//存储各种基础表数据
	//site
	//waybill_cancel
	//box
	//cross_tag
	public static final String BASIC_DB = "basic";
	
	public static final String WAYBILL_DB = "waybill";

	public static final String WAYBILL_DB_MAIN = "waybill_main";
	
}