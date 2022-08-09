package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName ScanTypeEnum
 * @Description 扫描类型
 **/
public enum ScanTypeEnum {

    PACKAGE(1, "包裹"),
    WAYBILL(2, "运单"),
    BOX(3, "箱"),
    BOARD(4, "板");

    private Integer code;

    private String name;

    ScanTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
