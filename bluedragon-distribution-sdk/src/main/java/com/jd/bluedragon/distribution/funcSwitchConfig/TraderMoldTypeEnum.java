package com.jd.bluedragon.distribution.funcSwitchConfig;

public enum TraderMoldTypeEnum {
    inside_type(1003,"公司内部");

    private int code;
    private String typeName;

    TraderMoldTypeEnum(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
