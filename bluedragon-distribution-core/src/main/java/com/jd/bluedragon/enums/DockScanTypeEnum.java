package com.jd.bluedragon.enums;

public enum DockScanTypeEnum {

    SCAN_DOCK(1, "扫码靠台"),
    SCAN_EXCEPTION(2, "扫码异常无法扫码");

    private Integer code;
    private String name;

    DockScanTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
