package com.jd.bluedragon.distribution.jy.enums;

/**
 * 卸车模式
 **/
public enum UnloadCarTypeEnum {

    MANUAL_TYPE(1, "人工"),
    PIPELINE_TYPE(0, "流水线");

    private Integer code;
    private String desc;

    UnloadCarTypeEnum() {
    }

    UnloadCarTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
