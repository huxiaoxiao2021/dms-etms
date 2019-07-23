package com.jd.bluedragon.distribution.transport.domain;

/**
 * @author lixin39
 * @Description 异常原因
 * @ClassName ArAbnormalReasonEnum
 * @date 2019/7/10
 */
public enum ArAbnormalReasonEnum {

    /**
     * 违禁品
     */
    CONTRABAND_GOODS(10, "违禁品", 10000001),

    /**
     * 航班异常
     */
    FLIGHT_ABNORMAL(20, "航班异常"),

    /**
     * 天气原因
     */
    WEATHER_ISSUES(30, "天气原因"),

    /**
     * 其他
     */
    OTHER(40, "其他");

    private Integer code;

    private String desc;

    private Integer id;

    ArAbnormalReasonEnum() {
    }

    ArAbnormalReasonEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    ArAbnormalReasonEnum(Integer code, String desc, Integer id) {
        this.code = code;
        this.desc = desc;
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getId() {
        return id;
    }

    public static ArAbnormalReasonEnum getEnum(Integer code) {
        for (ArAbnormalReasonEnum reason : ArAbnormalReasonEnum.values()) {
            if (reason.getCode().equals(code)) {
                return reason;
            }
        }
        return null;
    }
}
