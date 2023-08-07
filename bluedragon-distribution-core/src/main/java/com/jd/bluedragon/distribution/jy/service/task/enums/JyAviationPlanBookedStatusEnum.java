package com.jd.bluedragon.distribution.jy.service.task.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * 拣运发货航空计划订舱类型
 */
public enum JyAviationPlanBookedStatusEnum {
//    todo zcf 订舱状态枚举运输系统暂时未给,临时定义
    ADD(1, "航空计划生成"),
    UPDATE(2,"航空计划变更"),
    DISCARD(3, "航空计划废弃"),
    ;
    private Integer code;

    private String desc;


    public static Map<Integer, JyAviationPlanBookedStatusEnum> ENUM_MAP;


    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<>();
        for (JyAviationPlanBookedStatusEnum enumItem : JyAviationPlanBookedStatusEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem);
        }
    }

    public static JyAviationPlanBookedStatusEnum getBookedStatusEnumByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

    JyAviationPlanBookedStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
