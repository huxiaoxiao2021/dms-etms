package com.jd.bluedragon.distribution.jy.enums;

public enum CollectPackageExcepScanEnum {
    HAVE_SCAN(1, "已扫"),
    INTERCEPTED(2, "拦截"),
    FORCE_SEND(3, "强发");
    private Integer code;
    private String name;

    CollectPackageExcepScanEnum(Integer code, String name) {
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
