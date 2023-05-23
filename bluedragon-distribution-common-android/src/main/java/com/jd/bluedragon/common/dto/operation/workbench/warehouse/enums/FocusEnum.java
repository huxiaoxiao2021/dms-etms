package com.jd.bluedragon.common.dto.operation.workbench.warehouse.enums;

/**
 * @author liwenji
 * @description 关注
 * @date 2023-05-18 11:26
 */
public enum FocusEnum {
    FOCUS(1,"关注"),
    UN_FOCUS(0,"取消关注");
    private int code;
    private String msg;

    FocusEnum(int code, String msg) {
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
