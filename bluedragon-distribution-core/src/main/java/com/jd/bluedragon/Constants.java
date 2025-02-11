package com.jd.bluedragon;

import java.util.*;

public class Constants {
    public static final char WAYBILL_SIGN_B='3';
    public static final String MAX_PACK_NUM = "MAX_PACK_NUM";
    public static final String REST_KEY = "REST_KEY";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT1 = "yyyyMMdd";
    public static final String DATE_FORMAT2 = "yyMMdd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_TIME_MS_STRING = "yyyyMMddHHmmssSSS";
    /* 默认系统开始时间的标值 */
    public static final String DATE_START = "2012-01-01";

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";

    public static final String MAIN_CONFIGNAME = "app.properties";

    public static final String IMPORTANT_CONFIGNAME = "important.properties";

    public static final String POPABNORMAL_CONFIGNAME = "popabnormal.properties";

    public static final String DMS_IP_MAPPING_CONFIGNAME = "dms_ip_mapper.properties";

    public static final String REST_URL = "/services";

    public static final String SYS_NAME = "sys.dms";

    public static final String SEPARATOR_COMMA = ",";
    /**
     * 中文-'，'
     */
    public static final String SEPARATOR_COMMA_CN = "，";
    public static final String SEPARATOR_APOSTROPHE = "'";
    public static final String SEPARATOR_SEMICOLON = ";";
    public static final String SEPARATOR_HYPHEN = "-";
    public static final String SEPARATOR_COLON = ":";
    public static final String SEPARATOR_BLANK_SPACE = " ";
    public static final String SEPARATOR_TILDE = "~";

    public static final String PUNCTUATION_OPEN_BRACKET = "[";
    public static final String PUNCTUATION_CLOSE_BRACKET = "]";
    public static final String PUNCTUATION_OPEN_BRACKET_SMALL = "(";
    public static final String PUNCTUATION_CLOSE_BRACKET_SMALL = ")";
    public static final String OPERATE_SUCCESS = "1";
    public static final String OPERATE_FAIL = "0";
    public static final String UNDERLINE_FILL = "_";
    public static final String EMPTY_FILL = "";
    public static final String SEPARATOR_ASTERISK = "*";
    public static final String SEPARATOR_VERTICAL_LINE = "|";
    public static final String MIXED_SITE_NAME_PREFIX = "【集】";

    public static final int RESULT_SUCCESS = 1; //成功
    public static final int RESULT_FAIL = 0;    //接口异常或者失败
    public static final int RESULT_ERROR = -1;  //接口内部错误
    public static final int RESULT_WARN = 2;    //接口内部警告

    public static final Integer YN_YES = 1; // 使用
    public static final Integer YN_NO = 0; // 已删除

    public static final int NO_MATCH_DATA = 0;

    public static final String ENCODE = "UTF-8";

    public static final Integer STATUS_UNHANDLED = 0; // 未处理
    public static final Integer STATUS_FINISHED = 1; // 完成

    public static final Integer OPERATE_TYPE_INSPECTION = 1;
    public static final Integer OPERATE_TYPE_SORTING = 2;
    public static final Integer OPERATE_TYPE_SEND = 3;

    /** 业务类型 **/
    public static final int BUSSINESS_TYPE_POSITIVE = 10;
    public static final int BUSSINESS_TYPE_REVERSE = 20;
    public static final int BUSSINESS_TYPE_THIRD_PARTY = 30;
    public static final int BUSSINESS_TYPE_POP = 40;
    public static final int BUSSINESS_TYPE_InFactory = 41;
    public static final int BUSSINESS_TYPE_TRANSFER = 50;
    public static final int BUSSINESS_TYPE_SITE = 60;//配送员上门接货
    public static final int BUSSINESS_TYPE_BDB = 51;//夺宝岛
    public static final int BUSSINESS_TYPE_OEM = 52;//OEM
    public static final int BUSSINESS_TYPE_FC = 53;//返仓
    public static final int BUSSINESS_TYPE_RCD = 57;//反调度重打包裹
    public static final int BUSSINESS_TYPE_NEWTRANSFER=50; //包裹交接类型合一 所有类型都是50

    public static final int BUSSINESS_TYPE_ZY_ORDERTYPE=0;//自营的订单类型/协同仓
    public static final int BUSSINESS_TYPE_DBD_ORDERTYPE=2;//夺宝岛的
    public static final int BUSSINESS_TYPE_OEM_52=52; //协同仓storeid
    public static final int BUSSINESS_TYPE_OEM_58=58; //协同仓storeid
    public static final int BUSSINESS_TYPE_OEM_59=59; //协同仓storeid

    /** 接口调用返回code **/
    public static final int INTERFACE_CALL_SUCCESS = 1;

    /** 返调度再投状态 **/
    public static final int RETURN_SCHEDULING_STATE = 140;
    /** 三方退货站点类型 **/
    public static final int RETURN_PARTNER_SITE_TYPE = 16;
    /** 跨分拣中心收货站点类型一级 **/
    public static final int TRANS_SORTING_SITE_TYPE = 64;
    /** 跨分拣中心收货站点类型二级 **/
    public static final int TRANS_SORTING_SITE_TYPE_SECOND = 256;
    /**
     * 快运中心站点类型
     */
    public static final int B2B_SITE_TYPE = 6420;

    /**
     * 接货仓类型
     */
    public static final int JHC_SITE_TYPE = 6430;
    /**
     * 集配站编号
     */
    public static final Integer JI_PEI_CODE_9605 = 9605;
    /** 营业部 类型*/
    public static final Integer TERMINAL_SITE_TYPE_4 = 4;
//    后续增加城配的时候再使用，此次增加没有意义.
//    public static final Integer CHENGPEI_CODE_9606 = 9606;
    /**
     * B冷链转运中心站点类型
     */
    public static final Integer B2B_CODE_SITE_TYPE = 6460;
    /** 经济网网点类型 **/
    public static final Integer THIRD_ENET_SITE_TYPE= 10000;

    /**
     * 二级分拣中心
     */
    public static final Integer SITE_SUBTYPE_SECOND = 6409;

    /**
     * 三级分拣中心
      */
    public static final Integer SITE_SUBTYPE_THIRD = 6410;

    /**
     * 退货组
     */
    public static final Integer SITE_RETURN_GROUP = 12354;

    /**
     * 请求应用程序类型
     */
    public static final Integer PROGRAM_TYPE = 60;

    /**
     * 业务类型
     */
    public static final Integer BUSINESS_TYPE = 1005;

    /**
     * 业务操作类型
     */
    public static final Integer OPERATE_TYPE = 100503;

    /** 操作类型 **/
    public static final int TRANS_SORTING_OPERATE_TYPE = 1;
    public static final int WAREHOUSE_HANDOVER_OPERATE_TYPE = 2;
    public static final int POP_HANDOVER_OPERATE_TYPE = 3;
    public static final int PARTNER_INSPECTION_OPERATE_TYPE = 4;
    public static final int RETURN_SCHEDULING_OPERATE_TYPE = 5;
    public static final int PICKUP_OPERATE_TYPE = 6;
    public static final int SITE_RETURN_OPERATE_TYPE = 7;
    public static final int RETURN_PARTNER_OPERATE_TYPE = 8;
    public static final int OPERATE_TYPE_BDB = 9;
    public static final int OPERATE_TYPE_OEM = 10;
    public static final int OPERATE_TYPE_FC = 11;
    public static final int OPERATE_TYPE_RCD = 12;
    public static final int OPERATE_TYPE_SH = 26;//收货回传全程跟踪
    public static final int OPERATE_TYPE_PSY = 31;//配送员上门接货
    public static final int OPERATE_TYPE_In = 1150;//驻场
    /**
     * 业务操作日志系统编码-分拣web
     */
    public static final int BUSINESS_LOG_SOURCE_SYS_DMSWEB = 1;

    /**
     * 业务操作日志系统编码-分拣web中的ver
     */
    public static final int BUSINESS_LOG_SOURCE_SYS_VERINWEB = 2;

    /**
     * 业务操作日志系统编码-分拣worker
     */
    public static final int BUSINESS_LOG_SOURCE_SYS_DMSWORKER = 66;
    /**
     * 业务类型-未知业务类型
     */
    public static final Integer BUSINESS_TYPE_UNKNOWN = 0;

    /**
     * 操作日志业务编码-打印
     */
    public static final Integer BUSINESS_LOG_BIZ_TYPE_PRINT=2001;

    /**
     * 校验链调用biz
     */
    public static final Integer BUSINESS_LOG_BIZ_VER_FILTER_STATISTICS=400;

    /**
     * 操作日志记录
     */
    public static final Integer BUSINESS_LOG_BIZ_TYPE_OPERATE_LOG=2003;

    /**
     * 操作日志慢发货记录
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_SLOW_SEND=2003001;

    /**
     * 操作日志线上签记录
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_ONLINE_PRINT=2003002;

    /**
     * 操作日志业务编码-航空转陆运
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_ARABNORMAL=101401;

    /**
     * 统计校验链接调用operatetype
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_VER_FILTER_STATISTICS=101401;


    /**
     * 操作日志业务编码-航空转陆运
     */
    public static final Integer BUSINESS_LOG_BIZ_TYPE_ARABNORMAL=1014;
    /**
     * 操作日志业务编码-验货
     */
    public static final Integer BUSINESS_LOG_BIZ_TYPE_B_INSPECTION=500;
    /**
     * 操作日志业务编码-快运中心分拣补验货任务
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_INSPECTION=50010;
    /**
     * 操作日志业务编码-	客户端访问日志
     */
    public static final Integer BUSINESS_LOG_OPERATE_TYPE_CLIENTLOG=60010;
    /**
     * 操作日志业务编码-	客户端访问日志
     */
    public static final Integer BUSINESS_LOG_BIZ_TYPE_CLIENTLOG=600;
    /**
     * 业务类型-未知业务操作类型
     */
    public static final Integer OPERATE_TYPE_UNKNOWN = 0;
    /**
     * 业务类型-包裹标签打印
     */
    public static final Integer BUSINESS_TYPE_PACKAGE_PRINT = 1001;
    /**
     * 业务类型-称重
     */
    public static final Integer BUSINESS_TYPE_WEIGHT = 1002;
    /**
     * 业务类型-按运单称重
     */
    public static final Integer OPERATE_TYPE_WEIGHT_BY_WAYBILL = 100201;
    /**
     * 业务类型-分拣系统导出操作日志
     */
    public static final Integer BUSINESS_LOG_EXPORT_OPERATE = 5001;
    /**
     * 业务类型-分拣系统-导出操作日志
     */
    public static final Integer OPERATE_TYPE_REPORT_OPERATE = 500101;


    /** 大件包裹标识 **/
    public static final Short BOXING_TYPE = 2;
    /* 基础资料SiteType: 16为三方，4为自营，8为自提点 */
    public static final int BASE_SITE_SITE = 4;
    public static final int BASE_SITE_TYPE_ZT = 8;
    public static final Integer BASE_SITE_TYPE_THIRD = 16;
    public static final int BASE_SITE_TYPE_CAR_ZS = 32;// 汽车直送
    /* 基础资料siteType:64为分拣中心 */
    public static final Integer BASE_SITE_DISTRIBUTION_CENTER = 64;
    /* 基础资料siteType:96车队 */
    public static final Integer BASE_SITE_MOTORCADE = 96;
    public static final int BASE_SITE_SUBTYPE_JP = 9605;// 集配站
    public static final int BASE_SITE_TYPE_XN = 100;// 虚拟站点
    public static final int BASE_SITE_SUBTYPE_B2B2C = 10003;// B2B2C站点
    public static final int BASE_SITE_TYPE_KYZD = 101;// 快运终端
    public static final int BASE_SITE_TYPE_TCSP = 109;// 同城速配营业部
    /* 基础资料siteType:256为二级分拣中心 */
    public static final Integer BASE_SITE_DISTRIBUTION_SUBSIDIARY_CENTER = 256;
    /* 基础资料siteType:1024为B商家 */
    public static final int BASE_SITE_BUSSINESS=1024;

    public static final int OPERATE_TYPE_THIRD_INSPECTION = 80;

    /* 站点operateState：2为线下运营 */
    public static final Integer BASE_SITE_OPERATESTATE = 2;

    /**
     * 运营状态 operate_state 0关闭 1线上运营 2线下运营
     */
    public static final Integer BASE_SITE_OPERATESTATE_1 = 1;


    /**
     * 承运商类型 1:司机 0:承运商（三方快递，即站点表中类型为16的数据）
     */
    public static final Integer SENDUSERTYEP_DRIVER = 1;
    public static final Integer SENDUSERTYEP_CARRIER = 0;

    public static final String DEFAULT_OWN_SIGN_KEY = "ownSign";
    public static final String DEFAULT_OWN_SIGN_VALUE = "DMS";

    /**
     * 基础资料默认分拣中心所属类型
     */
    public static final Integer DMS_SITE_TYPE = 64;

    /**
     * 财务专用
     */
    public static final Integer FINANCIAL_SPECIAL_SITE_TYPE = 98;

    /**
     * POP订单类型
     */
    public static final Integer POP_FBP = 21;
    public static final Integer POP_SOP = 22;
    public static final Integer POP_LBP = 23;
    public static final Integer POP_SOPL = 25;
    public static final Integer ORDER_TYPE_B = 10000;

    public static final Integer POP_SOP_EMS_CODE = 999999999;
    public static final String POP_SOP_EMS_NAME = "EMS全国直发";


    /**
     * POP收货类型
     */
    public static final Integer POP_QUEUE_SUP = 1;
    public static final Integer POP_QUEUE_EXPRESS = 2;
    public static final Integer POP_QUEUE_DRIVER = 3;
    public static final Integer POP_QUEUE_SITE = 4;
    public static final Integer POP_QUEUE_PICKUP = 5;

    /**
	 * 打印包裹
	 */
	public static final Integer PRINT_PACK_TYPE = 1;
	/**
	 * 打印发票
	 */
	public static final Integer PRINT_INVOICE_TYPE = 2;

    /**
     * POP补全收货定义收货人
     */
    public static final String POP_RECEIVE_NAME = "分拣中心";

    /******************* 订单查询参数中的加载明细参数,可以多个 start ******************/
    /**
     * 只加载订单信息
     */
    public static final String KONG = "空";
    /**
     * 加载订单基本信息
     */
    public static final String JI_BEN_XIN_XI = "基本信息";
    /**
     * 加载订单状态信息
     */
    public static final String ZHUANG_TAI = "状态";
    /**
     * 加载订单顾客信息
     */
    public static final String GU_KE_XIN_XI = "顾客信息";
    /**
     * 加载订单金额信息
     */
    public static final String JIN_ER = "金额";
    /**
     * 加载订单支付信息
     */
    public static final String ZHI_FU = "支付";
    /**
     * 加载订单出库信息
     */
    public static final String CHU_KU = "出库";
    /**
     * 加载订单配送自提信息
     */
    public static final String PEI_SONG_ZI_TI = "配送自提";
    /**
     * 加载订单发票信息
     */
    public static final String FA_PIAO = "发票";
    /**
     * 加载订单拆分信息
     */
    public static final String CHAI_FEN = "拆分";
    /**
     * 加载订单备注信息
     */
    public static final String BEI_ZHU = "备注";
    /**
     * 加载订单全部信息(已废弃)
     */
    public static final String QUAN_BU = "全部";

    /**
     * 加载订单配送区域信息
     */
    public static final String POP = "POP";

    /**
     * 加载订单其他信息
     */
    public static final String OTHER = "其他";

    /******************* 订单查询参数中的加载明细参数,可以多个 end ******************/

    /******************* redis 相关   ******************/
    /**
     * 整体task mode key
     */
	public static final String TASK_MODE_KEY = "task.mode.key";

    /**
     * 本地缓存默认失效时间
     */
    public static final int POLLING_INTERVAL_TIME = 1500;// 秒

    /**
     * 时间：一分钟的秒数
     */
    public static final int TIME_SECONDS_ONE_MINUTE = 60;

    /**
     * 时间：一小时的秒数
     */
    public static final int TIME_SECONDS_ONE_HOUR = 3600;

    /**
     * 时间：48小时
     */
    public static final int FORTY_EIGHT_HOURS = 48;

    /**
     * 时间：72小时
     */
    public static final int SEVENTY_TWO_HOURS = 72;

    /**
     * 时间：一天的秒数
     */
    public static final int TIME_SECONDS_ONE_DAY = 86400;
    /**
     * 时间：一周的秒数
     */
    public static final int TIME_SECONDS_ONE_WEEK = 7*TIME_SECONDS_ONE_DAY;
    /**
     * 时间：15天的秒数
     */
    public static final int TIME_SECONDS_FIFTEEN_DAY = 15*TIME_SECONDS_ONE_DAY;
    /**
     * 时间：一月的秒数
     */
    public static final int TIME_SECONDS_ONE_MONTH = 30*TIME_SECONDS_ONE_DAY;
    /**
	 * 亚一站点
	 */
	public static final String ASION_NO_ONE_SITE_CODES_KEY = "ASION_NO_ONE_SITE_CODES";


    //region  Promise常量定义

    /**
     * Promsie 分拣中心类型
     */
    public static final Integer PROMISE_DISTRIBUTION_CENTER = 1;

    /**
     * Promsie 站点类型
     */
    public static final Integer PROMISE_SITE = 2;

    /**
     * Promsie B商家类型
     */
    public static final Integer PROMISE_DISTRIBUTION_B = 3;

    /**
     * 分拣中心环节请求
     */
    public static final String DISTRIBUTION_SOURCE = "11";

    /**
     * 纯外单约定订单号
     */
    public static final Integer ORDER_TYPE_B_ORDERNUMBER=0;

    /**
     * 省市县镇默认值
     */
    public static final Integer DEFALUT_PROVINCE_CITY_COUNTRY_TOWN_VALUE=0;

    //运力编码京东自营 -1
    public static final String JDZY = "-1";

    public static final String T_WAYBILL = "T"; // 正向物流发货
    //endregion

    // PDA登录信息
    public static final Integer PDA_USER_GETINFO_SUCCESS = 1; //获取信息成功
    public static final Integer PDA_USER_LOGIN_FAILUE = -1; //验证失败
    public static final Integer PDA_USER_GETINFO_FAILUE = 0; //获取基础资料数据失败
    public static final Integer PDA_USER_JSF_FAILUE = -3; //获取基础账号JSF数据失败
    public static final Integer PDA_USER_NO_EXIT = 2; //用户不存在
    public static final Integer PDA_USER_PASSWORD_WRONG = 6; //密码错误
    public static final Integer PDA_USER_LOCKED = 3; //密码错误超过十次pin被锁
    public static final Integer PDA_USER_BUSY = 100; //系统繁忙
    public static final Integer PDA_USER_NO_VERIFY = 7; //账号尚未审核通过
    public static final Integer PDA_USER_SECURITY_LOCKED = 8; //因安全原因账号被锁
    public static final Integer PDA_USER_LOGOUT = 14; //账号已经注销
    public static final Integer PDA_USER_SECURITY_CHECK = 1100; //需要验证(命中风险检查)
    public static final Integer PDA_USER_NO_USE = 1110; //无可用验证方式(命中风险检查)
    public static final Integer PDA_USER_IP_WRONG = 105; //ip在黑名单中
    public static final Integer PDA_USER_EMPTY = -4; //用户名或密码为空
    public static final Integer PDA_USER_ABNORMAL = -2; //登录异常

    public static final String PDA_USER_GETINFO_SUCCESS_MSG = "获取信息成功"; //成功信息
    public static final String PDA_USER_LOGIN_FAILUE_MSG = "验证失败"; //验证失败信息
    public static final String PDA_USER_GETINFO_FAILUE_MSG = "获取基础资料数据失败，请联系转运管理岗同事（质控）维护青龙基础资料角色"; //获取基础资料数据失败
    public static final String PDA_USER_JSF_FAILUE_MSG = "调取基础账号账号信息失败"; //获取基础账号JSF数据失败
    public static final String PDA_USER_NO_EXIT_MSG = "用户不存在"; //用户不存在
    public static final String PDA_USER_PASS_WORD_WRONG_MSG = "密码错误"; //密码错误
    public static final String PDA_USER_LOCKED_MSG = "密码错误超过十次pin被锁"; //密码错误超过十次pin被锁
    public static final String PDA_USER_BUSY_MSG = "系统繁忙"; //系统繁忙
    public static final String PDA_USER_NO_VERIFY_MSG = "账号尚未审核通过"; //账号尚未审核通过
    public static final String PDA_USER_SECURITY_LOCKED_MSG = "因安全原因账号被锁"; //因安全原因账号被锁
    public static final String PDA_USER_LOGOUT_MSG = "账号已经注销"; //账号已经注销
    public static final String PDA_USER_SECURITY_CHECK_MSG = "需要验证(命中风险检查)"; //需要验证(命中风险检查)
    public static final String PDA_USER_NO_USE_MSG = "无可用验证方式(命中风险检查)"; //无可用验证方式(命中风险检查)
    public static final String PDA_USER_IP_WRONG_MSG = "ip在黑名单中"; //ip在黑名单中
    public static final String PDA_USER_EMPTY_MSG = "用户名或密码为空"; //用户名或密码为空
    public static final String PDA_USER_ABNORMAL_MSG = "登录异常"; //登录异常
    public static final String PURE_MATCH_ERROR_MESSAGE = "该订单理赔未完成或物权不归京东!";//纯配逆向换单校验提示语

    public static final String PDA_THIRDPL_TYPE = "3pl_"; //小第三方
    public static final String PDA_BIG_THIRDPL_TYPE = "third_"; //大第三方（申通、圆通）
    public static final int PDA_THIRDPL_ID = 3000000;
    public static final int PDA_BIG_THIRDPL_ID = 6000000;
    public static final String BASIC_STAFF_COL = "staffId";	//员工标识

    // 一次邮件发送最大收件人数
    public static final int MAX_SEND_SIZE = 500;
    /**
     * Integer类型标识-false
     */
    public static final Integer INTEGER_FLG_FALSE = 0;
    /**
     * Integer类型标识-true
     */
    public static final Integer INTEGER_FLG_TRUE = 1;
    /**
     * String类型标识-1-true
     */
    public static final String STRING_FLG_TRUE = "1";
    /**
     * String类型标识-0-false
     */
    public static final String STRING_FLG_FALSE = "0";
    /**
     * 包裹称重流水-操作类型-分拣
     */
    public static final Integer PACK_OPE_FLOW_TYPE_SORTING = 1;
    /**
     * 包裹称重流水-操作类型-接货中心-配送员接货操作
     */
    public static final Integer PACK_OPE_FLOW_TYPE_PSY_REC = 2;
    /**
     * 包裹称重流水-操作类型-接货中心-驻场操作
     */
    public static final Integer PACK_OPE_FLOW_TYPE_ZC_REC = 3;
    /**
     * 包裹称重流水-操作类型-接货中心-车队操作
     */
    public static final Integer PACK_OPE_FLOW_TYPE_CD_REC = 5;
    /**
     * 包裹称重流水-操作类型-仓储操作
     */
    public static final Integer PACK_OPE_FLOW_TYPE_CC_REC = 4;

    /**
     * 是否启用称重量方：未启用称重未启用量方
     */
    public static final Integer WEIGHT_VOLUME_UNABLE = 0;

    /**
     * 是否启用称重量方：启用称重
     */
    public static final Integer WEIGHT_ENABLE = 1;

    /**
     * 是否启用称重量方：启用量方
     */
    public static final Integer VOLUME_ENABLE = 2;

    /**
     * 是否启用称重量方：启用量方和量方
     */
    public static final Integer WEIGHT_VOLUME_ENABLE = 3;

    /**
     * 面单特殊字符"航"
     * */
    public static final String SPECIAL_MARK_AIRTRANSPORT = "航";

    /**
     * Text-保价
     */
    public static final String TEXT_PRICE_PROTECT = "保价";
    /**
     * waybillSign-point-4-签单反还
     */
    public static final Integer WAYBILL_SIGN_POSITION_SIGN_BACK = 4;
    /**
     * waybillSign.point-10-配送业务类型
     */
    public static final Integer WAYBILL_SIGN_POSITION_DISTRIBUT_TYPE = 10;
    /**
     * waybillSign-point-31运输产品
     */
    public static final Integer WAYBILL_SIGN_POSITION_TRANSPORT_MODE = 31;

    /**
     * waybillSign-point-92 安心寄增值服务
     */
    public static final int WAYBILL_SIGN_POSITION_92 = 92;

    /**
     * waybillSign-point-92 安心寄增值服务 为2:追踪器
     */
    public static final char WAYBILL_SIGN_POSITION_92_2 = '2';


    /**
     * waybillSign-point-92 安心寄增值服务 为3:追踪箱
     */
    public static final char WAYBILL_SIGN_POSITION_92_3 = '3';

    /**
     * waybill_cancel 表 featureType ：30-病单，31-取消病单，32- 非病单
     */
    public static final Integer FEATURE_TYPCANCEE_UNSICKL = 32; // 非病单
    public static final Integer FEATURE_TYPCANCEE_SICKL = 30; // 病单

    public static final Integer FEATURE_TYPCANCEE_LP = 10; // 理赔完成拦截

    /**
     * Inspection 表 ，queue_no 字段长度 varchar(32) ，排队号
     */
    public static final Integer QUEUE_NO_LEGNTH = 32;

    //派车单号默认值
    public static final String SCHEDULE_CODE_DEFAULT = "-1";  //默认的scheduleCode值

    public static final Integer WAYBILL_DELIVERED_CODE = 150;  //订单妥投状态编码
    public static final Integer WAYBILL_REJECT_CODE = 160;  //订单拒收状态编码
    public static final Integer PACKAGE_REDELIVERY_CODE = 133;  //订单拒收状态编码
    /**
     * 字符类型yn标识,1-是
     */
    public static final char FLG_CHAR_YN_Y = '1';
    /**
     * 字符类型yn标识-0-否
     */
    public static final char FLG_CHAR_YN_N = '0';

    public static final int SEAL_SOURCE = 1;  //封车解封车操作源（代表我们DMS系统）
    /**
     * container_relation 发货状态 未发货
     */
    public static final Integer  CONTAINER_RELATION_SEND_STATUS_NO = 0;
    /**
     * container_relation 发货状态 已发货
     */
    public static final Integer  CONTAINER_RELATION_SEND_STATUS_YES = 1;
    /**
     * 商家别名标识-YHD：一号店
     */
    public static final String BUSINESS_ALIAS_YHD="YHD";
    /**
     * 商家别名标识-CMBC：招商银行
     */
    public static final String BUSINESS_ALIAS_CMBC="CMBC";
    /**
     * 商家商标图片-一号店（yhd4949.gif）
     */
    public static final String BRAND_IMAGE_KEY_YHD="yhd4949.gif";
    /**
     * 备件库条码前缀默认值-"null"
     */
    public static final String SPARE_CODE_PREFIX_DEFAULT="null";
    /**
     * 空铁提货操作
     */
    public static final int OPERATE_TYPE_AR_RECEIVE = 1810;

    /********************************************* UIM权限资源码相关start *********************************************/
    public static final String DMS_WEB_PERMISSION_CONTROL_R="DMS-WEB-PERMISSION-CONTROL-R"; //权限跳转
    public static final String DMS_WEB_PTORDER_DIFF_R="DMS-WEB-PTORDER-DIFF-R"; //平台差异处理
    public static final String DMS_WEB_PTORDER_QUEUE_R="DMS-WEB-PTORDER-QUEUE-R"; //平台排队号查询
    public static final String DMS_WEB_PTORDER_RECEIVE_R="DMS-WEB-PTORDER-RECEIVE-R"; //平台实收查询
    public static final String DMS_WEB_SORTING_RECEIVEWEIGHTCHECK_R="DMS-WEB-SORTING-RECEIVEWEIGHTCHECK-R"; //揽收重量校验统计
    public static final String DMS_WEB_SORTING_WEIGHTANDVOLUMECHECK_R="DMS-WEB-SORTING-WEIGHTANDVOLUMECHECK-R"; //重量体积抽验统计
    public static final String DMS_WEB_SORTING_SPOT_CHECK_REPORT_R="DMS-WEB-SORTING-SPOT-CHECK-REPORT-R"; //抽检报表
    public static final String DMS_WEB_SORTING_WEIGHTANDVOLUMECHECKOFB2B_R="DMS-WEB-SORTING-WEIGHTANDVOLUMECHECKOFB2B-R"; //人工抽检
    public static final String DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_R="DMS-WEB-SORTING-REVIEWWEIGHTSPOTCHECK-R"; //复重抽检任务导入
    public static final String DMS_WEB_SORTING_REVIEWWEIGHTSPOTCHECK_SPECIAL_R="DMS-WEB-SORTING-REVIEWWEIGHTSPOTCHECK-SPECIAL-R"; //复重抽检任务的导入功能（单独申请权限）
    public static final String DMS_WEB_UNLOAD_CAR_TASK_R="DMS_WEB_UNLOAD_CAR_TASK_R"; //卸车任务分配
    public static final String DMS_WEB_SORTING_INVENTORYTASK_R="DMS_WEB_SORTING_INVENTORYTASK_R"; //转运清场任务
    public static final String DMS_WEB_SORTING_INVENTORYEXCEPTION_R="DMS_WEB_SORTING_INVENTORYEXCEPTION_R"; //转运清场异常
    public static final String DMS_WEB_SORTING_CROSSBOX_R ="DMS-WEB-SORTING-CROSSBOX-R"; //跨箱号中转
    public static final String DMS_WEB_SORTING_GLOBALTRADE_R ="DMS-WEB-SORTING-GLOBALTRADE-R"; //全球购
    public static final String DMS_WEB_SORTING_GANTRY_R ="DMS-WEB-SORTING-GANTRY-R"; //龙门架注册
    public static final String DMS_WEB_SORTING_SORTSCHEME_R ="DMS-WEB-SORTING-SORTSCHEME-R"; //分拣计划配置
    public static final String DMS_WEB_SORTING_ADDRESSCHANGE_R ="DMS-WEB-SORTING-ADDRESSCHANGE-R"; //客户改址查询
    public static final String DMS_WEB_SORTING_PARTNERINSPECTION_R ="DMS-WEB-SORTING-PARTNERINSPECTION-R"; //第三方差异处理
    public static final String DMS_WEB_SORTING_OPERATELOG_R ="DMS-WEB-SORTING-OPERATELOG-R"; //分拣中心操作日志（新）
    public static final String DMS_WEB_SORTING_GODDESS_R ="DMS-WEB-SORTING-GODDESS-R"; //分拣运行时全程跟踪
    public static final String DMS_WEB_SORTING_OFFLINELOG_R ="DMS-WEB-SORTING-OFFLINELOG-R"; //分拣中心离线操作日志
    public static final String DMS_WEB_SORTING_FRESH_R ="DMS-WEB-SORTING-FRESH-R"; //生鲜温度录入
    public static final String DMS_WEB_SORTING_GANTRYAUTOSEND_R ="DMS-WEB-SORTING-GANTRYAUTOSEND-R"; //龙门架自动发货
    public static final String DMS_WEB_SORTING_SORTMACHINEAUTOSEND_R ="DMS-WEB-SORTING-SORTMACHINEAUTOSEND-R"; //分拣机自动发货
    public static final String DMS_WEB_SORTING_ECLPREFUSED_R="DMS-WEB-SORTING-ECLPREFUSED-R";//库房拒收外呼申请
    public static final String DMS_WEB_SORTING_UNKNOWNWAYBILL_R="DMS-WEB-SORTING-UNKNOWNWAYBILL-R";//三无外单托寄物核实
    public static final String DMS_WEB_SORTING_ABNORMALDISPOSE_R ="DMS-WEB-SORTING-ABNORMALDISPOSE-R"; //批次清零
    public static final String DMS_WEB_SORTING_DMSBARCODE_R="DMS-WEB-SORTING-DMSBARCODE-R";//69码查询商品名称
    public static final String DMS_WEB_SORTING_TMSPROXY_R="DMS-WEB-SORTING-TMSPROXY-R";//运输委托书打印
    public static final String DMS_WEB_SORTING_GOODSPRINT_R="DMS_WEB_SORTING_GOODSPRINT_R";//托寄物品名打印
    public static final String DMS_WEB_SORTING_REVERSEPARTDETAIL_CHECK_R="DMS-WEB-SORTING-REVERSEPARTDETAIL-CHECK-R";//半退明细查询
    public static final String DMS_WEB_TOOL_BUSIWEIGHTANDVOLUMEWHITELIST_R="DMS-WEB-TOOL-MERCHANTWEIGHTANDVOLUMEWHITELIST-R";//商家称重量方白名单
    public static final String DMS_WEB_EXPRESS_QUARANTINELICENSE_R="DMS-WEB-EXPRESS-QUARANTINELICENSE-R";//冷链卡班检疫证票号管理
    public static final String DMS_WEB_EXPRESS_B2BROUTER_R="DMS-WEB-EXPRESS-B2BROUTER-R";//B网路由配置表
    public static final String DMS_WEB_EXPRESS_DMSSTORAGEAREA_R="DMS-WEB-EXPRESS-DMSSTORAGEAREA-R";//流向库位配置表
    public static final String DMS_WEB_EXPRESS_PACKAGEHALF_R="DMS-WEB-EXPRESS-PACKAGEHALF-R";//快运协商再投
    public static final String DMS_WEB_EXPRESS_PACKAGEHALFREDELIVERY_R="DMS-WEB-EXPRESS-PACKAGEHALFREDELIVERY-R";//包裹部分签收操作
    public static final String DMS_WEB_EXPRESS_RMAHANDOVER_R="DMS-WEB-EXPRESS-RMAHANDOVER-R";//RMA交接单打印
    public static final String DMS_WEB_EXPRESS_WAYBILLCONSUMABLERECORD_R="DMS-WEB-EXPRESS-WAYBILLCONSUMABLERECORD-R";//B网包装耗材
    public static final String DMS_WEB_EXPRESS_PACKINGCONSUMABLEINFO_R="DMS-WEB-EXPRESS-PACKINGCONSUMABLEINFO-R";//包装耗材基础信息管理
    public static final String DMS_WEB_EXPRESS_DMSCONSUMABLERELATION_R="DMS-WEB-EXPRESS-DMSCONSUMABLERELATION-R";//快运中心包装耗材管理
    public static final String DMS_WEB_EXPRESS_STORAGEPACKAGEM_R="DMS-WEB-EXPRESS-STORAGEPACKAGEM-R";//暂存管理
    public static final String DMS_WEB_EXPRESS_PERFORMANCE_R="DMS-WEB-EXPRESS-PERFORMANCE-R";//加履交接单打印
    public static final String DMS_WEB_TOOL_WAYBILLCODECHECK_R ="DMS-WEB-TOOL-WAYBILLCODECHECK-R"; //金融客户运单号对比校验
    public static final String DMS_WEB_TOOL_SIGNRETURN_R ="DMS-WEB-TOOL-SIGNRETURN-R"; //签单返回合单打印交接单
    public static final String DMS_WEB_TOOL_REPAILSTOCK_R ="DMS-WEB-TOOL-REPAILSTOCK-R"; //逆向物流重新推送库管
    public static final String DMS_WEB_TOOL_REPAILREVERSE_R ="DMS-WEB-TOOL-REPAILREVERSE-R"; //逆向物流重新推送数据
    public static final String DMS_WEB_TOOL_WAYBILLCHECK_R ="DMS-WEB-TOOL-WAYBILLCHECK-R"; //运单判断
    public static final String DMS_WEB_TOOL_TURBOX_R ="DMS-WEB-TOOL-TURBOX-R"; //收发空箱查询
    public static final String DMS_WEB_TOOL_WAYBILLSEND_R ="DMS-WEB-TOOL-WAYBILLSEND-R"; //全国邮政数据重新下发
    public static final String DMS_WEB_TOOL_DAFU_R ="DMS-WEB-TOOL-DAFU-R"; //大福线白名单管理
    public static final String DMS_WEB_TOOL_REVERSERECEIVE_R ="DMS-WEB-TOOL-REVERSERECEIVE-R"; //逆向收货查询
    public static final String DMS_WEB_TOOL_SORTINGCENTER_R ="DMS-WEB-TOOL-SORTINGCENTER-R"; //分拣中心测试查询工具
    public static final String DMS_WEB_TOOL_AREADESTPLAN_R ="DMS-WEB-TOOL-AREADESTPLAN-R"; //龙门架发货关系维护
    public static final String DMS_WEB_TOOL_B2BWEIGHT_R ="DMS-WEB-TOOL-B2BWEIGHT-R"; //快运运单称重
    public static final String DMS_WEB_TOOL_DMSOPERATEHINT_R="DMS-WEB-TOOL-DMSOPERATEHINT-R";//PDA验货提示语
    public static final String DMS_WEB_TOOL_BOXLMIT_R="DMS-WEB-TOOL-BOXLMIT-R";//PDA建箱包裹数上限限制
    public static final String DMS_WEB_TOOL_INTERCEPT_CONFIG_R="DMS-WEB-TOOL-FUNC-SWITCH-CONFIG-R";//分拣功能开关配置
    public static final String DMS_WEB_TOOL_RECYCLEMATERIAL_R="DMS-WEB-TOOL-RECYCLEMATERIAL-R";//循环物资管理
    public static final String DMS_WEB_ISV_CONTROL_R ="DMS-WEB-ISV-CONTROL-R"; //ISV版本控制
    public static final String DMS_WEB_ISV_MANAGE_R ="DMS-WEB-ISV-MANAGE-R"; //ISV版本管理
    public static final String DMS_WEB_PRE_SEALVEHICLE_R ="DMS_WEB_PRE_SEALVEHICLE_R"; //预封车权限码
    public static final String DMS_WEB_OFFLINE_MANAGER_R ="bluedragon_offlinePwd_list"; //离线密码管理
    public static final String DMS_WEB_QUERY_KUGUANINIT ="DMS-WEB-QUERY-KUGUANINIT"; //库管首页
    public static final String DMS_WEB_QUERY_KUGUANLIST ="DMS-WEB-QUERY-KUGUANLIST"; //库管查询
    public static final String DMS_WEB_TRANSPORT_ARBOOKINGSPACE_R="DMS-WEB-TRANSPORT-ARBOOKINGSPACE-R"; //空铁订舱登记
    public static final String DMS_WEB_TRANSPORT_AREXCPREGISTER_R="DMS-WEB-TRANSPORT-AREXCPREGISTER-R"; //空铁异常登记
    public static final String DMS_WEB_ABNORMAL_ORDER_R="DMS-WEB-ABNORMAL-ORDER-R"; //异常单操作
    public static final String DMS_WEB_CROSS_SORTING_R="DMS-WEB-CROSS-SORTING-R";   //跨区分拣校验GlobalTrade
    public static final String DMS_WEB_POP_ABNORMAL_R="DMS-WEB-POP-ABNORMAL-R";  //POP差异订单
    public static final String DMS_WEB_SORTING_MACHINE_EXCEPTION="DMS-WEB-SORTING-MACHINE-EXCEPTION"; //分拣机或者龙门架异常
    public static final String DMS_WEB_NOTICE_MANAGE="DMS-WEB-NOTICE-MANAGE"; //通知栏管理
    public static final String DMS_WEB_INDEX_R="DMS_WEB_INDEX_R";  //系统主页
    public static final String DMS_WEB_COLLECT_SET="DMS_WEB_COLLECT_SET";  //集货配置
    public static final String DMS_WEB_COLLECT_REPORT="DMS_WEB_COLLECT_REPORT";  //集货报表
    public static final String DMS_WEB_RECYCLE_MATERIAL_SCAN_R = "DMS_WEB_RECYCLE_MATERIAL_SCAN_R"; //循环物资扫描查询
    public static final String DMS_WEB_EDN_PICKING_R = "DMS_WEB_EDN_PICKING_R"; //企配仓揽收
    public static final String DMS_BUSINESS_RETURN_ADRESS_R = "DMS_BUSINESS_RETURN_ADRESS_R"; //商家二次换单地址查询
    /********************************************* 研发UIM权限资源码相关start ***********************************************/
    public static final String DMS_WEB_DEVELOP_REDIS_R ="DMS_WEB_DEVELOP_REDIS_R"; //分拣缓存查询
    public static final String DMS_WEB_DEVELOP_TASK_R ="DMS_WEB_DEVELOP_TASK_R"; //分拣任务监控
    public static final String DMS_WEB_DEVELOP_DBTASK_R ="DMS_WEB_DEVELOP_DBTASK_R"; //分拣数据库任务查询
    public static final String DMS_WEB_DEVELOP_METHOD_INVOKE_R ="DMS_WEB_DEVELOP_METHOD_INVOKE_R"; //线上方法验证
    public static final String DMS_WEB_DEVELOP_SQLKIT_R ="DMS_WEB_DEVELOP_SQLKIT_R"; //分拣SQLKit
    public static final String DMS_WEB_DEVELOP_SYSCONFIG_R ="DMS_WEB_DEVELOP_SYSCONFIG_R"; //分拣规则管理
    public static final String DMS_WEB_DEVELOP_TOOLS_SUMMARY_R ="DMS_WEB_DEVELOP_TOOLS_SUMMARY_R"; //分拣工具管理
    public static final String DMS_WEB_DEVELOP_SYSTEMLOG_R ="DMS_WEB_DEVELOP_SYSTEMLOG_R"; //分拣操作日志
    public static final String DMS_WEB_DEVELOP_MEMORY_CACHE_R ="DMS_WEB_DEVELOP_MEMORY_CACHE_R"; //分拣内存缓存查询
    public static final String DMS_WEB_DEVELOP_WORKER_SETTING_R ="DMS_WEB_DEVELOP_WORKER_SETTING_R"; //分拣任务配置
    public static final String DMS_WEB_DEVELOP_OPERATE_R ="DMS_WEB_DEVELOP_OPERATE_R"; //分拣实操查询
    public static final String DMS_WEB_DEVELOP_DICT_R ="DMS_WEB_DEVELOP_DICT_R"; //分拣数据字典
    public static final String DMS_WEB_DEVELOP_RULE_CONFIG_R ="DMS_WEB_DEVELOP_RULE_CONFIG_R"; //规则管理
    public static final String ENTERPRISE_DISTRIBUTION_R ="ENTERPRISE_DISTRIBUTION_R"; //企配质检界面


    /********************************************* 研发UIM权限资源码相关end ***********************************************/
    public static final String DMS_WEB_COMMON_R ="DMS_WEB_COMMON_R"; //通用权限

    /********************************************* UIM权限资源码相关end ***********************************************/

    /******************************************** 封车类型相关start *****************************************/
    public static final Integer SEAL_TYPE_TRANSPORT = 10;    //按运力封车
    public static final Integer SEAL_TYPE_TASK = 20;   //按任务封车
    /******************************************** 封车类型相关end *****************************************/
    /**
     * 第三方配送站点-16
     */
    public static final Integer THIRD_SITE_TYPE = 16;
    /**
     * 第三方配送站点-二级类型-16
     */
    public static final Integer THIRD_SITE_SUB_TYPE = 16;
    /**
     * 第三方配送站点-三级类型-邮政落地配-16001
     */
    public static final Integer THIRD_SITE_THIRD_TYPE_SMS = 16001;
    /**
     * Double值-0
     */
    public static final Double DOUBLE_ZERO = 0.0;

    public static final Double DOUBLE_ONE = 1.0;

    /**
     * Long值-0
     */
    public static final Long LONG_ZERO = 0L;

    /**
     * Long值-1
     */
    public static final Long LONG_ONE = 1L;

    /**
     * 配置信息-北京的分拣中心
     */
    public static final String SYS_CONFIG_NAME_BJ_DMS_SITE_CODES = "bjDmsSiteCodes";
    /**
     * 配置信息-客户端检查配置sys.config.client.check
     */
    public static final String SYS_CONFIG_LOGIN_CHECK = "sys.config.login.check";
    /**
     * 配置信息-终端站点客户端菜单配置黑名单
     */
    public static final String SYS_CONFIG_CLIENT_SMSSITEMENUBLACKLIST = "sys.config.client.smsSiteMenuBlacklist";
    /**
     * 配置信息-自动签退处理的站点配置
     */
    public static final String SYS_CONFIG_AUTOHANDLESIGNOUTSITECODES = "sys.config.autoHandleSignOutSiteCodes";
    /**
     *  配置信息-青龙打印配置  qinglong.print.type.use
     *
     */
    public static final String SYS_WAYBILL_PRINT_ADDIOWN_NUMBER_CONF = "waybill.print.addiOwnNumberConf";


    /**
     *  配置信息-切换百川流量场地列表
     *
     */
    public static final String BAICHUAN_REVERSE_SITE_CONF = "baichuan.reverse.site.conf";


    /**
     *发货统一服务切换
     */
    public static final String SEND_CAPABILITY_SITE_CONF = "send.capability.site.conf";

    /**
     *  配置信息-切换百川流量,强制走老逻辑运单列表
     */
    public static final String BAICHUAN_REVERSE_WAYBILL_OLD_L_CONF = "baichuan.reverse.waybill.old.logic.conf";

    /**
     *  配置信息-切换百川流量,强制走老逻辑退货类型列表
     */
    public static final String BAICHUAN_REVERSE_TYPE_CONF = "baichuan.reverse.type.old.logic.conf";

    /**
     * 配置信息-拼多多不允许获取接口的打印类型 pdd.print.type.not.use;
     */
    public static final String SYS_CONFIG_PDD_PRINT_TYPE_NOT_USE = "pdd.print.type.not.use";

    /**
     * 配置信息-生成分拣中心退货任务的异常原因id列表 abnormal.reason.id.generate.sorting_return_task;
     */
    public static final String SYS_CONFIG_ABNORMAL_REASON_ID_GENERATE_SORTING_RETURN_TASK = "abnormal.reason.id.generate.sorting_return_task";

    /**
	  * 配置信息-客户端运行环境配置前缀 sys.config.client.runningMode.
	  */
    public static final String SYS_CONFIG_CLIENT_RUNNING_MODE_PRE = "sys.config.client.runningMode.";

    public static final String SYS_CONFIG_CROUTER_OPEN_DMS_CODES= "crouter.verify.allowed";

    /**
     * 解密虚拟手机号最长等待 时间天
     */
    public static final String SYS_CONFIG_ZJ_DECODE_MOBILE_VIRTUAL_AFTER_DAYS= "decode.mobile.virtual.after.days";
    /**
     * print titles
     */
    public static final  String  PRINT_TITLES="【原退】原运单号:";
    public static final  String  PRINT_JD_TITLES="【原退】原运单号:联系京东项目组获取运单号”。";

    /**
     * 揽收终止原因配置
     */
    public static final String SYS_CONFIG_COLLECT_TERMINATE_REASON= "collect_terminate_reason";

    /**
     * 安卓菜单可用性配置
     */
    public static final String SYS_CONFIG_ANDROID_MENU_USAGE= "android_menu_usage_";

    /**
     * 安卓菜单可用性配置，按场地id配置
     */
    public static final String SYS_CONFIG_ANDROID_MENU_USAGE_BY_SITE_CODE = "android_menu_usage_by_site_code_";

    /**
     * 全局功能管控配置，按系统
     */
    public static final String SYS_CONFIG_GLOBAL_FUNC_USAGE_CONTROL= "global_func_usage_control_";

    /**
     * 功能可用性配置黑名单
     */
    public static final String SYS_CONFIG_FUNC_USAGE= "func_usage_";

    /**
     * 隐藏面单手机号信息
     */
    public static final String SYS_CONFIG_HIDE_PHONE_6Char= "hide_phone_6char";

    /**
     * 功能可用性配置黑名单，按人员ERP配置
     */
    public static final String SYS_CONFIG_FUNC_USAGE_BY_ERP = "func_usage_by_erp_";

    /**
     * 功能可用性配置黑名单，按场地id配置
     */
    public static final String SYS_CONFIG_FUNC_USAGE_BY_SITE_CODE = "func_usage_by_site_code_";
    /**
     * 功能可用性配置白名单，按场地类型
     */
    public static final String SYS_CONFIG_FUNC_USAGE_WHITE_BY_SITE_TYPE = "func_usage_white_by_site_type_";
    /**
     * 功能可用性配置白名单，按场地名单
     */
    public static final String SYS_CONFIG_FUNC_USAGE_WHITE_BY_SITE_CODE = "func_usage_white_by_site_code_";
    /**
     * 功能可用性配置白名单，按人员erp列表
     */
    public static final String SYS_CONFIG_FUNC_USAGE_WHITE_BY_ERP = "func_usage_white_by_erp_";
    /**
     * 是否启用中台创建箱号开关key
     */
    public static final String CREATE_BOX_FROM_SSC_SWITCH = "CREATE_BOX_FROM_SSC_SWITCH";

    /**
     * 是否启用中台创建箱号开关key
     */
    public static final String FIND_BOX_FROM_SSC_SWITCH = "FIND_BOX_FROM_SSC_SWITCH";

    /**
     * 启用中台创建箱号的场地集合 key
     */
    public static final String CREATE_BOX_FROM_SSC_SITE = "CREATE_BOX_FROM_SSC_SITE";

    /**
     * 启用分拣创建箱号的场地集合 key
     */
    public static final String CREATE_BOX_FROM_DMS_SITE = "CREATE_BOX_FROM_DMS_SITE";

    /**
     * 配置信息-一车一单发货 自动取消组板功能开启的分拣中心
     */
    public static final String SYS_CONFIG_BOARD_COM_CANCEL_ATUO_OPEN_DMS_CODES="packageSend.board.com.cancel.auto.sites";
    /**
     * 配置信息-任务线上化任务-推送-功能开启的分拣中心
     */
    public static final String SYS_CONFIG_WORK_GRID_MANAGER_SITES="sys.config.task.workGridManager.sites";
    /**
     * 配置信息-新分拣开启的分拣中心
     */
    public static final String SYS_CONFIG_NEW_SORTING_OPEN_DMS_CODES="sorting.new.sites";


    /**
     * 配置信息-调用运单的分页获取数据包裹开关接口（为了支持2w个订单）
     */
    public static final String SYS_CONFIG_PACKAGE_PAGE_SWITCH= "package.page.switch";
    /**
     * 配置信息-分拣机自动发货 封车自动换批次功能开关（config_content中维护的是开启自动换批次的分拣中心所属的分拣机编码）
     */
    public static final String SYS_CONFIG_SORT_MACHINE_AUTO_CHANGE_SEND_CODE = "sortMachine.autoSend.sendCode.auto.change";

    /**
     * 三无寄托物核实 上报次数上限
     */
    public static final String SYS_ABNORMAL_UNKNOWN_REPORT_TIMES= "abnormal.unknown.report.times";
    /**
     * 三无寄托物核实 上报运单数量限制
     */
    public static final String SYS_ABNORMAL_UNKNOWN_REPORT_WAYBILL_MAX= "abnormal.unknown.report.waybill.max";

    /**
     * 配置信息-冷链卸货任务查询天数-冷链操作卸货出入库业务
     */
    public static final String SYS_CONFIG_COLD_CHAIN_UNLOAD_QUERY_DAYS = "cold.chain.operation.query.days";

    /**
     * 封车批次号缓存前缀
     */
    public static final String CACHE_KEY_PRE_SEAL_SENDCODE ="CACHE_SEAL_SENDCODE-";

    /**
     * PDA 提示语缓存前缀
     */
    public static final String CACHE_KEY_PRE_PDA_HINT ="CACHE_PDA_HINT-";
    /**
     * 字典名称-WaybillSign打标配置标识
     */
    public static final String DIC_NAME_WAYBILL_SIGN_CONFIG = "WaybillSign";
    /**
     * 字典名称-SendPay打标配置标识
     */
    public static final String DIC_NAME_SEND_PAY_CONFIG = "SendPay";
    /**
     * 字典名称-包裹标签打印字典配置标识
     */
    public static final String DIC_NAME_PACKAGE_PRINT_DIC_CONFIG = "PackagePrintDic";
    /**
     * 字典名称-包裹标签打印字典-Product
     */
    public static final Integer DIC_CODE_PACKAGE_PRINT_PRODUCT = 1010201;
    /**
     * 发货明细MQ source类型  ar代表空铁  dms默认正常
     */
    public static final String SEND_DETAIL_SOUCRE_AR = "AR";
    public static final String SEND_DETAIL_SOUCRE_NORMAL= "DMS";

    /**
     * 查询B网应履约时效接口相关
     * com.jd.etms.vrs.ws.VrsBNetQueryApi.queryPerformanceTime()
     */
    public static final Integer ROUTE_INTER_CONFIG_TYPE_QUAN_LIUCHENG_LVYUELV = 1;
    public static final Integer ROUTE_INTER_BIZZ_TYPE_ZHENG_CHE_B2B= 4;
    public static final Integer ROUTE_INTER_BIZZ_TYPE_CHUN_WAI_B2B= 5;
    public static final Integer ROUTE_INTER_BIZZ_TYPE_CANG_PEI_B2B= 6;

    /**
     * UMP监控应用名-dms.etms
     */
    public static final String UMP_APP_NAME_DMSWEB= "dms-etms";
    /**
     * UMP监控应用名-bluedragon-distribution-worker
     */
    public static final String UMP_APP_NAME_DMSWORKER= "dms-etms-worker";

    /**
     *  重泡比超过正常范围168:1到330:1
     */
    public static final double CBM_DIV_KG_MIN_LIMIT = 168.0;
    public static final double CBM_DIV_KG_MAX_LIMIT = 330.0;
    public static final String CBM_DIV_KG_MESSAGE = "重泡比超过正常范围168:1到330:1";
    public static final Integer CBM_DIV_KG_CODE = 10001;

    /**
     * 一级分拣中心名称后缀
     */
    public static final String SUFFIX_DMS_ONE="分拣中心";
    /**
     * 二级分拣中心名称后缀
     */
    public static final String SUFFIX_DMS_TWO="分拨中心";
    /**
     * 中转场名称后缀
     */
    public static final String SUFFIX_TRANSIT="中转场";

    /**
     * 分拣系统标识-DMS，调用外部系统接口需要传递
     */
    public static final String SYSTEM_CODE_OWON= "DMS";

    /**
     * 龙门架校验调整天数
     */
    public static final Integer GANTRY_CHECK_DAYS= -30;

    /**
     * 原包发货推迟时间5秒
     */
    public static final int DELIVERY_DELAY_TIME = 5000;

    /**
     * 组板即发货 延迟1s
     */
    public static final int COMBOARD_SEND_DELAY_TIME = 1000;

    /**
     * 始发道口号类型-1-普通
     */
    public static final Integer ORIGINAL_CROSS_TYPE_GENERAL= 1;
    /**
     * 始发道口号类型-2-航空
     */
    public static final Integer ORIGINAL_CROSS_TYPE_AIR= 2;
    /**
     * 始发道口号类型-3-填仓
     */
    public static final Integer ORIGINAL_CROSS_TYPE_FILL= 3;

    /**
     * 快运中心支持包装耗材状态标识
     */
    public static final Integer DMS_SUPPORT_PACKING_STATUS = 1;

    /**
     * 快运中心不支持包装耗材状态标识
     */
    public static final Integer DMS_NOT_SUPPORT_PACKING_STATUS = 0;

    /**
     * 快运中心启用包装耗材状态标识
     */
    public static final Integer DMS_ENABLE_PACKING_STATUS = 1;

    /**
     * 快运中心停用包装耗材状态标识
     */
    public static final Integer DMS_DISABLE_PACKING_STATUS = 0;

    /**
     * 包装耗材编号前缀
     */
    public static final String PACKING_PRE_CODE = "HC";

    /**
     * 包装耗材编号占位符
     */
    public static final String PACKING_PLACEHOLDER = "%03d";


    /**
     * 应付动态量方条码barcode类型 扫描到的号码类型1：包裹号 2：箱号 3：板号
     */
    public static final int DMS_OUT_MEASURE_BARCODE_TYPE_PACKAGECODE = 1;
    public static final int DMS_OUT_MEASURE_BARCODE_TYPE_BOXCODE = 2;
    public static final int DMS_OUT_MEASURE_BARCODE_TYPE_BOARDCODE= 3;

    /**
     * 再投审核完成类型（1：按运单审核；2：按包裹审核）
     */
    public static final Integer PACKAGE_APPROVE_TYPE = 2;

    /**
     * 已发货状态（托寄物品名打印）
     */
    public static final Integer GOODS_PRINT_WAYBILL_STATUS_1 =1;
    /**
     * 取消发货状态（托寄物品名打印）
     */
    public static final Integer GOODS_PRINT_WAYBILL_STATUS_0 =0;

    /**
     * 全程跟踪状态 换单完成
     */
    public static final String WAYBILL_TRACE_STATE_EXCHANGE = "-280";

    /**
     * 全程跟踪状态 包裹补打
     */
    public static final String WAYBILL_TRACE_STATE_RE_PRINT = "-220";

    /**
     * 全程跟踪状态  揽收完成
     */
    public static final String WAYBILL_TRACE_STATE_COLLECT_COMPLETE ="-640";

    /**
     * 全程跟踪状态 站点发货
     * */
    public static final String WAYBILL_TRACE_STATE_SEND_BY_SITE = "200";

    /**
     * 全程跟踪状态 发货
     * */
    public static final String WAYBILL_TRACE_STATE_SEND = "16";

    /**
     * 全程跟踪状态 封车
     * */
    public static final String WAYBILL_TRACE_STATE_SEAL_CAR = "-450";

    /**
     * 分拣中心收货
     */
    public static final String WAYBILL_TRACE_STATE_RECEIVE_WAYBILL = "-170";

    /**
     * 全程跟踪状态 分拣
     * */
    public static final String WAYBILL_TRACE_STATE_SORTING = "13";
    /**
     * 全程跟踪状态 分拣验货
     * */
    public static final String WAYBILL_TRACE_STATE_INSPECTION_BY_CENTER = "10";

    /**
     * 全程跟踪状态 退货装箱
     */
    public static final String WAYBILL_TRACE_STATE_BOXING_BY_SITE = "180";

    /**
     * 全程跟踪状态  便民店、自寄柜揽收交接成功
     */
    public static final String WAYBILL_TRACE_STATE_BMZT_COLLECT_HANDOVER_COMPLETE ="-1300";

    /**
     * 运单状态  拒收
     */
    public static final String WAYBILL_TRACE_STATE_REJECTED = "160";

    /**
     * 运单状态  妥投
     */
    public static final String WAYBILLTRACE_FINISHED = "150";

    /**
     * 运单状态  弃件
     */
    public static final String WAYBILLTRACE_WASTE = "620";
    /**
     * 运单状态  弃件（港澳单节点）
     */
    public static final String WAYBILLTRACE_WASTE_GA = "750";

    /**
     * 运单状态  异常退回
     */
    public static final String WAYBILLTRACE_EX_RETURN = "-3040";

    /**
     * 运单状态  解封车
     */
    public static final String WAYBILLTRACE_UN_SEAL_CAR = "-460";
    /**
     * 运单状态  清关失败
     */
    public static final String WAYBILLTRACE_FAIL_QG = "700";

    /** 系统编码 **/
    public static final String SYSTEM_CODE_WEB="DMS_WEB";

    /** 快运发货路由校验旧接口标识 **/
    public static final Integer DELIVERY_ROUTER_VERIFICATION_OLD = 0;

    /** 快运发货路由校验新接口标识 **/
    public static final Integer DELIVERY_ROUTER_VERIFICATION_NEW = 1;

    public static final double CONFIRM_WEIGHT_25 = 25.00;

    public static final double CONFIRM_WEIGHT_100 = 100.00;

    public static final double ILLEGAL_WEIGHT_1000 = 1000.00;

    /**
     * 计量单位：kg
     */
    public static final String MEASURE_UNIT_NAME_KG = "kg";

    /**
     * 计量单位：cm
     */
    public static final String MEASURE_UNIT_NAME_CM = "cm";

    /**
     * 数据库箱号最大长度限制，由于无法保证与数据库同步更新，此常量不属于权威定义
     */
    public static final int BOX_CODE_DB_COLUMN_LENGTH_LIMIT = 50;
    /**
     * 数据库车牌长度最大长度限制，由于无法保证与数据库同步更新，此常量不属于权威定义
     */
    public static final int CAR_CODE_DB_COLUMN_LENGTH_LIMIT = 32;

    /**
     * EMG条码前缀
     */
    public static final String EMG_CODE_PREFIX = "EMG";
    /**
     * 系统标识dms
     */
    public static final String SYS_CODE_DMS = "dms";

    /**
     * 字典的产品类型父节点
     */
    public static final Integer PRODUCT_PARENT_ID = 30048;

    /**
     * 字典的面单举报类型父节点
     */
    public static final Integer EXPRESS_BILL_REPORT_PARENT_ID = 601;

    /**
     * 字典的面单举报类型：一级原因父节点
     */
    public static final Integer FIRST_FACE_ABNORMAL_REPORT_PARENT_ID = 701;
    /**
     * 字典的面单举报类型：二级原因父节点
     */
    public static final Integer SECOND_FACE_ABNORMAL_REPORT_PARENT_ID = 701;

    /**
     * 货物类型
     */
    public static final int BASEDICT_GOODS_TYPE_TYPECODE = 10203;

    /**
     * 箱号类型校验开关key
     */
    public static final String BOX_TYPE_CHECK_SWITCH = "BOX_TYPE_CHECK_SWITCH";

    /**
     * 调用运输基础资料获取货物类型 父节点编码
     */
    public static final String PARENTCODE = "3";
    /**
     * 调用运输基础资料获取货物类型 字典节点级别
     */
    public static final int DICTLEVEL = 3;
    /**
     * 调用运输基础资料获取货物类型 字典分组
     */
    public static final String DICTGROUP = "1209";
    /**
     * 运单预分拣网点值 为-136代表超区
     */
    public static final int WAYBILL_SITE_ID_OUT_ZONE = -136;

    /**
     * 七大区区域ID：
     *  华东:3 西南:4 华北:6 华南:10
     *  华中:600 东北:611 西北:645
     * */
    public static final int EAST_CHINA_ORG_ID = 3;
    public static final int SOUTH_WEST_ORG_ID = 4;
    public static final int NORTH_CHINA_ORG_ID = 6;
    public static final int SOUTH_CHINA_ORG_ID = 10;
    public static final int CENTRAL_CHINA_ORG_ID = 600;
    public static final int NORTH_EAST_ORG_ID = 611;
    public static final int NORTH_WEST_ORG_ID = 645;

    /**
     * 冷链卡班短信扩展字段extension
     */
    public static final String DMS_COLD_CHAIN_SEND = "dms_coldChain_send";

    /**
     * 经济网租户编码
     */
    public static final String TENANT_CODE_ECONOMIC = "ECONOMIC_NET";


    /**
     *所属业务操作点击量
     *
     */
    public static final Integer BIZTYPE_URL_CLICK = 999001;

    public static final Integer BIZTYPE_URL_INTERCEPT = 999002;
    /**
     *
     * 菜单点击 分拣中心操作日志
     */
    public static final Integer OPERATELOG_CLICK = 1;
    /**
     * 菜单点击 分拣运行时全程跟踪
     */
    public static final Integer GODDESSLOG_CLICK = 2;

    /**
     * 菜单点击 分拣离线操作日志查询
     */
    public static final Integer OFFLINELOG_CLICK = 3;
    /**
     * 新日志查询
     */
    public static final Integer NEW_LOG = 4;

    /**
     * 负号
     */
    public final static String NEGATIVE_SIGN = "-";

    /**
     * 暂存 增值服务编码
     * */
    public static final String STORAGE_INCRE_SERVICE = "fr-a-0009";

    /**
     * j-one应用编码
     * */
    public static final String SYS_DMS = "dms.etms";

    /**
     * businessType缓存前缀
     * */
    public static final String BUSINESS_TYPE_PREFIX = "BUSINESS-TYPE";

    public static final String THIRD_ENET_BOX_WAYBILL_PREFIX = "THIRD_ENET_BOX_WAYBILL";

    /**
     * 运单路由字段使用的分隔符
     */
    public static final String WAYBILL_ROUTER_SPLIT = "\\|";

    /**
     * 特安送 增值服务编码
     * */
    public static final String TE_AN_SONG_SERVICE = "fr-a-0010";

    /**
     * 外单增值服务编码
     */
    public static final String EASY_FROZEN_SERVICE = "deductibleService";

    /**
     *外单易冻品增值服务编码
     */
    public static final String EASY_FROZEN_SERVICE_KEY = "39001003";

    /**
     * 自营单易冻品增值服务编码
     */
    public static final String SELF_EASY_FROZEN_SERVICE = "easyFreeze";

    /**
     * 特保单增值服务编码
     */
    public static final String LUXURY_SECURITY_SERVICE = "luxurySecurity";


    public static final String CONFIGNAME_MQ = "mq.properties";

    public static final String OWNSIGN = "ownSign";
    public static final String AUTOMATIC_WORKER_OWNSIGN = "DMSAUTOWORK";

    // 接口调用
    /**
     * 传入参数错误代码
     */
    public static final Integer PARAM_ERROR_CODE = -2;
    /**
     * 传入参数错误
     */
    public static final String PARAM_ERROR = "传入参数错误";
    /**
     * 调用服务异常代码
     */
    public static final Integer SERVICE_ERROR_CODE = -1;
    /**
     * 调用服务异常
     */
    public static final String SERVICE_ERROR = "调用服务异常";
    /**
     * 调用接口成功代码
     */
    public static final Integer SUCCESS_NO_CODE = 0;
    /**
     * 调用接口成功，无数据
     */
    public static final String SUCCESS_NO = "调用接口成功，无数据";
    /**
     * 调用接口成功代码
     */
    public static final Integer SUCCESS_CODE = 200;
    /**
     * 调用接口成功
     */
    public static final String SUCCESS = "调用接口成功，有数据";


    /**
     * 外单类型
     */

    public static final Integer POP_B = 10000;

    /**
     * 拍卖订单（多宝岛）
     */
    public static final Integer AUCTION = 2;

    public static final Integer BJ = 1;
    public static final Integer TJ = 3;
    public static final Integer SH = 2;
    public static final Integer CQ = 4;
    /**
     * 运单类型-B商家
     */
    public static final Integer WAYBILL_TYPE_BIZ = 10000;

    /**
     * 站点类型-B商家
     */
    public static final Integer SITE_TYPE_BIZ = 1024;

    public static final String DATE_TIME_FORMATLong = "yyyyMMddHHmmssSSS";



    /******************* 订单查询参数中的加载明细参数,可以多个 end ******************/

    /**
     * 混包箱号
     */
    public static final Integer MixBoxType = 1;


    /**
     * local库数据精简，对应的rules里的规则
     */
    public static final Integer LOCAL_STREAMELINE = 1130;

    /**
     * waybill库精简，对应的rules里的规则
     */
    public static final Integer WAYBILL_STREAMELINE = 1140;

    /**
     * 53位等于2 纯配外单
     */
    public static final char WAYBILL_SIGN_53_2 = '2';
    /**
     * 56位等于1 受信任商家
     */
    public static final char WAYBILL_SIGN_56_1 = '1';



    /**
     * 调用运单的分页接口一次获取的包裹数量
     */
    public static final Integer PACKAGE_NUM_ONCE_QUERY = 5000;

    /**
     * ump ver系统key
     */
    public static final String UMP_APP_NAME_DMSVER = "dmsver.etms";

    /**
     * C网无计泡拦截 存入scan_lists的exp_type
     */
    public static final String WEIGHT_VOLUME_INTERCEPT_SCAN_LISTS_EXP_TYPE = "BL_WV";
    /**WaybillCancel 存入scan_lists的exp_type*/
    public static final String WAYBILL_CANCEL_INTERCEPT_SCAN_LISTS_EXP_TYPE = "BL_BW";

    /*
     * 自动发货校验天数
     */
    public static final long THIRTY_DAYS_TIME = 2592000000L; //30天

    /*
     * 龙门架和分拣机的任务类型（在自动化系统中）
     * */
    public static final String SCANNER_FRAME_TASK_TYPE = "ScannerFrameTask";

    /*
        龙门架和分拣机的任务类型（在分拣系统中）
     */
    @Deprecated
    public static final int SCANNER_FRAME_TASK_TYPE_IN_DMS = 7779;

    /*
     * 自动化任务上传的接口
     */
    public static final String DMS_AUTO_JSF_INTERFACE_TASKSTOREJSFSERVICE = "com.jd.bd.dms.automatic.sdk.modules.task.TaskStoreJsfService";

    /*
        自动化任务上传的方法
     */
    public static final String DMS_AUTO_JSF_METHOD_STORETASK = "storeTask";

    /**
     * 基础资料默认分拣中心所属类型
     */
    public static final Integer DISTRIBUTE_CENTER_SITE_TYPE = 64;


    /**
     * 运单全程跟踪 揽收完成state枚举
     */
    public static final Integer WAYBILL_TRACE_STATE_RECEIVE = -640;



    /**
     * 运单全程跟踪 妥投state枚举
     * */
    public static final String WAYBILL_TRACE_STATE_APPROPRIVATED = "150";


    /**
     * 终端加盟-落local库运单预分拣前缀
     */
    public static final String FRANCHISEE_WAYBILL_BLOCK_SITE_CODE = "EXPFB";

    public static final int  BASIC_B_TRADER_ORG = -100; //B商家对应机构
    public static final String   BASIC_B_TRADER_ORG_NAME = "B商家机构"; //B商家对应机构名称
    public static final int  BASIC_B_TRADER_SITE_TYPE = 1024; //B商家对应站点类型

    /**
     * 一车一单操作类型
     */
    public static final int OPERATE_TYPE_NEW_PACKAGE_SEND = 60;

    public static final String STR_ALL = "ALL";

    public static final String UNDER_LINE = "_";

    /**
     * B网抽检图片前缀标识：B
     */
    public static final String SPOT_CHECK_B = "B";

    /**
     * 众邮称重拦截缓存-生效时间  5分钟的秒数
     */
    public static final int  ALL_MAIL_CACHE_SECONDS = 5;

    /**
     * 常量值：0
     * */
    public static final int  CONSTANT_NUMBER_ZERO = 0;
    /**
     * 常量值：1
     * */
    public static final int  CONSTANT_NUMBER_ONE = 1;
    /**
     * 常量值：-1
     * */
    public static final Integer NEGATIVE_NUMBER_ONE = -1;
    /**
     * 常量值：2
     * */
    public static final int  CONSTANT_NUMBER_TWO = 2;
    /**
     * 常量值：3
     * */
    public static final int  CONSTANT_NUMBER_THREE = 3;

    /**
     * 常量值：10
     * */
    public static final int  CONSTANT_NUMBER_TEN = 10;

    /**
     * 常量值：200
     * */
    public static final int CONSTANT_TWO_HUNDRED = 200;

    /**
     * 常量值：500
     * */
    public static final int CONSTANT_FIVE_HUNDRED = 500;

    /**
     * 常量值：1000
     * */
    public static final int CONSTANT_ONE_THOUSAND = 1000;

    /**
     * 常量值：20
     * */
    public static final int  CONSTANT_NUMBER_TWENTY = 20;

    /**
     * 组板转移标识
     */
    public static final Integer IS_COMBITION_TRANSFER = 1;
    /**
     * 跨越那边要求 打印出来的跨越单号为：KYCode + "-" + 000 + "001"
     */
    public static final String KY_PRINT_CODE_SUFFIX = "001";
    /**
     * 操作标识|开启-1
     */
    public static final Integer FLAG_OPRATE_ON = 1;
    /**
     * 操作标识|关闭-0
     */
    public static final Integer FLAG_OPRATE_OFF = 0;
    /**
     * PDA无封车号任务前缀
     */
    public static final String PDA_UNLOAD_TASK_PREFIX="PDA";

    /**
     * 全部站点隐藏
     */
    public static final int ALL_SITE_CLOSE = -1;

    /**
     * 在配置内的站点隐藏
     */
    public static final int SITE_CLOSE = 1;

    /**
     * 不隐藏
     */
    public static final int SITE_OPEN = 0;

    /**
     * 全部关闭
     */
    public static final String CLOSE_ALL_SITE = "close_all_site";
    /**
     * PDA超标值 1
     */
    public static final Integer IS_EXCESS = 1;

    /**
     * 抽检PDA来源标识
     */
    public static final Integer PDA_SOURCE = 1;

    /**
     * 字典节点级别
     */
    public static final int DICT_LEVEL = 2;

    /**
     * 父节点编码
     */
    public static final String PARENT_CODE = "1019";

    /**
     * 字典分组
     */
    public static final String DICT_GROUP = "1019";


    /**
     * 快运发货标识
     */
    public static final Integer KY_DELIVERY = 1;

    /**
     * 暂存预约接口成功响应码
     */
    public static final Integer STAGING_CHECK_SUCCESS_CODE = 0;

    /**
     * 暂存预约提示语
     */
    public static final String PDA_STAGING_CONFIRM_MESSAGE = "此单为暂存预约单，请暂存上架";

    /**
     * 数字：0
     */
    public static final Integer NUMBER_ZERO = 0;

    /**
     * 数字：1
     */
    public static final Integer NUMBER_ONE = 1;

    /**
     * 数字：2
     */
    public static final Integer NUMBER_TWO = 2;

    /**
     * 数字：3
     */
    public static final Integer NUMBER_THREE = 3;

    /**
     * 数字：9
     */
    public static final Integer NUMBER_NINE = 9;

    /**
     * 数字：24
     */
    public static final Integer NUMBER_TWENTY_FOUR = 24;

    /**
     * 数字：5
     */
    public static final Integer NUMBER_FIVE = 5;
    /**
     * SendD取消状态
     */
    public static final int OPERATE_TYPE_CANCEL_L = 0;
    public static final int OPERATE_TYPE_CANCEL_Y = 1;

    /**
     * 线路类型-默认值0
     */
    public static Integer LINE_TYPE_DEFAULT = 0;

    /**
     * 换行符
     */
    public static String LINE_NEXT_CHAR = "\n";
    /**
     * {
     */
    public static final String JSON_START_STR1  = "{";

    /**
     * [
     */
    public static final String JSON_START_STR2  = "[";
    /**
     * 默认时区-GMT+8
     */
    public final static String TIME_ZONE8 = "GMT+8";
    /**
     * 默认国际化-zh_CN
     */
    public final static String LOCALE_ZH_CN = "zh_CN";
    /**
     * 传摆线路类型列表
     */
    public static List<Integer> CUAN_BAI_LINE_TYPES = new ArrayList<Integer>();
    static{
    	//11-市内传站
    	CUAN_BAI_LINE_TYPES.add(11);
    	//5-摆渡
    	CUAN_BAI_LINE_TYPES.add(5);
    	//9-市内传站返回
    	CUAN_BAI_LINE_TYPES.add(9);
    	//10-长途传站
    	CUAN_BAI_LINE_TYPES.add(10);
    	//34-长途传站返回
    	CUAN_BAI_LINE_TYPES.add(34);
    }

    /**
     * 车牌长度9位 ex:010A00001
     */
    public static final int VEHICLE_NUMBER_LENGTH_9 = 9;

    /**
     * 车牌长度10位 ex:0371A00001
     */
    public static final int VEHICLE_NUMBER_LENGTH_10 = 10;

    /**
     * 查询已扫包裹最大数限制单次IN50
     */
    public static final int QUERY_LOAD_SCAN_MAX = 50;
    //DeliveryPackageD信任包裹称重
    public static Integer isTrust = 1;

    /**
     * 卸车任务流水线模式:只验货不组板
     */
    public static final Integer ASSEMBLY_LINE_TYPE = 0;

    /**
     * 系统名
     */
    public static final String SYSTEM_NAME = "QLFJZXJT";

    /**
     * 导出并发限制数量
     */
    public static Integer CONCURRENCY_EXPORT_LIMIT = 50;

    /**
     * 导出并发key 缓存有效时间 单位:天
     */
    public static Integer EXPORT_REDIS_KEY_TIME_OUT = 1;

    /**
     * 开启状态 1
     */
    public static final String SWITCH_OPEN = "1";

    /**
     * 关闭状态 0
     */
    public static final String SWITCH_OFF = "0";

    /**
     * 默认泡重比：8000
     * */
    public static final Integer DEFAULT_VOLUME_RATE = 8000;

    /**
     * 快运使用的泡重比:6000
     */
    public static final Integer EXPRESS_VOLUME_RATE= 6000;

    /**
     * B网特快重货重泡比
     * */
    public static final int VOLUME_RATIO_TKZH = 6000;
    /**
     * B网非特快重货重泡比
     * */
    public static final int VOLUME_RATIO_NOT_TKZH = 4800;

    /**
     * 驻厂操作全程跟踪
     */
    public static final String TRACE_PACK_RECEIVE = "订单/包裹已接货";
    public static final String TRACE_DELIVERY_COLLECT = "配送员%s揽收完成";

    /***
     * 跨越成功校验缓存前缀
     */
    public static final String KYEXPRESSLOADSUCCESS="kyloadcarsuccess:";
    /***跨越成功发送妥投消息前缀**/
    public static final String KYSENDMSGSUCCESSPREFIX = "kysendmsgsuccessprefix:";

    /**
     * 库表切换警告信息
     */
    public static final String UNLOAD_TRANSFER_WARN_MESSAGE = "抱歉，数据库正在升级中，请稍后再试";

    /**
     * 产品类型-冷链小票
     */
    public static final String PRODUCT_TYPE_COLD_CHAIN_XP = "ll-m-0020";

    /**
     * 产品类型-医药大票
     */
    public static final String PRODUCT_TYPE_MEDICINE_DP = "ll-m-0018";

    public static final String TOTAL_URL_INTERCEPTOR = "*";
    /**
     * 用户有效标识
     */
    public static final Integer FLAG_USER_Is_Resign = 1;
    /**
     * 医药零担产品类型
     */
    public static final String PRODUCT_TYPE_MEDICAL_PART_BILL = "LL-YYLD-M";
    /**
     * 医药冷链产品
     */
    public static final String PRODUCT_TYPE_MEDICAL_COLD_BILL = "ll-m-0002";
    /**
     * 医冷零担（md-m-0002）
     */
    public static final String  PRODUCT_TYPE_MEDICAL_COLD_PART_BILL = "md-m-0002";


    /**
     * 运单预售未付尾款
     */
    public static final String PRODUCT_ABILITY_OF_PRE_SELL_NO_PAY  = "VI007-01";

    /**
     * 运单预售已付尾款
     */
    public static final String PRODUCT_ABILITY_OF_PRE_SELL_PAY_DONE  = "VI007-02";

    /**
     * 产品类型-医冷零担
     */
    public static final String PRODUCT_TYPE_MEDICINE_COLD = "md-m-0002";

    /**
     * 验货菜单编码：0101019
     */
    public static final String MENU_CODE_INSPECTION  = "0101019";

    public static final String TRANSFER_TASK_PREFIX  = "transfer_task_biz_id_%s";
    public static final String DELETE_ZIJIAN_TASK_PREFIX  = "delete_task_biz_id_%s";

    public static final String SEND_TASK_MANUAL_CREATED_PREFIX = "NSST";
    /**
     * sql中in语句数量限制 100
     */
    public static final int DB_SQL_IN_LIMIT_NUM = 100;
    /**
     * sql拆分-最大分组数 10000
     */
    public static final int DB_SQL_IN_MAX_GROUP_NUM = 10000;

    public static  final  int COMBOARD_LIMIT =100;

    /**
     * 传摆发货功能编码：0203004-gz
     */
    public static final String MENU_CODE_SEND_GZ  = "0203004-gz";

    /**
     * OSS外网域名
     */
    public static final String OSS_DOMAIN = "storage.jd.com";

    /**
     * 按任务封车：0101013-rw
     */
    public static final String MENU_CODE_SEAL_GZ  = "0101013-gz";


    /**
     * 批量一车一单发货批次扫描批次
     */
    public static final String MENU_CODE_BATCH_SEND_CODE  = "0101004-gz";

    /**
     * 租板岗-流向锁前缀
     */
    public static final String JY_COMBOARD_SENDFLOW_LOCK_PREFIX  = "jy_comboard_sendflow_lock_%s";
    /**
     * 租板岗-板锁前缀
     */
    public static final String JY_COMBOARD_BOARD_LOCK_PREFIX  = "jy_comboard_board_lock_%s";
    /**
     * 租板岗-混扫任务锁前缀
     */
    public static final String JY_COMBOARD_CTT_LOCK_PREFIX  = "jy_comboard_ctt_lock_%s";

    public static final int  LOCK_EXPIRE  = 3;
    /**
     * 无任务卸车上游站点初始值
     */
    public static final Long START_SITE_INITIAL_VALUE = 0L;

    public static final Integer DEFAULT_PAGE_NO  = 1;
    public static final Integer DEFAULT_PAGE_SIZE  = 10;
    public static final Integer DEFAULT_PAGE_SIZE_LIMIT  = 1024;



    /**
     * 路由对应分拣发货操作类型值
     */
    public static final Integer SORT_SEND_VEHICLE = 41;

    /**
     * 数字
     */
    public static final class Numbers {
        public static final Integer INTEGER_ZERO = 0;
    }

    /**
     * 特殊字符正则
     */
    public static final String SPECIAL_CHAR_REGEX = "[\n\r\t`~!@#$%^&*()+=|{}':;,\\[\\].<>/?！￥…（）—【】‘；：”“’。， 、？]";

    /** 运力编码：飞机场网点类型 */
    public static final Integer NODE_TYPE_AIRPORT = 7;
    /** 运力编码：火车站网点类型 */
    public static final Integer NODE_TYPE_RAILWAY = 9;


    /**
     * 租板岗-流向锁前缀
     */
    public static final String JY_COMBOARD_SENDFLOW_GROUP_LOCK_PREFIX  = "jy_comboard_sendflow_group_lock_%s_%s_%s";


    /**
     * 租板岗-板锁前缀
     */
    public static final String JY_SEAL_LOCK_PREFIX  = "jy_comboard_seal_lock_%s";

    /**
     * 操作进度锁
     */
    public static final String JY_OPERATE_PROGRESS_PREFIX  = "jy_operate_progress_%s";

    public static final String JY_OPERATE_PROGRESS_POSITION_PREFIX  = "jy_operate_progress_position_%s";

    public static final String JY_TMS_SIMPLE_TASK_CODE_PREFIX  = "jy_tms_simple_task_code_%s";
    /**
     * 产品类型-医药专送
     */
    public static final String PRODUCT_TYPE_MEDICINE_SPECIAL_DELIVERY = "md-m-0005";

    /**
     * 集齐加锁前缀
     */
    public static final String JQ_AGG_LOCK_PREFIX = "JQ_LOCK_AGG_{0}_{1}_{2}";
    /**
     * 集齐运单锁（内部给部分包裹list加bit锁）
     */
    public static final String JQ_DETAIL_AGG_LOCK_PREFIX = "JQ_LOCK_DETAIL_AGG_{0}_{1}";
    /**
     * 集齐部分包裹bit锁
     */
    public static final String JQ_DETAIL_AGG_BIT_LOCK_PREFIX = "JQ_DETAIL_AGG_BIT_LOCK_PREFIX_{0}_{1}";
    public static final int JQ_DETAIL_AGG_BIT_LOCK_TIMEOUT = 120;

    /**
     * DB 执行in 限制最大数量
     */
    public static final Integer DB_IN_MAX_SIZE  = 100;

    /**
     * 拣运滞留任务biz前缀
     */
    public static final String JY_BIZ_TASK_STRAND_PREFIX  = "STRAND%s";

    public static Map<String, String> topic2DataSource =new HashMap<>();
    static {
        topic2DataSource.put("jy_aggs","aggsMain");
        topic2DataSource.put("jy_aggs_slave","aggsSlave");
    }
    /**
     * 特安 增值服务编码
     * */
    public static final String TE_AN_SERVICE = "ed-a-0047";


    /**
     * 标准B网车队配置匹配接口常量
     */
    //订单类别 1:自营 2:外单
    public static final Integer B2BSUPPORT_ORDER_TYPE_1 = 1;
    public static final Integer B2BSUPPORT_ORDER_TYPE_2 = 2;
    //订单业务类型
    public static final Integer B2BSUPPORT_ORDER_BUSINESS_TYPE = 2;
    //行业类型
    public static final Integer B2BSUPPORT_INDUSTRY_TYPE = 0;
    //商家id(青龙业主号对应id)
    public static final Long B2BSUPPORT_VENDOR_ID = 0L;
    //期望配送方式
    public static final Integer B2BSUPPORT_REQUIRE_TRANS_MODE = 1;
    //是否冷链
    public static final Integer B2BSUPPORT_COLD_CHAIN = 0;

    //德邦默认用户id
    public static final Integer USER_CODE_DEBON = 0;

    //德邦默认操作站点id
    public static final Integer OPERATE_SITE_CODE_DEBON = -1;

    //德邦默认操作站点名称
    public static final String OPERATE_SITE_NAME_DEBON = "system";

    /**
     * 电商特惠产品编码
     */
    public static final String E_COMMERCE_SPECIAL_OFFER_SERVICE = "ed-m-0059";


    /**
     * 意见反馈APPID
     */
    public static final Long APP_ID = 8181L;

    /**
     * 意见反馈ORG_TYPE_ERP参数
     */
    public static final Integer ORG_TYPE_ERP = 2;

    /**
     * http字符串
     */
    public static final String HTTP_STR = "http";

    /**
     * https字符串
     */
    public static final String HTTPS_STR = "https";
    /**
     * PDA操作分页最大值保护
     */
    public static final Integer PDA_DEFAULT_PAGE_MAXSIZE = 100;


    /**
     * 找货任务互斥锁
     */
    public static final String JY_CREATE_FINDGOODS_TASK_LOCK_PREFIX  = "jy_findGoods_task_lock_%s_%s_%s_%s";

    /**
     * 找货任务互斥锁
     */
    public static final String JY_FINDGOODS_TASK_LOCK_PREFIX  = "jy_findGoods_task_lock_%s";


    /**
     * 异常运单任务缓存key
     */
    public static final String EXP_WAYBILL_CACHE_KEY_PREFIX ="exp.waybill.cache:";

    /**
     * 安检场地关系配置
     */
    public static final String SYS_CONFIG_SECURITY_CHECK_SITE_ASSOCIATION = "security_check_site_association_";

    public static final Integer SEND_FLOW_COUNT_LIMIT_DEFAULT=6;

    /**
     * 波次数据主备数据源映射
     */
    public static Map<String, String> sendPredictaggstopic2DataSource =new HashMap<>();
    static {
        sendPredictaggstopic2DataSource.put("jy_send_predict_aggs","aggsMain");
        sendPredictaggstopic2DataSource.put("jy_send_predict_aggs_slave","aggsSlave");
    }

    /**
     * 异常上报原因与二次安检关系-新版异常上报
     */
    public static final Map<Long, List<Long>> SECURITY_CHECK_NEW_VERSION_ABNORMAL_REASON_MAP = new HashMap<Long, List<Long>>(){{
        put(20009L, new ArrayList<>(Collections.singletonList(20010L)));
        put(20006L, new ArrayList<>(Collections.singletonList(20088L)));
    }};

    /**
     * 异常上报原因与二次安检关系-老版异常上报
     */
    public static final Long SECURITY_CHECK_OLD_VERSION_ABNORMAL_REASON_THIRD_ID = 27000L;

    // 新版分拣中心规范-分拣中心类型
    public static final Integer SORTING_SORT_TYPE = 12351;
    // 新版分拣中心规范-分拣中心子类型
    public static final Integer SORTING_SORT_SUBTYPE = 123511;
    // 新版分拣中心规范-分拣中心下中转站
    public static final Integer SORTING_SORT_THIRD_TYPE = 1235116;
    // 新版分拣中心规范-接货仓类型
    public static final Integer JHC_SORT_TYPE = 12352;

    /**
     *  逆向原因编码
     * 1-拦截逆向
     * 3-清关逆向
     */
    public static final Integer INTERCEPT_REVERSE_CODE_1 = 1;
    public static final Integer INTERCEPT_REVERSE_CODE_3 = 3;

    /**
     * 运单全程跟踪记录类型（强校验）
     * 枚举类型：1:运单维度不记录各包裹全程跟踪,2:运单维度记录各包裹全程跟踪,3:包裹维度记录包裹全程跟踪
     */
    public static final Integer WAYBILL_TRACE_TYPE =1;

    /**
     * 逆向退货类型:拒收退回
     */
    public static final Integer REVERSE_TYPE_REJECT_BACK =7;

    /**
     * 换单来源 2: 分拣/SORT_CENTER
     */
    public static final Integer CHANGE_WAYBILL_OPERATE_SOURCE_SORT_CENTER =2;

    /**
     * 运单全程跟踪记录类型-包裹维度记录包裹全程跟踪 （packageBarCode为包裹号)
     */
    public static final Integer  WAYBILL_TRACE_TYPE_PACKAGE =3;

    /**
     * 对外展示话术标识  1 - 对外不展示话术
     */
    public static final Integer  WAYBILL_TRACE_DISPLAY =1;

    /**
     * 异常类型 1-快速退款，2-外单拦截，3-仓储病单拦截 , 4-理赔拦截,5-生产单,6-客服 7-eclp，8,运营拦截，9-仓预售单拦截
     */
    public static final int WAYBILL_EXCEPTION_ID_8 = 8;

    /**
     * 拦截类型 1:取消订单拦截,2:拒收订单拦截,3:恶意订单拦截,4:分拣中心拦截,5-仓储异常拦截,6-白条强制拦截，7-仓储病单拦截，
     * 8.伽利略拒收订单拦截，9.京沃取消订单拦截 ，10-理赔拦截，11-取消订单拦截仓已拦截成功，12-理赔破损拦截，13-运营退货拦截，14-派送地址不详下单失败拦截15审单失败拦截
     */
    public static final int WAYBILL_INTERCEPT_TYPE_4 = 4;

    public static final String WAYBILL_INTERCEPT_REASON = "违禁品退回";


    /**
     * 运输内网账号类型
     */
    public static final int TMS_INTERNAL_ERP_ACCOUNT_TYPE = 1;



    /**
     * 本场地多扫标识
     */
    public static final Integer MORE_LOCAL_SCAN = 1;

    /**
     * 非本场地多扫标识
     */
    public static final Integer MORE_OUT_SCAN = 2;

    /**
     * 本场地部分多扫标识
     */
    public static final Integer MORE_LOCAL_PART_SCAN = 3;

    /**
     *  配置信息-非本场地多扫弱提醒开关
     *
     */
    public static final String MORE_OUT_SCAN_NOTIFY_SWITCH = "more.out.scan.notify.switch";

    /**
     *  配置信息-查询es卸车扫描是否多扫开关
     *
     */
    public static final String MORE_SCAN_QUERY_ES_SWITCH = "more.scan.query.es.switch";

    /**
     *  配置信息-设备抽检申诉核对超时未确认时长
     *
     */
    public static final String SPOT_CHECK_APPEAL_TIME_OUT = "spot.check.appeal.time.out";

    /**
     *  配置信息-处于待发货状态的自建任务停留时长
     *
     */
    public static final String TO_SEND_MANUAL_TASK_TIME_OUT = "to.send.manual.task.time.out";

    /**
     *  配置信息-处于发货中状态的自建任务停留时长
     *
     */
    public static final String SENDING_MANUAL_TASK_TIME_OUT = "sending.manual.task.time.out";

    /**
     * 滞留原因-清场
     */
    public static final Integer REASON_CODE_FIND_GOODS = 115;
    public static final String REASON_MESSAGE_FIND_GOODS = "清场找到";
    /**
     * oss链接内外网key
     */
    public static final String OSS_OUTER_NET_KEY = "oss.outerNet";
    public static final String OSS_INNER_NET_KEY = "oss.innerNet";
    public static final String OSS_INNER_OFFICE_NET_KEY = "oss.innerNet.office";
    /**
     * 配置信息-自动签退超过多少小时未签退的数据
     */
    public static final String SYS_CONFIG_NOT_SIGNED_OUT_RECORD_MORE_THAN_HOURS="sys.config.autoHandleSignInRecord.notSignedOutRecordMoreThanHours";

    /**
     * 集包岗-任务锁前缀
     */
    public static final String JY_COLLECT_BOX_LOCK_PREFIX  = "jy_collect_package_box_lock_%s";

    /**
     * PDA调查问卷ID配置
     */
    public static final String PDA_QUESTIONNAIRE_ID = "pda.questionnaire.id";

    /**
     * 运单返调度审批配置
     */
    public static final String  REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG = "reassignWaybill.province.area.approval.config";


    /**
     * 返调度运单任务锁key
     */
    public static final String REASSIGN_WAYBILL_LOCK_KEY_PREFIX ="reassign.waybill.lock:";

    /**
     * 调查问卷场地白名单
     */
    public static final String PDA_QUESTIONNAIRE_SITE_WHITE_LIST = "pda.questionnaire.white.list";

    /**
     * 换单打印限制1次场地白名单
     */
    public static final String  EXCHANGE_WAYBILL_PRINT_LIMIT_1_SITE_WHITE_LIST = "exchange.waybill.print.limit.site.white.list";


    /**
     * PDA调查问卷ID配置
     */
    public static final String PDA_QUESTIONNAIRE_FUNC_CODE = "pda.questionnaire.func.code";

    /**
     * 找货通知redis key:  find_goods_notice_场地id_签到日期_波次开始时间_波次结束时间
     * eg:find_goods_notice_8663_2023-11-28_09:00:00_12:00:00
     */
    public static final String FIND_GOODS_NOTICE_CACHE_KEY = "find_goods_notice_%s_%s_%s_%s";

    public static final String FIND_GOODS_NOTICE_TITLE = "波次清场找货通知";

    public static final String FIND_GOODS_NOTICE_CONTENT = "%s分拣中心，%s%s（波次开始时间）-%s（波次结束时间）班次清场，需找货%s件，已找到%s件，仍有%s件未找到，其中包含%s件高值、%s件特快、%s件生鲜，请安排继续找货。";


    /**
     * 包裹补打站点类型限制配置
     */
    public static final String PACKAGE_PRINT_LIMIT_SITE_TYPE_CONFIG  = "package.print.limit.siteType.config";

    /**
     * 标准岗位编码限制配置
     */
    public static final String PACKAGE_PRINT_LIMIT_POSITION_CODE_TYPE_CONFIG  = "package.print.limit.positionCode.config";


    /**
     * 备件库
     */
    public static final String SITE_TYPE_SPWMS = "spwms";

    /**
     * 计提时间-小时
     */
    public static final String USER_SIGN_RECORD_FLOW_ACCRUAL_HOUR = "userSignRecordFlow.accrualHour";

    /**
     * 计提时间-日期
     */
    public static final String USER_SIGN_RECORD_FLOW_ACCRUAL_DAY = "userSignRecordFlow.accrualDay";
    /**
     * 换单打印限制1次全国开关
     */
    public static final String  EXCHANGE_WAYBILL_PRINT_LIMIT_1_SWITCH = "exchange.waybill.print.limit.switch";

    /**
     * 计提时间-小时 最大修改时间 修改时请参考签到推送人资时间，不要晚于这个时间
     */
    public static final String USER_SIGN_RECORD_FLOW_LAST_MODIFY_ACCRUAL_HOUR = "userSignRecordFlow.lastModifyAccrualHour";

    /**
     * 计提时间-日期 最大修改时间 修改时请参考签到推送人资时间，不要晚于这个时间
     */
    public static final String USER_SIGN_RECORD_FLOW_LAST_MODIFY_ACCRUAL_DAY = "userSignRecordFlow.lastModifyAccrualDay";

    /**
     * 装车评价包裹凌乱倾倒
     */
    public static final Integer DIMENSION_600 = 600;

    /**
     * 装车评价大压小/重压轻/木压纸
     */
    public static final Integer DIMENSION_800 = 800;


    /**
     * 运单返调度审批配置 新
     */
    public static final String  REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG_NEW = "reassignWaybill.province.area.approval.config.new";

    /**
     * 运单返调度审批版本配置
     */
    public static final String  REASSIGN_WAYBILL_PROVINCE_AREA_APPROVAL_CONFIG_FLOW_VERSION_NEW = "reassignWaybill.province.area.approval.config.flow.version.new";

    /**
     * win_pda下线
     */
    public static final String SYS_CONFIG_WIN_PDA_OFFLINE = "win_pda_offline";


    /**
     * android_pda下线
     */
    public static final String SYS_CONFIG_ANDROID_PDA_OFFLINE = "android_pda_offline";

    /**
     * 自动签退任务-开关
     */
    public static final String SYS_CONFIG_AUTO_SIGN_OUT_SWITCH = "auto.sign.out.switch";

    /**
     * 自动签退任务-扫描场地范围
     */
    public static final String SYS_CONFIG_AUTO_SIGN_OUT_SITE_CODE = "auto.sign.out.site.code";
    /**
     * 返调度退仓站点协助配送关系配置
     */
    public static final String  REASSIGN_WAYBILL_STORE_SITE_CONFIG = "reassign.waybill.store.site.config";

    /**
     * 空铁提货岗流向模板号前缀 AVIATION_RAIL各取前两字母
     */
    public static final String AVIATION_TEMPLATE_PREFIX = "AVIATION";

    public static final String RAIL_TEMPLATE_PREFIX = "RAIL";

    /**
     * 配置信息-基于排班自动签退试用场地列表
     */
    public static final String AUTO_SIGN_OUT_SCHEDULE_SITE = "auto.sign.out.schedule.site";

    /**
     * 排班开始日期前1小时
     * */
    public static final Integer SCHEDULE_BEFORE_ONE_HOUR = -1;

    /**
     * 签到开始日期前1天
     * */
    public static final Integer SIGN_BEFORE_ONE_DAY = -1;



    /**
     * 箱号嵌套最大允许的层级
     */
    public static final int BOX_NESTED_MAX_DEPTH = 2;

    /**
     * 操作流水表表名
     */
    public static final String TABLE_JY_OPERATE_FLOW  = "jy_operate_flow";

    /**
     * 配置信息-司机到达B2B业务类型列表
     */
    public static final String TMS_ARRIVE_B2B_BUSINESS_TYPE = "tms.arrive.b2b.business.type";


    /**
     * 评价申诉权限 0 关闭
     */
    public static final Integer EVALUATE_APPEAL_PERMISSIONS_0 = 0;
    /**
     * 评价申诉权限 1 开启
     */
    public static final Integer EVALUATE_APPEAL_PERMISSIONS_1 = 1;
    /**
     * 装车评价申诉集合key
     */
    public static final String APPEAL_MAP_KEY_ID = "id";
    /**
     * 装车评价申诉集合key
     */
    public static final String APPEAL_MAP_KEY_OPINION = "opinion";
    /**
     * 审核记录统计数
     */
    public static final Integer APPEAL_COUNT_NUM = 3;
    /**
     * 评价状态：0-不满意 1-满意
     */
    public static final Integer EVALUATE_STATUS_DISSATISFIED = 0;
    /**
     * 评价状态：0-不满意 1-满意
     */
    public static final Integer EVALUATE_STATUS_SATISFIED = 1;
    /**
     * 评价类型：1-装车 2-卸车
     */
    public static final Integer EVALUATE_TYPE_LOAD = 1;

    /**
     * 配置信息-视频中台文件上传域名内外网转换
     */
    public static final String VIDEO_DOMAIN_TRANSFORM_MAP = "video.domain.transform.map";

    /**
     * 电子围栏验货 全程跟踪话术
     */
    public static final String ELECTRONIC_FENCE_TRACE_INSPECTION_REMARK = "整车验货 (车牌号：%s)";

    /**
     * 电子网关验货 全程跟踪话术
     */
    public static final String ELECTRONIC_GATEWAY_TRACE_INSPECTION_REMARK = "整容验货（容器号：%s）";

    /**
     * 人工验货全程跟踪话术
     */
    public static final String TRACE_INSPECTION_REMARK = "%s场地已验货";
    /**
     * 弃件暂存岗位限制配置
     */
    public static final String DISCARDED_STORAGE_LIMIT_POSITION_CONFIG  = "discarded.storage.limit.position.config";



    /**
     * 围栏到车验货场地配置
     */
    public static final String TMS_SEND_ARRIVE_AND_BOOK_SITE_CONF = "tms.send.arrive.and.book.site.conf";

    /**
     * 循环物资验货场地配置
     */
    public static final String RECYCLE_MATERIAL_OPERATE_RECORD_SITE_CONF = "recycle.material.operate.record.site.conf";

    /**
     * 围栏到车验货场地类型配置
     */
    public static final String TMS_SEND_ARRIVE_AND_BOOK_SITE_TYPE_CONF = "tms.send.arrive.and.book.site.conf.type.conf";

    /**
     * 循环物资验货场地类型配置
     */
    public static final String RECYCLE_MATERIAL_OPERATE_RECORD_SITE_TYPE_CONF = "recycle.material.operate.record.site.type.conf";
    /**
     * 取消退货组，异常原因 白名单仓配置
     */
    public static final String CANCEL_RETURN_GROUP_WHITE_LIST_CONF = "cancel.return.group.white.list.conf";

    /**
     * 网格工序编制人数配置-PDA强卡开关
     */
    public static final String STAND_NUM_PDA_SIGN_CHECK_SWITCH = "stand.num.pda.sign.check.switch";

    /**
     * 违禁品上报包裹校验开关
     */
    public static final String CONTRABAND_PACKAGE_CHECK_SWITCH = "contraband.package.check.switch";

    /**
     * 租板发货-司机违规举报锁前缀
     */
    public static final String JY_DRIVER_VIOLATION_REPORTING_LOCK_PREFIX  = "jy_driver_violation_reporting_lock_%s";
    /**
     * 租板发货-司机违规举报同步质检key
     */
    public static final String JY_DRIVER_VIOLATION_REPORTING_KEY_PREFIX  = "jy_driver_violation_reporting_key_%s";

    /**
     * 箱号目的地校验开关配置
     */
    public static final String CHECK_BOX_END_SITE_MATCH_SWITCH  = "check.box.end.site.match.switch";

    /**
     * 包裹补打次数拦截配置
     */
    public static final String PACKAGE_REPRINT_INTERCEPT_CONFIG  = "package.reprint.intercept.config";

}
