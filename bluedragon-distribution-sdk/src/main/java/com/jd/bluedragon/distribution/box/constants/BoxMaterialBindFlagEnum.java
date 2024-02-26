package com.jd.bluedragon.distribution.box.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 箱号物资绑定关系枚举
 *
 * @author fanggang7
 * @time 2024-02-24 09:58:15 周六
 */
public enum BoxMaterialBindFlagEnum {

    /**
     * 绑定
     */
    BIND(1, "绑定"),
    /**
     * 解绑
     */
    UNBIND(2, "解绑"),
    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (BoxMaterialBindFlagEnum enumItem : BoxMaterialBindFlagEnum.values()) {
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

    BoxMaterialBindFlagEnum(Integer code, String name) {
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
