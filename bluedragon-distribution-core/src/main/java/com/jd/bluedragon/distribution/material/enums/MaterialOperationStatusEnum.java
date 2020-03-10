package com.jd.bluedragon.distribution.material.enums;

/**
 * @ClassName MaterialOperationStatusEnum
 * @Description
 * @Author wyh
 * @Date 2020/2/28 14:39
 **/
public enum MaterialOperationStatusEnum {

    INBOUND((byte)1, "已出库未出库"),

    OUTBOUND((byte)2, "已出库");

    private byte code;

    private String name;

    MaterialOperationStatusEnum(byte code, String name) {
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
