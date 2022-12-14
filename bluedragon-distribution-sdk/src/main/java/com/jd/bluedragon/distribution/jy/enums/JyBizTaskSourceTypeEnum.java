package com.jd.bluedragon.distribution.jy.enums;

/**
 *  卸车任务来源
 **/
public enum JyBizTaskSourceTypeEnum {

    SORTING(1, "分拣"),

    TRANSPORT(2, "转运");

    private Integer code;
    private String name;

    JyBizTaskSourceTypeEnum(Integer code, String name) {
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
