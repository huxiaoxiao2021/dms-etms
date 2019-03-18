package com.jd.bluedragon.distribution.mixedPackageConfig.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则类型枚举
 * Created by zhangleqi on 2017/8/25
 */
public enum YNEnum {

    //枚举值
    Y("1", "生效"),
    N("0", "失效");

    private String code;
    private String name;
    private static Map<String, YNEnum> codeMap;
    public static Map<String, String> YNMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, YNEnum>();
        YNMap = new HashMap<String, String>();
        for (YNEnum yNEnum : YNEnum.values()) {
            codeMap.put(yNEnum.getCode(), yNEnum);
            YNMap.put(yNEnum.getCode(), yNEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static YNEnum getYNEnumByKey(String code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByKey(String code) {
        YNEnum YNEnum = codeMap.get(code);
        if (YNEnum != null) {
            return YNEnum.getName();
        }
        return "未知规则类型";
    }

    YNEnum(String code, String name) {
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
