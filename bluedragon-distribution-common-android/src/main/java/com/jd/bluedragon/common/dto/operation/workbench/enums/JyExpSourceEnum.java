package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum JyExpSourceEnum {
    COMMON(0,"通用入口"),
    UNLOAD(1,"卸车入口"),
    SEND(2,"发货入口"),
    SANWU_PC(3,"三无系统"),
    OPERATE_INTERCEPT(4,"拦截实操"),
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

    public static JyExpSourceEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JyExpSourceEnum value : JyExpSourceEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
