package com.jd.bluedragon.common.dto.router.dynamicLine.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态线路启用状态枚举
 *
 * @author fanggang7
 * @time 2024-04-02 18:09:34 周二
 */
public enum RouterDynamicLineStatusEnum {

    /**
     * 待启用
     */
    DEFAULT(0, "待启用"),
    /**
     * 启用
     */
    ENABLE(1, "启用中"),

    /**
     * 已失效
     */
    DISABLE(-1, "已失效"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        // 将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (RouterDynamicLineStatusEnum enumItem : RouterDynamicLineStatusEnum.values()) {
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

    RouterDynamicLineStatusEnum(Integer code, String name) {
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
