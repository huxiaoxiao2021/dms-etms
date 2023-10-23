package com.jd.bluedragon.distribution.jy.constants;

public enum TaskBindTypeEnum {
    //
    BIND_TYPE_AVIATION(1,"空铁发货任务绑定关系"),
    ;
    private Integer code;
    private String msg;

    TaskBindTypeEnum(Integer code, String msg) {
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
