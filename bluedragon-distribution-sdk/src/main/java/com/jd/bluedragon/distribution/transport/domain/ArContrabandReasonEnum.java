package com.jd.bluedragon.distribution.transport.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lixin39
 * @Description 违禁品原因枚举值
 * @ClassName ArContrabandReasonEnum
 * @date 2019/7/10
 */
public enum ArContrabandReasonEnum {
    /**
     * 政府航空管制
     */
    GOVERNMENT_AVIATION_CONTROL(6, "政府航空管制","10,40"),

    /**
     * 航空停运
     */
    AIR_OUTAGE(7, "航空停运","10,40"),

    /**
     * 航空舱位不足
     */
    AIR_NO_CABIN(8, "航空舱位不足","10,40"),

    /**
     * 包装不符合空运标准
     */
    AIR_FAILURE(9, "包装不符合空运标准","10,40"),

    /**
     * 生鲜无检疫证明
     */
    AIR_QUARANTINE(10, "生鲜无检疫证明","10,40"),

    /**
     * 铁路停运
     */
    RAIL_OUTAGE(11, "铁路停运","60"),

    /**
     * 铁路舱位不足
     */
    RAIL_NO_CABIN(12, "铁路舱位不足","60");


    /**
     * 枚举编码
     */
    private Integer code;

    /**
     * 枚举说明
     */
    private String desc;

    /**
     * 适用的变更方式
     * 10：航空转陆运
     * 40：航空转高铁
     * 60：铁路转公路
     */
    private String transpondTypes;

    ArContrabandReasonEnum() {
    }

    ArContrabandReasonEnum(Integer code, String desc,String transpondTypes) {
        this.code = code;
        this.desc = desc;
        this.transpondTypes= transpondTypes;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getTranspondTypes() {
        return transpondTypes;
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
