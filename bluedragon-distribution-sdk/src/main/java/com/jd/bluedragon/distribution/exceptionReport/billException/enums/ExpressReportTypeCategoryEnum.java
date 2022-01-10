package com.jd.bluedragon.distribution.exceptionReport.billException.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常面单举报，举报类型分类枚举
 *
 * @author fanggang7
 * @time 2022-01-04 11:05:15 周二
 */
public enum ExpressReportTypeCategoryEnum {

    /**
     * 操作问题
     */
    OP_PROBLEM(1, "操作问题"),

    /**
     * 设备问题
     */
    EQUIPMENT_PROBLEM(2, "设备问题"),

    /**
     * 模板问题
     */
    TEMPLATE_PROBLEM(3, "模板问题"),

    /**
     * 系统问题
     */
    SYSTEM_PROBLEM(4, "系统问题"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (ExpressReportTypeCategoryEnum enumItem : ExpressReportTypeCategoryEnum.values()) {
            ENUM_MAP.put(enumItem.getCode(), enumItem.getName());
            ENUM_LIST.add(enumItem.getCode());
        }
    }

    /**
     * 通过code获取name
     *
     * @param code 编码
     * @return string
     */
    public static String getEnumNameByCode(Integer code) {
        return ENUM_MAP.get(code);
    }

    ExpressReportTypeCategoryEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
