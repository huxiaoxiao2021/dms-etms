package com.jd.bluedragon.distribution.collect.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum  CollectGoodsPlaceStatusEnum {
    FREE_0("0","空闲"),
    NOT_FREE_1("1","非空闲"),
    FULL_2("2","已满");


    private String code;
    private String name;
    private static Map<String, CollectGoodsPlaceStatusEnum> codeMap;
    public static Map<String, String> typeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, CollectGoodsPlaceStatusEnum>();
        typeMap = new HashMap<String, String>();
        for (CollectGoodsPlaceStatusEnum _enum : CollectGoodsPlaceStatusEnum.values()) {
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
    public static CollectGoodsPlaceStatusEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static CollectGoodsPlaceStatusEnum getEnumByValue(String value) {
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
        CollectGoodsPlaceStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    CollectGoodsPlaceStatusEnum(String code, String name) {
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
