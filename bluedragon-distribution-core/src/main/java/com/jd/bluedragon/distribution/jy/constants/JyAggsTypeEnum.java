package com.jd.bluedragon.distribution.jy.constants;

public enum JyAggsTypeEnum {
    JY_SEND_AGGS(1,"发货岗数据统计"),
    JY_UNLOAD_AGGS(2,"卸车岗数据统计"),
    JY_COMBOARD_AGGS(3,"组板岗数据统计");
    private int code;
    private String msg;

    JyAggsTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
