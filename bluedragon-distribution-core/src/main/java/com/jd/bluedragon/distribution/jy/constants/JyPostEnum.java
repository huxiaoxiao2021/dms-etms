package com.jd.bluedragon.distribution.jy.constants;

/**
 * 岗位类型
 * zhengchengfa
 */
public enum JyPostEnum {
    //
    SEND_SEAL_DMS("101","分拣发货封车岗"),
    SEND_SEAL_TYS("102","转运发货封车岗"),
    SEND_SEAL_BOARD("103","组板发货封车岗"),
    SEND_SEAL_WAREHOUSE("104","接货仓发货封车岗"),
    //
    RECEIVE_DMS("201","分拣卸车岗"),
    RECEIVE_TYS("202","转运卸车岗"),
    //
    ;
    private String code;
    private String msg;

    JyPostEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
