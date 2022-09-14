package com.jd.bluedragon.distribution.jy.enums;

public enum ExcepScanTypeEnum {
    INTERCEPTED(1,"拦截"),
    FORCE_SEND(2,"强发"),
    INTERCEPTED_AND_FORCE(3,"拦截/强发"),
    HAVE_SCAN(4,"已扫"),
    INCOMPELETE(5,"不齐(已到未扫和未到)"),
    HAVE_INSPECTION_BUT_NOT_SEND(6,"已验未扫"),
    INCOMPELETE_DEAL(7,"不齐(已扫)");
    private Integer code;
    private String name;

    ExcepScanTypeEnum(Integer code, String name) {
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
