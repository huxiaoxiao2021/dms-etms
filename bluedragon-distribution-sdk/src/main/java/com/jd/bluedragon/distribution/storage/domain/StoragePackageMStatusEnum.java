package com.jd.bluedragon.distribution.storage.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * 暂存主表 状态枚举
 *
 * 1-已上架，2-可发货，3-强制可发货，4-已发货
 *
 */
public enum StoragePackageMStatusEnum {

    PUTAWAY_1("1","已上架"),
    CAN_SEND_2("2","可发货"),
    FORCE_SEND_3("3","强制可发货"),
    SEND_4("4","已发货");
    private String code;
    private String name;
    private static Map<String, StoragePackageMStatusEnum> codeMap;
    public static Map<String, String> ArBookingSpaceTransportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, StoragePackageMStatusEnum>();
        ArBookingSpaceTransportTypeMap = new HashMap<String, String>();
        for (StoragePackageMStatusEnum _enum : StoragePackageMStatusEnum.values()) {
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
    public static StoragePackageMStatusEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static StoragePackageMStatusEnum getEnumByValue(String value) {
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
        StoragePackageMStatusEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    StoragePackageMStatusEnum(String code, String name) {
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
