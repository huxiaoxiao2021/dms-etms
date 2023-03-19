package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/19 11:21
 * @Description:
 */
public enum JyExpSaveTypeEnum {

    TEMP_SAVE(0,"暂存"),
    SAVE(1,"保存")
    ;


    private final Integer code;

    private final String text;

    JyExpSaveTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static JyExpSaveTypeEnum getEnumByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JyExpSaveTypeEnum value : JyExpSaveTypeEnum.values()) {
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }

}
