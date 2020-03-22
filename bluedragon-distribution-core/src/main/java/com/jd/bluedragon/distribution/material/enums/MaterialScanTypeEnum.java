package com.jd.bluedragon.distribution.material.enums;

/**
 * @ClassName MaterialScanTypeEnum
 * @Description
 * @Author wyh
 * @Date 2020/2/28 14:40
 **/
public enum MaterialScanTypeEnum {

    INBOUND((byte)1, "入库"),

    OUTBOUND((byte)2, "出库");

    private byte code;

    private String name;

    MaterialScanTypeEnum(byte code, String name) {
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
