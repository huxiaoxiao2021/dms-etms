package com.jd.bluedragon.distribution.jy.enums;

public enum MixBoxTypeEnum {
    MIX_ENABLE(1,"允许混装"),
    MIX_DISABLE(0,"不能混装");
    private Integer code;
    private String name;

    MixBoxTypeEnum(Integer code, String name) {
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
