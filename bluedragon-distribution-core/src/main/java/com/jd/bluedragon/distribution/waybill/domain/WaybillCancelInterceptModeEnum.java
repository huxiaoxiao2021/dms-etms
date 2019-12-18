package com.jd.bluedragon.distribution.waybill.domain;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName WaybillCancelInterceptModeEnum
 * @date 2019/8/14
 */
public enum WaybillCancelInterceptModeEnum {

    NOTICE(1, "通知"),

    INTERCEPT(2, "拦截");

    private int code;

    private String desc;

    WaybillCancelInterceptModeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
