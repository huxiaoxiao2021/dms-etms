package com.jd.bluedragon.distribution.jy.enums;

/**
 * @author pengchong28
 * @description 装车评价记录申诉状态
 * @date 2024/3/6
 */
public enum EvaluateAppealStatusEnum {
    NO_APPEAL(0,"未申诉"),
    NO_APPEAL_AFTER_TIMEOUT(1,"超时未申诉"),
    PENDING_REVIEW(2,"待审核"),
    REVIEWED(3,"已审核"),
    TIMED_OUT_AND_NOT_REVIEWED(4,"超时未审核");
    private Integer code;
    private String name;

    EvaluateAppealStatusEnum(Integer code, String name) {
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
        for (EvaluateAppealStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e.getName();
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
