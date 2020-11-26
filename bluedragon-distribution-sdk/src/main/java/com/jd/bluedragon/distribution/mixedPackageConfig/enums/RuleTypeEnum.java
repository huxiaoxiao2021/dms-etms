package com.jd.bluedragon.distribution.mixedPackageConfig.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 规则类型枚举
 * Created by zhangleqi on 2017/8/25
 */
public enum RuleTypeEnum {

    //枚举值
    BUILD_PACKAGE(1, "建包"),
    SEND_GOODS(2, "发货");

    private Integer code;
    private String name;
    private static Map<Integer, RuleTypeEnum> codeMap;
    public static Map<Integer, String> ruleTypeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<Integer, RuleTypeEnum>();
        ruleTypeMap = new HashMap<Integer, String>();
        for (RuleTypeEnum ruleTypeEnum : RuleTypeEnum.values()) {
            codeMap.put(ruleTypeEnum.getCode(), ruleTypeEnum);
            ruleTypeMap.put(ruleTypeEnum.getCode(), ruleTypeEnum.getName());
        }
    }

    /**
     * 通过编码获取枚举
     *
     * @param code 编码
     * @return 规则类型
     */
    public static RuleTypeEnum getruleTypeEnumByKey(Integer code) {
        return codeMap.get(code);
    }

    /**
     * 通过编码获取规则类型名称
     *
     * @param code 编码
     * @return 规则类型
     */
    public static String getNameByKey(Integer code) {
        RuleTypeEnum ruleTypeEnum = codeMap.get(code);
        if (ruleTypeEnum != null) {
            return ruleTypeEnum.getName();
        }
        return "未知规则类型";
    }

    RuleTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

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
