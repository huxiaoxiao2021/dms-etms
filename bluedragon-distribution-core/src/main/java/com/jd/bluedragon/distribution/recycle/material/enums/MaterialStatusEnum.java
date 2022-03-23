package com.jd.bluedragon.distribution.recycle.material.enums;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName MaterialStatusEnum
 * @date 2018/11/29
 */
public enum MaterialStatusEnum {
    /**
     * 正常
     */
    NORMAL(1, "正常"),

    /**
     * 维修
     */
    SERVICE(2, "维修"),

    /**
     * 报废
     */
    SCRAPPED(3, "报废");

    private int code;

    private String name;

    MaterialStatusEnum() {
    }

    MaterialStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static MaterialStatusEnum getEnum(int code) {
        for (MaterialStatusEnum statusEnum : MaterialStatusEnum.values()) {
            if (code == statusEnum.getCode()) {
                return statusEnum;
            }
        }
        return null;
    }
}
