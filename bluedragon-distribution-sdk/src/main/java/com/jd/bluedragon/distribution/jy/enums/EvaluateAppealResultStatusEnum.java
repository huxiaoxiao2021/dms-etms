package com.jd.bluedragon.distribution.jy.enums;

/**
 * @author pengchong28
 * @description 装车评价申诉结果状态
 * @date 2024/3/7
 */
public enum EvaluateAppealResultStatusEnum {
    NO_HANDLE(0,"未处理"),
    PASS(1,"通过"),
    REJECT(2,"驳回");
    private Integer code;
    private String name;

    EvaluateAppealResultStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (EvaluateAppealResultStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e.getName();
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
