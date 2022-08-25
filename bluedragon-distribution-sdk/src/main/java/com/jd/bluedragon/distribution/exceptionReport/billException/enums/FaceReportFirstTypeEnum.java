package com.jd.bluedragon.distribution.exceptionReport.billException.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 面单异常举报一级举报类型
 *
 * @author hujiping
 * @date 2022/6/10 3:51 PM
 */
public enum FaceReportFirstTypeEnum {

    OP_PROBLEM(1, "操作问题"),
    EQUIPMENT_PROBLEM(2, "设备问题"),
    TEMPLATE_PROBLEM(3, "模板问题"),
    SYSTEM_PROBLEM(4, "系统问题"),
    ;

    public static final Map<Integer, String> ENUM_MAP;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        for (ExpressReportTypeCategoryEnum enumItem : ExpressReportTypeCategoryEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
        }
    }

    FaceReportFirstTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

    private Integer code;

    private String name;

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
}
