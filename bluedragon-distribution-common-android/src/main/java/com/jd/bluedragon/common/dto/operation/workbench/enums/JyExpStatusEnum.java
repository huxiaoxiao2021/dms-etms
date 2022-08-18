package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum JyExpStatusEnum {
    TO_PICK(0,"待取件"),
    TO_PROCESS(1,"待处理"),
    TO_PRINT(2,"待打印"),
    COMPLATE(3,"已完成"),
    ;


    private final int code;

    private final String text;

    JyExpStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static JyExpStatusEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JyExpStatusEnum value : JyExpStatusEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}
