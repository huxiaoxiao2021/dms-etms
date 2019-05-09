package com.jd.bluedragon.distribution.notice.utils;

/**
 * @author lixin39
 * @Description 通知级别枚举
 * @ClassName NoticeLevelEnum
 * @date 2019/4/17
 */
public enum NoticeLevelEnum {

    /**
     * 普通级别
     */
    NORMAL(1, "普通"),

    /**
     * 重要级别
     */
    IMPORTANT(2, "重要"),

    /**
     * 严重级别
     */
    SERIOUS(3, "严重");

    private int code;

    private String name;

    NoticeLevelEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * 根据编码获取通知级别信息
     *
     * @param code
     * @return
     */
    public static NoticeLevelEnum getEnum(int code) {
        for (NoticeLevelEnum level : NoticeLevelEnum.values()) {
            if (level.getCode() == code) {
                return level;
            }
        }
        return null;
    }
}
