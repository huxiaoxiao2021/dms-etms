package com.jd.bluedragon.distribution.ministore.enums;

public enum BizDirectionEnum {
    FROWARD(10,"正向"),
    BACKWARD(20,"逆向");
    private Integer code;
    private String msg;

    BizDirectionEnum(Integer code, String msg) {
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
