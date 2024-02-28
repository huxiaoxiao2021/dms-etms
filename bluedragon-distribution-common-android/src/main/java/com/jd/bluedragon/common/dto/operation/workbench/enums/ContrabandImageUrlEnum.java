package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum ContrabandImageUrlEnum {
    WAYBILL_IMAGE_TYPE(3, "面单全景"),
    PANORAMA_IMAGE_TYPE(7, "货物全景"),
    CONTRABAND_IMAGE_TYPE(15, "差错图片");


    private final int code;

    private final String desc;

    ContrabandImageUrlEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
