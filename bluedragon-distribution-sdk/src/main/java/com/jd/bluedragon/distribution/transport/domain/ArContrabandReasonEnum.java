package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author lixin39
 * @Description 违禁品原因枚举值
 * @ClassName ArContrabandReasonEnum
 * @date 2019/7/10
 */
public enum ArContrabandReasonEnum {
    /**
     * 液体或膏状
     */
    LIQUID_OR_PASTE(1, "含液体或膏状"),

    /**
     * 气体
     */
    GAS(2, "含气体"),

    /**
     * 粉末
     */
    POWDER(3, "含粉末"),

    /**
     * 电池
     */
    BATTERY(4, "含电池"),

    /**
     * 磁
     */
    MAGNET(5, "含磁"),

    /**
     * 危险品
     */
    DANGEROUS_GOODS(6, "危险品"),

    /**
     * 其他
     */
    OTHER(7, "其他");

    private Integer code;

    private String desc;

    ArContrabandReasonEnum() {
    }

    ArContrabandReasonEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ArContrabandReasonEnum getEnum(Integer code) {
        for (ArContrabandReasonEnum reason : ArContrabandReasonEnum.values()) {
            if (reason.getCode().equals(code)) {
                return reason;
            }
        }
        return null;
    }
}
