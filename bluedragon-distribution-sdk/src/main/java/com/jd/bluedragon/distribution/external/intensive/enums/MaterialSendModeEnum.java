package com.jd.bluedragon.distribution.external.intensive.enums;

public enum MaterialSendModeEnum {

    WARM_BOX_SEND((byte)1, "保温箱发货"),

    TYPE_BATCH_SEND((byte)2, "物资按类型批量发货"),
    MATERIAL_TAG_SEND((byte)3,"物资按标签发货"),

    COLLECTION_BAG_SEND((byte)4, "集包袋发货");

    private byte code;

    private String name;

    MaterialSendModeEnum(byte code, String name) {
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
