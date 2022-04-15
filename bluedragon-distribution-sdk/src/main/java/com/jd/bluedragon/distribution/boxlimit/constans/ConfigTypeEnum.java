package com.jd.bluedragon.distribution.boxlimit.constans;

import java.util.HashMap;
import java.util.Map;

/**
 * 集箱包裹配置枚举
 */
public enum ConfigTypeEnum {

    COMMON_CONFIG_TYPE(1,"通用配置类型"),
    SITE_CONFIG_TYPE(2,"场地配置类型");

    private Integer code;

    private String name;

    ConfigTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.getCode() + "-" + this.getName();
    }

    public static ConfigTypeEnum getFromCode(String code) {
        for (ConfigTypeEnum item : ConfigTypeEnum.values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static Map<Integer,String> getMap(){
        Map<Integer,String> result = new HashMap<Integer, String>();
        for(ConfigTypeEnum configTypeEnum : ConfigTypeEnum.values()){
            result.put(configTypeEnum.getCode(),configTypeEnum.getName());
        }
        return result;
    }
}
