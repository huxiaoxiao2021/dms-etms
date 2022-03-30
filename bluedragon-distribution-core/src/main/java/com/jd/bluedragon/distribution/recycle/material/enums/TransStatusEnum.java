package com.jd.bluedragon.distribution.recycle.material.enums;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TransStatusEnum
 * @date 2018/11/29
 */
public enum TransStatusEnum {
    /**
     * 在库
     */
    AT_THE_SITE(1, "在库"),

    /**
     * 在途
     */
    ON_THE_WAY(2, "在途");

    private int code;

    private String name;

    TransStatusEnum() {
    }

    TransStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TransStatusEnum getEnum(int code) {
        for (TransStatusEnum statusEnum : TransStatusEnum.values()) {
            if (code == statusEnum.getCode()) {
                return statusEnum;
            }
        }
        return null;
    }

}

