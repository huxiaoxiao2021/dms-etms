package com.jd.bluedragon.distribution.half.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 配送结果
 *
 * 1-妥投，2-拒收
 *
 */
public enum PackageHalfResultTypeEnum {

    DELIVERED_1("1","妥投"),
    REJECT_2("2","拒收");

    private String code;
    private String name;
    private static Map<String, PackageHalfResultTypeEnum> codeMap;
    public static Map<String, String> ArBookingSpaceTransportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, PackageHalfResultTypeEnum>();
        ArBookingSpaceTransportTypeMap = new HashMap<String, String>();
        for (PackageHalfResultTypeEnum _enum : PackageHalfResultTypeEnum.values()) {
            codeMap.put(_enum.getCode(), _enum);
            ArBookingSpaceTransportTypeMap.put(_enum.getCode(), _enum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static PackageHalfResultTypeEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static PackageHalfResultTypeEnum getEnumByValue(String value) {
        Set<Map.Entry<String, String>> entries = ArBookingSpaceTransportTypeMap.entrySet();
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
        PackageHalfResultTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    PackageHalfResultTypeEnum(String code, String name) {
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
