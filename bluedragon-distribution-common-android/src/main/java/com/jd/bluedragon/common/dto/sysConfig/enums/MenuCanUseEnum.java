package com.jd.bluedragon.common.dto.sysConfig.enums;

/**
 * 可用类型枚举
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 21:21:56 周一
 */
public enum MenuCanUseEnum {
    /**
     * 不可用
     */
    DISABLE(0, "不可用"),
    /**
     * 可用
     */
    ENABLE(1, "可用"),
    ;

    private Integer code;

    private String name;

    MenuCanUseEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
