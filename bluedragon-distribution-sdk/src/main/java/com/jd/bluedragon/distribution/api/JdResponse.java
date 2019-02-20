package com.jd.bluedragon.distribution.api;

public class JdResponse extends JdObject {
    
    private static final long serialVersionUID = 4872674759695586302L;
    
    public static final Integer CODE_OK = 200;
    public static final String MESSAGE_OK = "OK";
    
    public static final Integer CODE_OK_NULL = 2200;
    public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
    
    public static final Integer CODE_SEE_OTHER = 303;
    public static final String MESSAGE_SEE_OTHER = "See Other";
    
    public static final Integer CODE_PARAM_ERROR = 10000;
    public static final String MESSAGE_PARAM_ERROR = "参数错误";

    public static final Integer CODE_NO_POP_WAYBILL = 11000;
    public static final String MESSAGE_NO_POP_WAYBILL = "运单不存在或者为非POP";

    public static final Integer CODE_POP_ABNORMAL_WAYBILL = 12000;
    public static final String MESSAGE_POP_ABNORMAL_WAYBILL = "运单正在进行POP差异订单审核，无法进行收货";

    public static final Integer CODE_NO_SITE = 13000;
    public static final String MESSAGE_NO_SITE="站点【{0}】不存在或已关闭";

    public static final Integer CODE_INVALID_PACKAGECODE_BOXCODE = 14000;
    public static final String MESSAGE_INVALID_PACKAGECODE_BOXCODE="请扫描正确的包裹号/箱号";

    public static final Integer CODE_CAN_NOT_GENERATE_PACKAGECODE =  15000;
    public static final String MESSAGE_CAN_NOT_GENERATE_PACKAGECODE = "按运单号[{0}]发货失败";

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
    public static final String MESSAGE_THREEPL_SCHEDULE_ERROR = "此运单对应商家基础资料设置不允许转三方快递派送";

    public static final Integer CODE_SITE_OFFLINE_ERROR = 20006;
    public static final String MESSAGE_SITE_OFFLINE_ERROR = "不能预分拣到已经线下运营的站点!";

    public static final Integer CODE_STORE_BLACKLIST_ERROR = 20007;
    public static final String MESSAGE_STORE_BLACKLIST_ERROR = "此运单禁止更改逆向目的仓，必须按照出库原仓退回!";

    public static final Integer CODE_SITE_BLACKLIST_ERROR = 20008;
    public static final String MESSAGE_SITE_BLACKLIST_ERROR = "此运单必须按照出库原仓退回，请先操作逆向换单打印!";

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

    public static final Integer CODE_RE_PRINT_IN_ONE_HOUR = 30100;
    public static final String MESSAGE_RE_PRINT_IN_ONE_HOUR = "条码在1小时内重复打印，是否继续？";
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
