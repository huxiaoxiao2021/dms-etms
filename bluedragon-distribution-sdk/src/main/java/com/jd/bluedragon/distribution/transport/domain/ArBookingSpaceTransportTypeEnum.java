package com.jd.bluedragon.distribution.transport.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 订舱登记表 运力类型枚举
 *
 * 1-散航，2-全货机，3-铁路
 *
 */
public enum ArBookingSpaceTransportTypeEnum {

    BULK_AIRCRAFT_1("1","散航"),
    ALL_CARGO_AIRCRAFT_2("2","全货机"),
    RAILWAY_3("3","铁路");

    private String code;
    private String name;
    private static Map<String, ArBookingSpaceTransportTypeEnum> codeMap;
    public static Map<String, String> ArBookingSpaceTransportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, ArBookingSpaceTransportTypeEnum>();
        ArBookingSpaceTransportTypeMap = new HashMap<String, String>();
        for (ArBookingSpaceTransportTypeEnum _enum : ArBookingSpaceTransportTypeEnum.values()) {
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
    public static ArBookingSpaceTransportTypeEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static ArBookingSpaceTransportTypeEnum getEnumByValue(String value) {
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
        ArBookingSpaceTransportTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    ArBookingSpaceTransportTypeEnum(String code, String name) {
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
