package com.jd.bluedragon.enums;

public enum EnvEnum {
    DEV("dev","开发环境"),
    TEST("test","测试环境"),
    UAT("uat","uat环境"),
    PROD("prod","生产环境");
    private String code;
    private String name;

    EnvEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
