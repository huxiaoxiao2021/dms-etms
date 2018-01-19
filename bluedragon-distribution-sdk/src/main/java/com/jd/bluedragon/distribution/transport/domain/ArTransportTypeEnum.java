package com.jd.bluedragon.distribution.transport.domain;

/**
 * <p>
 * Created by lixin39 on 2018/1/3.
 */
public enum ArTransportTypeEnum {
    /**
     * 航空
     */
    AIR_TRANSPORT(1, "航空"),
    /**
     * 铁路
     */
    RAILWAY(2, "铁路");

    private int code;

    private String name;

    ArTransportTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public static ArTransportTypeEnum getEnum(int code) {
        for (ArTransportTypeEnum type : ArTransportTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

}
