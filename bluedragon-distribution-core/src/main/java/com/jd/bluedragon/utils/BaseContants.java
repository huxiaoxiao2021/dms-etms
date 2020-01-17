package com.jd.bluedragon.utils;

import java.util.HashMap;
import java.util.Map;

public class BaseContants {
	public static final int SITE = 1;
	public static final int DISTRIBUTE_CENTER = 2;
	public static final int TRADER_TYPE = 153;
	public static final int THIRD_SITE = 16;

	public static final int  THIRDPL_SMS_TYPE_INIT = 0;		//3PL_SMS_TYPE 初始值、插入失败
	public static final int  THIRDPL_SMS_TYPE_SUCCESS = 1;	//3PL_SMS_TYPE 插入成功 
	public static final int  THIRDPL_SMS_TYPE_EXIST = 2;	//3PL_SMS_TYPE 存在此记录
	
	public static final int BASIC_ALL_ORG_COL = -1;	//所有机构标识
	public static final String BASIC_ORG_COL = "orgId";		//机构标识
	public static final String BASIC_SITE_COL = "siteId";		//站点标识
	public static final String BASIC_STAFF_COL = "staffId";	//员工标识
	public static final String BASIC_YN_COL = "yn";	//是否有效
	
	public static final int PDA_USER_GETINFO_SUCCESS = 1; //获取信息成功
	public static final int PDA_USER_LOGIN_FAILUE = -1; //验证失败
	public static final int PDA_USER_GETINFO_FAILUE = 0; //获取信息失败
	public static final String PDA_USER_GETINFO_SUCCESS_MSG = "获取信息成功"; //成功信息
	public static final String PDA_USER_LOGIN_FAILUE_MSG = "验证失败"; //验证失败信息
	public static final String PDA_USER_GETINFO_FAILUE_MSG = "获取信息失败"; //获取信息失败
	
	public static final String PDA_THIRDPL_TYPE = "3pl_"; //小第三方	
	public static final String PDA_BIG_THIRDPL_TYPE = "third_"; //大第三方（申通、圆通）
	public static final int PDA_THIRDPL_ID = 3000000;
	public static final int PDA_BIG_THIRDPL_ID = 6000000;	
	
	public static final int OWN_STAFF_TYPE = 1; //自营员工类型
	
	public static final int  BASIC_DATA_SYNC_INIT = 0; // 基础资料数据同步初始值、插入失败
	public static final int  BASIC_DATA_SYNC_SUCCESS = 1; // 基础资料数据同步插入成功 
	public static final int  BASIC_DATA_SYNC_EXIST = 2;	// 基础资料数据同步存在此记录
	
	public static final int  BASIC_B_TRADER_ORG = -100; //B商家对应机构
	public static final String   BASIC_B_TRADER_ORG_NAME = "B商家机构"; //B商家对应机构名称
	public static final int  BASIC_B_TRADER_SITE_TYPE = 1024; //B商家对应站点类型
	public static final int  BASIC_B_TRADER_CODE_LENGTH = 8; //B商家编号对应长度
	public static final Integer  BASIC_B_THIRD_USER_TYPE = 1;  //0:自营 1:第三方人员信息类型
	
	public static final int  BASIC_AGING_TYPE = 6054;//全网时刻时效类型
	public static final int  BASIC_FLIGHTS_TYPE = 6055;//全网时刻班次类型
	public static final int  BASIC_FORWARD_MOMENT_TYPE = 7000;//全网时刻 正向时刻类型（测试库）
	
	public static final Map<Integer,String> diySiteType=new HashMap<Integer,String>();
	static{
		diySiteType.put(8, "京东自提点");
		diySiteType.put(44, "便利店联营点(WOW)");
		//diySiteType.put(42, "便利店联营点(好邻居)"); //最后确认过滤掉
		diySiteType.put(26, "好邻居联营点");
		diySiteType.put(128, "校园联营点");
		diySiteType.put(28, "自提柜");
		diySiteType.put(30, "社区合作联营点");
		diySiteType.put(36, "公交客运联营点");
		diySiteType.put(22, "合作站");
		
	}
	public static final int BASIC_VEHICLE_MANAGER = 7007;//车辆管理
	public static final int BASIC_VEHICLE_BRANDS = 2;//车辆品牌
	public static final int BASIC_VEHICLE_MODEL = 3;//车辆型号
	public static final int BASIC_VEHICLE_TYPE = 4;//车辆类型
	public static final int BASIC_TIRE_MODEL = 5;//轮胎规格型号
	public static final int BASIC_MAKE_COMPANY = 6;//车辆制造商
	public static final int BASIC_MAKE_ADDRESS = 7;//车辆产地
	public static final int BASIC_VEHICLE_STATUS = 8;//车辆状态
	public static final int BASIC_EMISSION_STANDARD = 19;//发动机排放标准
	public static final int BASIC_TIRE_AMOUNT = 20;//轮胎数量
	public static final int BASIC_AXEX_AMOUNT = 21;//轴数		public static final int BASIC_SAF_STATUS_OK=200; //saf接口调用状态码，成功
	public static final int BASIC_SAF_STATUS_ERROR=300; //saf接口调用状态码,失败
	public static final int BASIC_SAF_STATUS_OK=200; //saf接口调用状态码，成功
	
	//===============================================================3cod对外接口常量
	public static final int BASIC_COD_PAGESIZE=500; //获取cod分页数据每页返回多少条数据
	public static final int COD_EXPIRY=7200;//3cod接口缓存数据有效期
	
	public static final String ALL_SHIPPERS="all_shippers"; //所有承运商缓存key
	public static final String COD_SHIPPERS="cod_shippers"; //支持货到付款的承运商key
	public static final String NON_COD_SHIPPERS="non_cod_shippers"; //支持货到付款的承运商key
	
	public static final String PAGE_COD_SITECODE_PAGENUM="page_cod_%s_%s"; //第一%s是sitecode，第二个是%s是pagenum
	public static final String PAGE_NON_COD_SITECODE_PAGENUM="page_non_cod_%s_%s";//第一%s是sitecode，第二个是%s是pagenum
	
	public static final String PAGE_COD_SITECODE="page_cod_%s"; //第一%s是sitecode，支持货到付款的总页数key
	public static final String PAGE_NON_COD_SITECODE="page_non_cod_%s";////第一%s是sitecode，非支持货到付款的总页数key
	
	public static final int TRADER_MOLD = 7024;//商家维护页面商家类别
	public static final int PAY_COMPANY = 7025;//商家维护页面付款公司
	public static final int PAY_COMPANY_BUMEN = 7026;//商家维护页面付款公司所属部门
	public static final Integer RETURN_MODE = 70330312;//退货方式
	public static final Integer GOODS_CATEGORY_CODE = 70330314;//商品品类
	public static final int TRADER_ADDRESS = 7027;//商家地址类型
	public static final int BASIC_SITE_SELFD_SUBTYPE = 28;//自提柜站点类型
	public static final String TRADER_RIGHT_GROUP="TMS-BASE-SJRG";//商家维护权限-集团用户
	public static final String TRADER_RIGHT_LOCALMANAGER="TMS-BASE-SJRM";//商家维护权限-区域经理
	public static final String TRADER_RIGHT_LOCAL="TMS-BASE-SJRL";//商家维护权限-区域非经理

	
	public static final String DEFAULTSMALLIMAGE = "http://img30.360buyimg.com/basic/g14/M05/11/10/rBEhV1MFxssIAAAAAAAY53MUMnUAAI6iwIGcz0AABj_662.jpg";
			//"http://img30.360buyimg.com/basic/g14/M0A/0E/06/rBEhV1Lgn4IIAAAAAAASkhyzehkAAIQ6wAwRqUAABKq415.jpg";
	public static final String DETAULTBIGIMAGE ="http://img30.360buyimg.com/basic/g15/M02/06/19/rBEhWFMFxg0IAAAAAAE2IaisryYAAIyOgP5Z_cAATY5803.png"; 
			//"http://img30.360buyimg.com/basic/g15/M08/03/0F/rBEhWVLgnrwIAAAAAAANBIOyqwEAAIIogP_rJIAAA0c248.jpg";
	
	public static final int SANFANGKUAIDI=16;//站点类型为第三方快递类型
	public static final int ZITIGUI=28;//站点类型为自提柜类型
	public static final int DMS=64;//站点类型为分拣中心类型
	public static final int ECONOMIC_NET_SITE=10000;//站点类型为经济网类型
	public static final int LOCAL=1;//道口类型为本地，即分拣中心和站点绑定的分拣中心一致
	public static final int DIRECT=2;//道口类型直发，分拣中心和站点绑定的不一致，但还是维护了道口
	public static final int CROSS=3;//跨分拣道口
	public static final int WORK_TYPE_CROSSWMS =1;//原始道口同步任务类型
	public static final int WORK_TYPE_CALCULATECROSS =2;//大全表计算总任务
	public static final int WORK_TYPE_DESCENDCROSS =3;//道口大全表下发总任务
	public static final int WORK_TYPE_SITETOWMS =3;//站点同步wms
	public static final int ALLDMSALLSTORE =11;//大全表计算--计算所有分拣中心到所有库房
	public static final int SINGLEDMSSINGLESTORE =12;//大全表计算--新增库房和分拣中心绑定关系，计算指定分拣中心到指定库房
	public static final int ADDSINGLECROSS =13;//大全表计算--新增站点道口，计算单条站点道口信息到绑定库房
	public static final int SITECHANGEDMS =14;//大全表计算--站点更换绑定分拣中心或名称
	public static final int DELETESTORECROSS =15;//大全表计算--删除库房和分拣中心绑定关系，库房分拣中心绑定关系删除
	public static final int DELETESITECROSS =16;//大全表计算--删除单条站点道口数据
	public static final int EDITSINGLESITECROSS =17;//大全表计算--修改单个站点道口
	public static final int ADDAIRCONFIG =18;//大全表计算--新增航空配置
	public static final int DELETEAIRCONFIG =19;//大全表计算--删除航空配置
	public static final int NODMSSITE =20;//大全表计算--没有绑定分拣中心站点更改名称或省市县
	public static final int DMSNAMECHANGE =21;//大全表计算--分拣中心改名称
	public static final int EDITSINGLEDMSCROSS =22;//大全表计算--分拣--分拣道口修改
	public static final int DMSASSORTCHANGE =23;//大全表计算--分拣中心省市修改
	public static final int SITENAMECHANGE =24;//大全表计算--站点名称变化
	
	public static final int DATAEXCEPTION = 0;//道口维护数据异常
	public static final int MULTICROSS = -1;//道口维护校验重复道口
	public static final int MULTITABLETRORY = -2;//道口维护校验重复茏车
	public static final int NOSITEEXIST = -3;//道口维护输入站点不存在
	
	public static final int SUCCESS = 1;//成功
	public static final int FAIL = 0;//成功
	public static final int EXCEPTION = 2;//异常
	public static final String QUEUEID0 = "0";
	public static final String QUEUEID1 = "1";
	public static final int ORIGINALSTATUS = 4;
	public static final int SPECIALSTATUS = 5;
	
	
	
	public static final int SEPAICL_WORK_TASK = 4;//work特殊限制任务类型，目前针对亚一统一包裹标签
	public static final int TRADER_MOLD_POP = 1001;//商家类别pop商家
	
	public static final String SITE_CODE_SEQ = "站点Code序列";

	
}
