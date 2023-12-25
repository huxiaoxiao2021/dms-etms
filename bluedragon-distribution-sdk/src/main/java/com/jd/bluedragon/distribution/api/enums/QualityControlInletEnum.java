package com.jd.bluedragon.distribution.api.enums;

public enum QualityControlInletEnum {
    DMS_SORTING(1,"分拣"),
    REVERSE_INTENSIVE(2,"逆向集约"),
    ;

    private QualityControlInletEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }


    private Integer code;
    private String name;

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
