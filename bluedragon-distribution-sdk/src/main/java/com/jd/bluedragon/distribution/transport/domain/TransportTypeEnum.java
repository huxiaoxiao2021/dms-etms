package com.jd.bluedragon.distribution.transport.domain;

/**
 * <p>
 * Created by lixin39 on 2018/1/3.
 */
public enum TransportTypeEnum {
    /**
     * 空运
     */
    AIR_TRANSPORT(1, "空运"),
    /**
     * 铁路
     */
    RAILWAY(2, "铁路");

    private int code;

    private String name;

    TransportTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

    public static TransportTypeEnum getEnum(int code) {
        for (TransportTypeEnum type : TransportTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

}
