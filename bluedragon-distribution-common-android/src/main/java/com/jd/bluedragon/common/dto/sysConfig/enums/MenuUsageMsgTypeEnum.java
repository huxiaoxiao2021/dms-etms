package com.jd.bluedragon.common.dto.sysConfig.enums;

/**
 * 菜单可用性提示展示类型枚举
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 21:22:24 周一
 */
public enum MenuUsageMsgTypeEnum {

    /**
     * toast
     */
    TOAST(1, "toast"),
    /**
     * dialog
     */
    DIALOG(2, "可用"),
    ;

    private Integer code;

    private String name;

    MenuUsageMsgTypeEnum(Integer code, String name) {
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
