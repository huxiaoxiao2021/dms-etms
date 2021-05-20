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
     * 液体或膏状
     */
    LIQUID_OR_PASTE(1, "违禁品-含液体或膏状","10,40,60"),

    /**
     * 粉末
     */
    POWDER(2, "违禁品-含粉末","10,40,60"),

    /**
     * 电池
     */
    BATTERY(3, "违禁品-含电池","10,40,60"),

    /**
     * 磁
     */
    MAGNET(4, "违禁品-含磁","10,40,60"),

    /**
     * 危险品
     */
    DANGEROUS_GOODS(5, "违禁品-危险品","10,40,60"),

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

    /**
     * 获取所有带违禁品标识的违禁品原因
     * @return
     */
    public static List<Integer> getContrabandFlagReason(){
        List<Integer> list = new ArrayList<Integer>();
        list.add(LIQUID_OR_PASTE.getCode());
        list.add(POWDER.getCode());
        list.add(BATTERY.getCode());
        list.add(MAGNET.getCode());
        list.add(DANGEROUS_GOODS.getCode());
        return list;
    }
}
