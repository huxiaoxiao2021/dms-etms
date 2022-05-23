package com.jd.bluedragon.distribution.ministore.enums;

public enum MSDeviceBindEventTypeEnum {
    SEAL_BOX(1,"封箱"),
        UN_SEAL_BOX(2,"解封箱");

    private Integer code;
    private String msg;
    MSDeviceBindEventTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
