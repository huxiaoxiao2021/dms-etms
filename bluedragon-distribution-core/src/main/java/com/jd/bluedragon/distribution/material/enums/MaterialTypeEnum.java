package com.jd.bluedragon.distribution.material.enums;

/**
 * @ClassName MaterialTypeEnum
 * @Description 物资类型
 * @Author wyh
 * @Date 2020/2/27 9:29
 **/
public enum MaterialTypeEnum {

    WARM_BOX((byte)1, "保温箱"),

    TAG_NO((byte)2,"封签号");

    private byte code;

    private String name;

    MaterialTypeEnum(byte code, String name) {
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
