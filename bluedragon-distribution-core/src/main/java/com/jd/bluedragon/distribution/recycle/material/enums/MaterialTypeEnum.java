package com.jd.bluedragon.distribution.recycle.material.enums;

/**
 * @author lixin39
 * @Description 物资类型枚举
 * @ClassName MaterialTypeEnum
 * @date 2018/11/29
 */
public enum MaterialTypeEnum {
    /**
     * 笼车
     */
    TROLLEY(1, "笼车"),

    /**
     * 托盘
     */
    TRAY(2, "托盘"),

    /**
     * 保温箱
     */
    COOLER_BOX(3, "保温箱"),

    /**
     * 青流箱
     */
    BLUE_FLOW_BOX(4, "青流箱"),

    /**
     * 周转筐
     */
    BASKET(5, "周转筐")
    ;

    private int code;

    private String name;

    MaterialTypeEnum() {
    }

    MaterialTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MaterialTypeEnum getEnum(int code) {
        for (MaterialTypeEnum typeEnum : MaterialTypeEnum.values()) {
            if (code == typeEnum.getCode()) {
                return typeEnum;
            }
        }
        return null;
    }
}
