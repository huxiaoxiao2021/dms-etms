package com.jd.bluedragon.distribution.collect.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum CollectGoodsPlaceTypeEnum {
    SMALL_TYPE_1("1","小单"),
    MIDDLE_TYPE_2("2","中单"),
    BIG_TYPE_3("3","大单"),
    BIG_TYPE_4("4","异常");

    private String code;
    private String name;
    private static Map<String, CollectGoodsPlaceTypeEnum> codeMap;
    public static Map<String, String> typeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, CollectGoodsPlaceTypeEnum>();
        typeMap = new HashMap<String, String>();
        for (CollectGoodsPlaceTypeEnum _enum : CollectGoodsPlaceTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            typeMap.put(_enum.getCode(), _enum.getName());
        }
    }



    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static CollectGoodsPlaceTypeEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static CollectGoodsPlaceTypeEnum getEnumByValue(String value) {
        Set<Map.Entry<String, String>> entries = typeMap.entrySet();
        for(Map.Entry<String, String> entry : entries){
            if(entry.getValue().equals(value)){
                return codeMap.get(entry.getKey());
            }
        }
        return null;
    }
    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByKey(String code) {
        CollectGoodsPlaceTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    CollectGoodsPlaceTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }


}
