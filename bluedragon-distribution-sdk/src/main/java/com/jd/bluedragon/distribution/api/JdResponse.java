package com.jd.bluedragon.distribution.api;

public class JdResponse extends JdObject {
    
    private static final long serialVersionUID = 4872674759695586302L;
    
    public static final Integer CODE_OK = 200;
    public static final String MESSAGE_OK = "OK";
    
    public static final Integer CODE_OK_NULL = 2200;
    public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
    
    public static final Integer CODE_SEE_OTHER = 303;
    public static final String MESSAGE_SEE_OTHER = "See Other";

    public static final Integer CODE_BUSY = 20108;
    public static final String MESSAGE_BUSY = "系统繁忙，请稍后重试！";

    public static final Integer CODE_PARAM_ERROR = 10000;
    public static final String MESSAGE_PARAM_ERROR = "参数错误";
    public static final String MESSAGE_PARAM_ERROR_2 = "网点信息为空，请重新登录再试！";

    public static final Integer CODE_NO_POP_WAYBILL = 11000;
    public static final String MESSAGE_NO_POP_WAYBILL = "运单不存在或者为非POP";

    public static final Integer CODE_POP_ABNORMAL_WAYBILL = 12000;
    public static final String MESSAGE_POP_ABNORMAL_WAYBILL = "运单正在进行POP差异订单审核，无法进行收货";

    public static final Integer CODE_NO_SITE = 13000;
    public static final String MESSAGE_NO_SITE="站点【{0}】不存在或已关闭";

    public static final Integer CODE_INVALID_PACKAGECODE_BOXCODE = 14000;
    public static final String MESSAGE_INVALID_PACKAGECODE_BOXCODE="请扫描正确的包裹号/箱号";

    public static final Integer CODE_CAN_NOT_GENERATE_PACKAGECODE =  15000;
    public static final String MESSAGE_CAN_NOT_GENERATE_PACKAGECODE = "运单号[{0}]发货验证失败。该运单中无包裹信息";

    public static final Integer CODE_SERVICE_ERROR = 20000;
    public static final String MESSAGE_SERVICE_ERROR = "服务异常";

    public static final Integer CODE_SERVICESEND_ERROR = 20011;
    public static final String MESSAGE_SERVICESEND_ERROR = "调用监控差异接口异常";
    
    public static final Integer CODE_PACKAGE_ERROR = 20001;
    public static final String MESSAGE_PACKAGE_ERROR = "无操作记录,请检查包裹";
    
    public static final Integer CODE_SENDED = 20002;
    public static final String MESSAGE_SENDED = "包裹或者运单已经发货，不能执行当前操作";

    public static final Integer CODE_SENDCODE_ERROR = 20003;
    public static final String MESSAGE_SENDCODE_ERROR = "此批次号已发货完成，请更换新批次号";

    public static final Integer CODE_SENDDATA_GENERATED_EMPTY = 20004;
    public static final String MESSAGE_SENDDATA_GENERATED_EMPTY = "生成发货数据为空";

    public static final Integer CODE_THREEPL_SCHEDULE_ERROR = 20005;
    public static final String MESSAGE_THREEPL_SCHEDULE_ERROR = "此单的商家不允许转三方快递派送，请调度到自营站点！";

    public static final Integer CODE_SITE_OFFLINE_ERROR = 20006;
    public static final String MESSAGE_SITE_OFFLINE_ERROR = "不能预分拣到已经线下运营的站点!";

    public static final Integer CODE_STORE_BLACKLIST_ERROR = 20007;
    public static final String MESSAGE_STORE_BLACKLIST_ERROR = "此运单禁止更改逆向目的仓，必须按照出库原仓退回!";

    public static final Integer CODE_SITE_BLACKLIST_ERROR = 20008;
    public static final String MESSAGE_SITE_BLACKLIST_ERROR = "此运单必须按照出库原仓退回，请先操作逆向换单打印!";

    public static final Integer CODE_SITE_SIGNRE_ERROR = 20009;
    public static final String MESSAGE_SITE_SIGNRE_ERROR = "此运单要求签单返回，只能分配至自营站点!";

    public static final Integer CODE_FEATHER_LETTER_ERROR = 20010;
    public static final String MESSAGE_FEATHER_LETTER_ERROR = "此单为鸡毛信运单请输入设备号或取消鸡毛信复选框！";

    public static final Integer CODE_FEATHER_LETTER_DISABLE_ERROR = 20011;
    public static final String MESSAGE_FEATHER_LETTER_DISABLE_ERROR = "鸡毛信设备不可用，请确认！";

    public static final Integer CODE_CODMONAY_THIRD_SITE_ERROR = 20012;
    public static final String MESSAGE_CODMONAY_THIRD_SITE_ERROR = "有货到付款金额，不能分配到第三方快递！";

    public static final Integer CODE_OUT_ZONE_ERROR = 20013;
    public static final String MESSAGE_OUT_ZONE_ERROR = "此运单收件地址为春节禁售或疫情地区，无法揽收，请退回商家！";

    public static final Integer CODE_GET_TRANSPLAN_ERROR = 20014;
    public static final String MESSAGE_GET_TRANSPLAN_ERROR = "没有查询到相应运输计划，请核实查询条件后重新操作！";

    public static final Integer CODE_CHECK_MATERIAL_ERROR = 20015;
    public static final String MESSAGE_CHECK_MATERIAL_ERROR = "此运单号已绑定循环集包袋，请扫描包裹号操作!";

    public static final Integer CODE_BASIC_SITE_CODE_ERROR = 20016;
    public static final String  MESSAGE_BASIC_SITE_CODE_ERRPR = "预分拣站点滑道信息获取失败";

    public static final Integer CODE_TARGET_SITE_NO_ROUTE_CONFIRM = 20017;
    public static final String  MESSAGE_TARGET_SITE_NO_ROUTE_CONFIRM = "调度站点无滑道信息，是否继续操作?";

    public static final Integer CODE_SELF_REVERSE_SCHEDULE_ERROR = 20018;
    public static final String MESSAGE_SELF_REVERSE_SCHEDULE_ERROR = "特殊品类逆向订单禁止直接返调度到库房，请操作返调度到就近【备件库】";

    public static final Integer CODE_UNLOADBILL = 2424;
    public static final String MESSAGE_UNLOADBILL = "已经装载不允许取消";

    public static final Integer CODE_RESIGNATION = 3000;
    public static final String MESSAGE_RESIGNATION = "登陆用户未在青龙基础资料中维护，请重新登陆!";

    public static final Integer CODE_TIMEOUT = 3001;
    public static final String MESSAGE_TIMEOUT = "客户端时间和服务器时间相差5分钟";
    
    public static final Integer CODE_NOT_FOUND = 404;
    public static final Integer CODE_MISMATCH = 405;
    public static final Integer CODE_PARAMETER_REQUIRED = 406;
    public static final Integer CODE_WRONG_STATUS = 408;
    public static final Integer CODE_INTERNAL_ERROR = 500;
    public static final Integer CODE_TIME_ERROR = 501;
    public static final Integer CODE_SITE_ERROR = 502;
    public static final Integer CODE_RETURN_ERROR = 503;
    public static final Integer CODE_NOT_3PL_SITE = 504;
    public static final Integer CODE_NOT_EXIST_SORTINGRET = 505;
    public static final Integer CODE_REDISPATCH = 506;
    public static final Integer CODE_NOT_CONTAINER_RELATION = 20003;
    public static final Integer CODE_NOT_EXIST_WAYBILL =409;
    public static final Integer CODE_EXIST_BOX_CODE =600;


    public static final String MESSAGE_DRIVERS_EMPTY = "获取司机信息为空";
    public static final String MESSAGE_VEHICLES_EMPTY = "获取车牌号信息为空";
    public static final String MESSAGE_SITES_EMPTY = "获取站点信息为空";
    public static final String MESSAGE_SITE_EMPTY = "没有对应站点";
    public static final String MESSAGE_ERROR_EMPTY = "获取错误信息为空";
    public static final String MESSAGE_SITETYPE_EMPTY = "获取站点类型信息为空";
    public static final String MESSAGE_ERROR_UPDATE_EMPTY = "根据更新时间获取错误信息为空";
    public static final String MESSAGE_TIME_ERROR = "时间格式错误";
    public static final String MESSAGE_SITE_ERROR = "该用户没有对应站点";
    public static final String MESSAGE_ALLORGS_EMPTY = "获取全部机构信息为空";
    public static final String MESSAGE_RETURN_ISSEND = "已经发货不能退货";
    public static final String MESSAGE_RETURN_ERROR = "分拣退货操作失败";
    public static final String MESSAGE_SORTINGCENTER_ERROR = "分拣中心编码错误";
    public static final String MESSAGE_ALLSITE_EMPTY = "获取所有站点信息为空";
    public static final String MESSAGE_NOT_3PL_SITE = "获取站点不是三方站点";
    public static final String MESSAGE_NOT_EXIST_SORTINGRET = "该订单未做分拣退货操作";
    public static final String MESSAGE_REDISPATCH = "该订单已做站点反调度再投";
    public static final String MESSAGE_NOT_EXIST_WAYBILL = "运单不存在";
    public static final String SEND_SITE_NO_MATCH="发货站点与箱子目的地不一致,是否继续?";
    public static final String SEND_WAYBILL_NOT_FOUND="没有获取到该运单";
    public static final String SEND_BOX_NOT_FOUND="没有获取到该箱子";
    public static final String MESSAGE_EXIST_BOX_CODE="箱号已存在";
    public static final String MESSAGE_NO_FEATHER_LETTER="非鸡毛信运单，不用取消鸡毛信服务";
    public static final String MESSAGE_OUT_ZONE="此运单收件地址为春节禁售或疫情地区，请操作逆向换单后退回";

    public static final Integer CODE_RE_PRINT_IN_ONE_HOUR = 30100;
    public static final String MESSAGE_RE_PRINT_IN_ONE_HOUR = "条码在1小时内重复打印，是否继续？";

    public static final Integer CODE_RE_PRINT_NO_PACK_LIST = 30101;
    public static final String MESSAGE_RE_PRINT_NO_PACK_LIST = "运单没有包裹数据，请查看运单详情包裹信息或联系IT，咚咚：org.wlxt2";

    public static final Integer CODE_RE_PRINT_NO_THIS_PACK = 30102;
    public static final String MESSAGE_RE_PRINT_NO_THIS_PACK = "运单{0}中不存在该包裹号!";

    public static final Integer CODE_RE_PRINT_PACK_SIZE_TOO_LARGE = 30103;
    public static final String MESSAGE_RE_PRINT_PACK_SIZE_TOO_LARGE = "该单包裹数为{0}，确定打印所有包裹";

    public static final Integer CODE_RE_SCHEDULE_WAYBILL_NO_INFO = 30104;
    public static final String MESSAGE_RE_SCHEDULE_WAYBILL_NO_INFO = "对不起，没有获取到您需要的运单";

    public static final Integer CODE_RE_SCHEDULE_WAYBILL_NO_PACKAGE = 30105;
    public static final String MESSAGE_RE_SCHEDULE_WAYBILL_NO_PACKAGE = "对不起，此运单没有包裹信息";

    public static final Integer CODE_RE_SCHEDULE_WAYBILL_NO_THIS_PACKAGE = 30106;
    public static final String MESSAGE_RE_SCHEDULE_WAYBILL_NO_THIS_PACKAGE = "对不起，运单中没有此包裹信息";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO = 30107;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_INFO = "输入的单号有误，请重新输入！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_INFO = 30108;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_INFO = "通过原单号未获得新单号！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_WAYBILLCODE = 30109;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_NEW_WAYBILLCODE = "未触发逆向新运单";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_LP = 30129;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_LP = "该理赔单正在审核中。请到运营管控-理赔单监控报表查看换单状态，当显示“可换单”时即可重试操作。";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS = 30110;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_OUT_15_DAYS = "已超15天，请核实录入是否正确！是否继续操作换单打印?";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME = 30111;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME = "换单新单无体积数据，请输入新单包裹长宽高！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_WEIGHT = 30112;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_WEIGHT = "换单新单无重量数据，请输入新单包裹重量！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME_WEIGHT = 30113;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_WAYBILL_NO_VOLUME_WEIGHT = "换单新单无重量及体积数据，请输入新单包裹重量及长宽高！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_VOLUME = 30114;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_VOLUME = "体积录入异常！此包裹为半收包裹，长宽高必须输入！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_WEIGHT = 30115;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_HALF_PACKAGE_NO_WEIGHT = "重量录入异常！此包裹为半收包裹，重量必须录入！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_NO_WEIGHT = 30116;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_NO_WEIGHT = "启用包裹称重，未录入包裹重量信息！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT = 30117;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT = "包裹重量为{0},是否确定称重打印？";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_100 = 30118;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_100 = "包裹重量为{0},已经超过100KG,是否仍要确定称重打印？";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_1000 = 30119;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_CONFIRM_WEIGHT_OUT_1000 = "包裹重量为{0},已经超过1000KG,不允许操作！";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_UNABLE_WEIGHT_VOLUME = 30120;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_UNABLE_WEIGHT_VOLUME = "该包裹{0}属于半收业务必须启用称重，并进行量方";

    public static final Integer CODE_REVERSE_CHANGE_PRINT_ALREADY = 30121;
    public static final String MESSAGE_REVERSE_CHANGE_PRINT_ALREADY = "该单号{0}已打印";

    public static final Integer CODE_RE_PRINT_REPEAT = 30122;
    public static final String MESSAGE_RE_PRINT_REPEAT = "此条码已操作过补打，是否再次打印？";

    //弃件拦截
    public static final Integer CODE_WAYBILL_WASTE = 30123;
    public static final String MESSAGE_WAYBILL_WASTE = "弃件禁换单，每月5、20日原运单返到货传站分拣中心，用箱号纸打印“返分拣弃件”贴面单同侧(禁手写/遮挡面单)";

    public static final Integer CODE_DATA_OVERFLOW = 10001;
    public static final String MESSAGE_DATA_OVERFLOW = "每批上传包裹数量不能超过200";

    public static final Integer CODE_SORTING_DATA_OVERFLOW = 10002;
    public static final String MESSAGE_SORTING_DATA_OVERFLOW = "每批上传包裹数量不能超过";

    public static final Integer BOX_CODE_ISEXISTS = 10003;
    public static final String MESSAGE_BOX_CODE_ISEXISTS = "箱号已存在";

    public static final Integer SCHEDULE_CODE_FAILED = 10004;
    public static final String MESSAGE_SCHEDULE_CODE_FAILED = "城配项目获取派车单号失败!";

    public static final String MESSAGE_SERVICE_ERROR_C = "服务异常!";

    public static final Integer CODE_SITE_CODE_IS_NULL = 11000;
    public static final String MESSAGE_SITE_CODE_IS_NULL = "分拣中心id为空";



    public static final Integer CODE_EXPID_EMPTY = 409;
    public static final String MESSAGE_CODE_INTERNAL_ERROR = "内部错误";

    public static final Integer CODE_MACHINE_CODE_ERROR = 700;
    public static final String MESSAGE_MACHINE_CODE_ERROR = "机器码配置错误";

    public static final Integer CODE_NotExists = 400;
    public static final String MESSAGE_CODE_NotExists = "运单包裹不存在";

    public static final Integer CODE_Exception = 401;
    public static final String MESSAGE_CODE_Exception = "Exception 异常捕获";
    public static final String MESSAGE_BILLCODE_EXCEPTION = "运单号错误!";


    public static final Integer CODE_WAYBILL_INFO_NOT_EXISTS = 401;
    public static final String  MESSAGE_SORT_SCHEME_NOT_EXISTS = "没有对应的分拣计划！EXPID没有对应格口";
    public static final Integer CODE_SORT_SCHEME_NOT_EXISTS = 402;
    public static final String MESSAGE_WAYBILL_INFO_NOT_EXISTS = "无此运单信息！EXPDE没有对应格口";
    public static final Integer CODE_BOX_CODE_IS_EXISTS = 403;
    public static final String MESSAGE_BOX_CODE_IS_EXISTS = "存在箱号数据，确认是否是重复建包或使用了旧箱号";
    public static final Integer CODE_BOX_CODE_IS_NOT_EXISTS = 405;
    public static final String MESSAGE_BOX_CODE_IS_NOT_EXISTS = "该箱号无建包信息";
    public static final Integer CODE_PACKAGE_CODE_IS_NOT_EXISTS = 405;
    public static final String MESSAGE_PACKAGE_CODE_IS_NOT_EXISTS = "该包裹号无分拣信息";
    public static final Integer CODE_NO_WEIGHT_HEIGHT_CHUTE = 406;
    public static final String MESSAGE_O_WEIGHT_HEIGHT_CHUTE = "无计泡数据而且未配置无称重量方EXPWV格口";
    public static final Integer CODE_UPDATE_DISPATCH_INTERCEPT = 407;
    public static final String MESSAGE_UPDATE_DISPATCH_INTERCEPT = "运单拦截，所有包裹都需要补打，且EXP130或EXP120没有格口";

    public static final Integer CODE_WAYBILL_NOT_ONE_PACKAGE = 408;
    public static final String MESSAGE_CODE_WAYBILL_NOT_ONE_PACKAGE= "{0}非一单一件，不能按运单分拣";
    public static final Integer CODE_WAYBILL_NOT_ONE_EXTERNAL = 409;
    public static final String MESSAGE_WAYBILL_NOT_ONE_EXTERNAL= "{0}非外单，不能按运单分拣";
    public static final Integer CODE_FRANCHISEE_WAYBILL_BLOCK = 411;
    public static final String MESSAGE_FRANCHISEE_WAYBILL_BLOCK = "{0}为加盟运单，未交接";





    /** 请求服务URL */
    private String request;
    
    /** 请求服务URL */
    private String location;
    
    /** 相应状态码 */
    private Integer code;
    
    /** 响应消息 */
    private String message;

    public JdResponse() {
    }
    
    public JdResponse(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    
    public String getRequest() {
        return this.request;
    }
    
    public void setRequest(String request) {
        this.request = request;
    }
    
    public Integer getCode() {
        return this.code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

}
