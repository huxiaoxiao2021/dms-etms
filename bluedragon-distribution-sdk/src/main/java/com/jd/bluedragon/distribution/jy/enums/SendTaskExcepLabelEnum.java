package com.jd.bluedragon.distribution.jy.enums;

public enum SendTaskExcepLabelEnum {
    CANCEL(1,"取消");
    private Integer code;
    private String name;

    SendTaskExcepLabelEnum(Integer code, String name) {
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
