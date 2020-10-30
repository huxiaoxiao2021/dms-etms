package com.jd.bluedragon.distribution.whitelist;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lijie
 * @date 2020/3/18 22:39
 */
public enum DimensionEnum {
    PERSON(1,"个人","ALL_MAIL_CACHE_KEY_ONE_"),
    SITE(2,"场地","ALL_MAIL_CACHE_KEY_"),
    NATIONAL(3,"全国","ALL_MAIL_CACHE_KEY_ALL_COUNTRY");

    private int code;
    private String name;
    private String cachePreKey;

    public static Map<Integer, DimensionEnum> codeMap;
    public static Map<Integer, String> dimensionEnumMap;
    public static Map<Integer,String> cacheKeyEnumMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, DimensionEnum>();
        dimensionEnumMap = new HashMap<Integer, String>();
        cacheKeyEnumMap = new HashMap<Integer, String>();

        for (DimensionEnum _enum : DimensionEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            dimensionEnumMap.put(_enum.getCode(),_enum.getName());
            cacheKeyEnumMap.put(_enum.getCode(),_enum.getCachePreKey());
        }
    }

    /**
     * 通过编码获取枚举
     */
    public static DimensionEnum getEnumByKey(int code) {
        return codeMap.get(code);
    }

    DimensionEnum(int code, String name,String cachePreKey) {
        this.code = code;
        this.name = name;
        this.cachePreKey=cachePreKey;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCachePreKey() {
        return cachePreKey;
    }

    public void setCachePreKey(String cachePreKey) {
        this.cachePreKey = cachePreKey;
    }
}
