package com.jd.bluedragon.enums;

public enum SendStatusEnum {
    HAS_BEEN_SENDED(1,"已发货"),
    NO_SEND(0,"未发货");
    private int code;
    private String name;

    SendStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
