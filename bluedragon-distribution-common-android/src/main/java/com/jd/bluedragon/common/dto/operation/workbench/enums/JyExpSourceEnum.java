package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum JyExpSourceEnum {
    COMMON(0,"通用入口"),
    UNLOAD(1,"卸车入口"),
    SEND(2,"发货入口"),
    ;


    private final int code;

    private final String text;

    JyExpSourceEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
