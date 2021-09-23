package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 超标状态枚举
 *
 * @author: hujiping
 * @date: 2019/10/20 17:56
 */
public enum ExcessStatusEnum {

    EXCESS_ENUM_NO(0,"未超标"),
    EXCESS_ENUM_YES(1,"超标"),
    EXCESS_ENUM_COMPUTE(2,"待集齐计算"),
    EXCESS_ENUM_NO_KNOW(-1,"未知");

    private Integer code;
    private String name;

    ExcessStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
