package com.jd.bluedragon.distribution.jy.enums;

public enum ExcepScanLabelEnum {
    INTERCEPTED(1,"拦截"),
    FORCE_SEND(2,"强发"),
    INCOMPELETE_HAVE_INSPECTION_BUT_NOT_SEND(3,"在库"),
    INCOMPELETE_NOT_ARRIVE(4,"未到");
    private Integer code;
    private String name;

    ExcepScanLabelEnum(Integer code, String name) {
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
