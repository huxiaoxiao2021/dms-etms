package com.jd.bluedragon.distribution.spotcheck.enums;

/**
 * 抽检记录维度枚举
 *
 * @author hujiping
 * @date 2021/8/27 11:32 上午
 */
public enum SpotCheckRecordTypeEnum {

    PACKAGE(1, "包裹"),
    WAYBILL(2, "运单");

    private Integer code;
    private String name;

    SpotCheckRecordTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
