package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author jinjingcheng
 * @date 2020/4/27
 */
public enum ArCarrierTypeEnum {

    SELF_CARRIER(1,"自营"),
    THREE_CARRIER(2, "三方承运商");

    private int code;

    private String name;

    ArCarrierTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
