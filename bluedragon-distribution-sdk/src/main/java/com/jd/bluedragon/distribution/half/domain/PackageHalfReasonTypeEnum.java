package com.jd.bluedragon.distribution.half.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 *  包裹半收 拒收原因
 *
 * 1-破损 ,2- 丢失,3- 报废 ,4-客户原因,5- 其他
 *
 */
public enum PackageHalfReasonTypeEnum {

    BREAKAGE_1("1","破损"),
    LOSS_2("2","丢失"),
    SCRAP_3("3","报废"),
    CUSTOMER_4("4","客户原因"),
    OTHER_3("5","其他");

    private String code;
    private String name;
    private static Map<String, PackageHalfReasonTypeEnum> codeMap;
    public static Map<String, String> ArBookingSpaceTransportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, PackageHalfReasonTypeEnum>();
        ArBookingSpaceTransportTypeMap = new HashMap<String, String>();
        for (PackageHalfReasonTypeEnum _enum : PackageHalfReasonTypeEnum.values()) {
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
    public static PackageHalfReasonTypeEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static PackageHalfReasonTypeEnum getEnumByValue(String value) {
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
        PackageHalfReasonTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    PackageHalfReasonTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean equals(String code) {
        return this.getCode().equals(code);
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
