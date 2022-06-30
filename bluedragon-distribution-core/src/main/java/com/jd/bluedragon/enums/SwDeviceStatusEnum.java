package com.jd.bluedragon.enums;

public enum SwDeviceStatusEnum {
    AVAILABLE(1,"可用"),
    NOT_AVAILABLE(0,"不可用"),
    MINISTORE_SYS_INVOKE_EXCEPTION(-1,"保温箱服务调用异常");
    private Integer code;
    private String msg;

    SwDeviceStatusEnum(Integer code, String msg) {
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
