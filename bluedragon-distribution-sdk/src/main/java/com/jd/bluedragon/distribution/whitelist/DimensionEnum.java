package com.jd.bluedragon.distribution.whitelist;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2020/3/18 22:39
 */
public enum DimensionEnum {
    PERSON(1,"个人"),
    SITE(2,"场地"),
    NATIONAL(3,"全国");

    private int code;
    private String name;

    public static Map<Integer, DimensionEnum> codeMap;
    public static Map<Integer, String> dimensionEnumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, DimensionEnum>();
        dimensionEnumMap = new HashMap<Integer, String>();

        for (DimensionEnum _enum : DimensionEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            dimensionEnumMap.put(_enum.getCode(),_enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     */
    public static DimensionEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    DimensionEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
