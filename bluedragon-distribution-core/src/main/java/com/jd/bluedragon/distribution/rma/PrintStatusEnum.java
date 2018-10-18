package com.jd.bluedragon.distribution.rma;

public enum PrintStatusEnum {

    /**
     * 已打印
     */
    HAD_PRINTED(1, "已打印"),
    /**
     * 未打印
     */
    NOT_PRINTED(0, "未打印");

    private int code;

    private String name;

    PrintStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PrintStatusEnum getEnum(int code) {
        for (PrintStatusEnum type : PrintStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
