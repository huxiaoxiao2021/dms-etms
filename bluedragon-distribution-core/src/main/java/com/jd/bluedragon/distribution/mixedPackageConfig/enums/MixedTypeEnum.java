package com.jd.bluedragon.distribution.mixedPackageConfig.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *  混装类型枚举
 * Created by zhangleqi on 2017/8/25
 */
public enum MixedTypeEnum {

    //枚举值
    UNMIXED(0, "不允许混装"),
    MIXED(1, "允许混装");
    private Integer code;
    private String name;
    private static Map<Integer, MixedTypeEnum> codeMap;
    public static Map<Integer, String> mixedTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, MixedTypeEnum>();
        mixedTypeMap = new HashMap<Integer, String>();
        for (MixedTypeEnum mixedTypeEnum : MixedTypeEnum.values()) {
            codeMap.put(mixedTypeEnum.getCode(), mixedTypeEnum);
            mixedTypeMap.put(mixedTypeEnum.getCode(), mixedTypeEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 混装类型枚举
     */
    public static MixedTypeEnum getmixedTypeEnumByKey(String code) {
        return codeMap.get(Integer.parseInt(code));
    }

    /**
     * 通过编码获取混装类型名称
     *
     * @param code 编码
     * @return 混装类型名称
     */
    public static String getNameByKey(String code) {
        MixedTypeEnum mixedTypeEnum = codeMap.get(Integer.parseInt(code));
        if (mixedTypeEnum != null) {
            return mixedTypeEnum.getName();
        }
        return "未知混装类型";
    }

    MixedTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

//    public boolean equals(String code) {
//        return this.getCode().equals(code);
//    }


    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }


}
