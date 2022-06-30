package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName SendBarCodeQueryEntranceEnum
 * @Description
 * @Author wyh
 * @Date 2022/6/14 14:34
 **/
public enum SendBarCodeQueryEntranceEnum {

    INTERCEPT(1, "拦截"),
    FORCE_SEND(2, "强制发货"),
    GO_TO_SEAL_PREVIEW(3, "前往封车前预览"),
    ;

    private Integer code;

    private String name;

    SendBarCodeQueryEntranceEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
