package com.jd.bluedragon.distribution.inspection;

/**
 * 验货操作类型枚举
 */
public enum InspectionOperateTypeEnum {
    PACK(1,"按包裹验货"),
    WAYBILL(2,"按运单验货"),
    BOX(3,"按箱验货");

    private final int code;
    private final String name;

    InspectionOperateTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
