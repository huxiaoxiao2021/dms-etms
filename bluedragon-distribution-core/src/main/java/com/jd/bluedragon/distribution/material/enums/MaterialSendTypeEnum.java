package com.jd.bluedragon.distribution.material.enums;

/**
 * @ClassName MaterialSendTypeEnum
 * @Description 物资发货方式
 * @Author wyh
 * @Date 2020/2/27 10:41
 **/
public enum MaterialSendTypeEnum {

    SEND_BY_SINGLE_MATERIAL((byte)1, "按物资单个发货"),

    SEND_BY_CONTAINER((byte)2, "按容器发货"),

    SEND_BY_BATCH((byte)3, "按批次发货");

    private byte code;

    private String name;

    MaterialSendTypeEnum(byte code, String name) {
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
