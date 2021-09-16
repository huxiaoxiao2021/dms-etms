package com.jd.bluedragon.distribution.rule.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/2/20 17:53
 * @Description:
 * 分拣规则枚举
 */
public enum SortingRuleTypeEnum {

    RULE_1121("1121","跨区校验"),
    RULE_1120("1120","跨分拣校验"),
    RULE_1125("1125","混装箱规则"),
    RULE_1122("1122","路由校验");


    private String code;
    private String name;
    private static Map<String, SortingRuleTypeEnum> codeMap;
    public static Map<String, String> typeMap;

    static {
        //将所有枚举装载到map中
        codeMap = new HashMap<String, SortingRuleTypeEnum>();
        typeMap = new HashMap<String, String>();
        for (SortingRuleTypeEnum _enum : SortingRuleTypeEnum.values()) {
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
    public static SortingRuleTypeEnum getEnumByKey(String code) {
        return codeMap.get(code);
    }


    public static SortingRuleTypeEnum getEnumByValue(String value) {
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
        SortingRuleTypeEnum _enum = codeMap.get(code);
        if (_enum != null) {
            return _enum.getName();
        }
        return "未知规则类型";
    }

    SortingRuleTypeEnum(String code, String name) {
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
