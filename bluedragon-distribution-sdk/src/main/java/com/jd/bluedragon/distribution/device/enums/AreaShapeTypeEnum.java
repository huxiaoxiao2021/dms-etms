package com.jd.bluedragon.distribution.device.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 围栏形状类型枚举
 *
 * @author fanggang7
 * @time 2022-12-02 20:01:55 周四
 */
public enum AreaShapeTypeEnum {

    /**
     * 多边形
     */
    POLYGON(2, "多边形"),

    ;

    public static Map<Integer, String> ENUM_MAP;

    public static List<Integer> ENUM_LIST;

    private Integer code;

    private String name;

    static {
        //将所有枚举装载到map中
        ENUM_MAP = new HashMap<Integer, String>();
        ENUM_LIST = new ArrayList<Integer>();
        for (AreaShapeTypeEnum enumItem : AreaShapeTypeEnum.values()) {
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

    AreaShapeTypeEnum(Integer code, String name) {
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
