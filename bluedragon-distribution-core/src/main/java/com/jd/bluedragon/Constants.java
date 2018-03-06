package com.jd.bluedragon;

import com.jd.bluedragon.utils.LogHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

public class Constants {
    public static final char WAYBILL_SIGN_B='3';
    public static final String MAX_PACK_NUM = "MAX_PACK_NUM";
    public static final String REST_KEY = "REST_KEY";
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT1 = "yyyyMMdd";
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
    
    public static final String SEPARATOR_COMMA = ",";
    public static final String SEPARATOR_APOSTROPHE = "'";
    public static final String SEPARATOR_SEMICOLON = ";";
    public static final String SEPARATOR_HYPHEN = "-";
    
    public static final String PUNCTUATION_OPEN_BRACKET = "[";
    public static final String PUNCTUATION_CLOSE_BRACKET = "]";
    
    public static final String OPERATE_SUCCESS = "1";
    public static final String OPERATE_FAIL = "0";
    
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
    /** 大件包裹标识 **/
    public static final Short BOXING_TYPE = 2;
    /* 基础资料SiteType: 16为三方，4为自营 */
    public static final int BASE_SITE_SITE = 4;//站点
    public static final Integer BASE_SITE_TYPE_THIRD = 16;
    /* 基础资料siteType:64为分拣中心 */
    public static final Integer BASE_SITE_DISTRIBUTION_CENTER = 64;
    /* 基础资料siteType:256为二级分拣中心 */
    public static final Integer BASE_SITE_DISTRIBUTION_SUBSIDIARY_CENTER = 256;
    /* 基础资料siteType:1024为B商家 */
    public static final int BASE_SITE_BUSSINESS=1024;

    public static final int OPERATE_TYPE_THIRD_INSPECTION = 80;
    
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
     * 时间：一天的秒数
     */
    public static final int TIME_SECONDS_ONE_DAY = 86400;
    /**
     * 时间：一周的秒数
     */
    public static final int TIME_SECONDS_ONE_WEEK = 7*TIME_SECONDS_ONE_DAY;
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
    public static final String BASIC_STAFF_COL = "staffId";	//员工标识

    private static final Log logger= new SimpleLog("test");
    public static void main(String[] args) {

        LogHelper.errorUseCurrentStackTrace(logger,"test");
    }
    
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
     * String类型标识-true
     */
    public static final String STRING_FLG_TRUE = "1";
    /**
     * String类型标识-false
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
     * 包裹称重流水-操作类型-仓储操作
     */
    public static final Integer PACK_OPE_FLOW_TYPE_CC_REC = 4;
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
     * waybill_cancel 表 featureType ：30-病单，31-取消病单，32- 非病单
     */
    public static final Integer FEATURE_TYPCANCEE_UNSICKL = 32; // 非病单
    public static final Integer FEATURE_TYPCANCEE_SICKL = 30; // 病单

    /**
     * Inspection 表 ，queue_no 字段长度 varchar(32) ，排队号
     */
    public static final Integer QUEUE_NO_LEGNTH = 32;

    //派车单号默认值
    public static final String SCHEDULE_CODE_DEFAULT = "-1";  //默认的scheduleCode值

    public static final Integer WAYBILL_DELIVERED_CODE = 150;  //订单妥投状态编码
    public static final Integer WAYBILL_REJECT_CODE = 160;  //订单拒收状态编码
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
    public static final String DMS_WEB_PTORDER_DIFF_R="DMS-WEB-PTORDER-DIFF-R"; //平台差异处理
    public static final String DMS_WEB_PTORDER_QUEUE_R="DMS-WEB-PTORDER-QUEUE-R"; //平台排队号查询
    public static final String DMS_WEB_PTORDER_RECEIVE_R="DMS-WEB-PTORDER-RECEIVE-R"; //平台实收查询
    public static final String DMS_WEB_SORTING_CROSSBOX_R ="DMS-WEB-SORTING-CROSSBOX-R"; //跨箱号中转
    public static final String DMS_WEB_SORTING_GLOBALTRADE_R ="DMS-WEB-SORTING-GLOBALTRADE-R"; //全球购
    public static final String DMS_WEB_SORTING_GANTRY_R ="DMS-WEB-SORTING-GANTRY-R"; //龙门架注册
    public static final String DMS_WEB_SORTING_SORTSCHEME_R ="DMS-WEB-SORTING-SORTSCHEME-R"; //分拣计划配置
    public static final String DMS_WEB_SORTING_ADDRESSCHANGE_R ="DMS-WEB-SORTING-ADDRESSCHANGE-R"; //客户改址查询
    public static final String DMS_WEB_SORTING_PARTNERINSPECTION_R ="DMS-WEB-SORTING-PARTNERINSPECTION-R"; //第三方差异处理
    public static final String DMS_WEB_SORTING_GODDESS_R ="DMS-WEB-SORTING-GODDESS-R"; //分拣运行时全程跟踪
    public static final String DMS_WEB_SORTING_OFFLINELOG_R ="DMS-WEB-SORTING-OFFLINELOG-R"; //分拣中心离线操作日志
    public static final String DMS_WEB_SORTING_FRESH_R ="DMS-WEB-SORTING-FRESH-R"; //生鲜温度录入
    public static final String DMS_WEB_SORTING_GANTRYAUTOSEND_R ="DMS-WEB-SORTING-GANTRYAUTOSEND-R"; //龙门架自动发货
    public static final String DMS_WEB_SORTING_SORTMACHINEAUTOSEND_R ="DMS-WEB-SORTING-SORTMACHINEAUTOSEND-R"; //分拣机自动发货
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
    public static final String DMS_WEB_ISV_CONTROL_R ="DMS-WEB-ISV-CONTROL-R"; //ISV版本控制
    public static final String DMS_WEB_ISV_MANAGE_R ="DMS-WEB-ISV-MANAGE-R"; //ISV版本管理
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
     * Double值-0
     */
    public static final Double DOUBLE_ZERO = 0.0;
    /**
     * 配置信息-北京的分拣中心
     */
    public static final String SYS_CONFIG_NAME_BJ_DMS_SITE_CODES = "bjDmsSiteCodes";
    /**
     * 封车批次号缓存前缀
     */
    public static final String CACHE_KEY_PRE_SEAL_SENDCODE ="CACHE_SEAL_SENDCODE-";
}
