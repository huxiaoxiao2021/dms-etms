package com.jd.bluedragon.distribution.jy.dto.evaluate;

public enum EvaluateImgTypeEnum {
    LOAD(1, "装车"),
    UNLOAD_BEFORE(2, "卸车前"),
    UNLOAD(3, "卸车中"),
    ;
    private Integer code;
    private String desc;
    EvaluateImgTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
