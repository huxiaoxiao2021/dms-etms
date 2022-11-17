package com.jd.bluedragon.distribution.jy.enums;

public enum UnloadStatisticsQueryTypeEnum {

    WAYBILL(3,"运单"),
    PACKAGE(4,"包裹");
    private Integer code;
    private String desc;

    UnloadStatisticsQueryTypeEnum(Integer code, String desc) {
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
