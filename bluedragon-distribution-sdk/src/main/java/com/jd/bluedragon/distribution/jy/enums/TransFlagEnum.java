package com.jd.bluedragon.distribution.jy.enums;

public enum TransFlagEnum {
    OUT(1, "迁出"),

    IN(2, "迁入");
    private Integer code;
    private String name;

    TransFlagEnum(Integer code, String name) {
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
