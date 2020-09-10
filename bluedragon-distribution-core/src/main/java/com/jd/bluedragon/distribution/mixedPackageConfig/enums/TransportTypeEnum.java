package com.jd.bluedragon.distribution.mixedPackageConfig.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 承运类型枚举
 * Created by zhangleqi on 2017/8/25
 */
public enum TransportTypeEnum {

    //枚举值
    AIR_TRANSPORT(1, "航空运输"),
    HIGHWAY_TRANSPORT(2, "公路运输");

    private Integer code;
    private String name;
    private static Map<Integer, TransportTypeEnum> codeMap;
    public static Map<Integer, String> transportTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, TransportTypeEnum>();
        transportTypeMap = new HashMap<Integer, String>();
        for (TransportTypeEnum transportTypeEnum : TransportTypeEnum.values()) {
            codeMap.put(transportTypeEnum.getCode(), transportTypeEnum);
            transportTypeMap.put(transportTypeEnum.getCode(), transportTypeEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 承运类型
     */
    public static TransportTypeEnum getTransportTypeEnumByKey(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取承运类型名称
     *
     * @param code 编码
     * @return 承运类型
     */
    public static String getNameByKey(Integer code) {
        TransportTypeEnum transportTypeEnum = codeMap.get(code);
        if (transportTypeEnum != null) {
            return transportTypeEnum.getName();
        }
        return "未知承运类型";
    }

    TransportTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

//    public boolean equals(Integer code) {
//        return this.getCode().equals(code);
//    }


    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getCode() + "-" + this.getName();
    }


}
