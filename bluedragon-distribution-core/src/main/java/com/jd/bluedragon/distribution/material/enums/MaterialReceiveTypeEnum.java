package com.jd.bluedragon.distribution.material.enums;

/**
 * @ClassName MaterialReceiveTypeEnum
 * @Description 物资收货方式
 * @Author wyh
 * @Date 2020/2/27 10:41
 **/
public enum MaterialReceiveTypeEnum {

    RECEIVE_BY_SINGLE_MATERIAL((byte)1, "按物资单个收货"),

    RECEIVE_BY_CONTAINER((byte)2, "按容器收货");

    private byte code;

    private String name;

    MaterialReceiveTypeEnum(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
