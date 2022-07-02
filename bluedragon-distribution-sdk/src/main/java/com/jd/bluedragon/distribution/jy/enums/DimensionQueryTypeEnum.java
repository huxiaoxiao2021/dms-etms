package com.jd.bluedragon.distribution.jy.enums;

public enum DimensionQueryTypeEnum {
    TASK(1,"按任务"),
    BOARD(2,"按板"),
    WAYBILL(3,"按板"),
    PACKAGE(4,"按板");
    private Integer code;
    private String desc;

    DimensionQueryTypeEnum(Integer code, String desc) {
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
