package com.jd.bluedragon.distribution.businessIntercept.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截报表用到的操作维度枚举
 *
 * @author fanggang7
 * @time 2020-12-11 10:50:24 周五
 */
public enum BusinessInterceptOperateDimensionTypeEnum {

    /**
     * 包裹
     */
    PACKAGE(1, "包裹"),
    /**
     * 运单
     */
    WAYBILL(2, "运单"),
    /**
     * 离线
     */
    BOX(3, "箱"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BusinessInterceptOperateDimensionTypeEnum enumItem : BusinessInterceptOperateDimensionTypeEnum.values()) {
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

    BusinessInterceptOperateDimensionTypeEnum(Integer code, String name) {
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
