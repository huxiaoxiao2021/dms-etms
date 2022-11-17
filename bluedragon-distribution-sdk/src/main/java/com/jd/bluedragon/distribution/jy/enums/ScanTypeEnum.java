package com.jd.bluedragon.distribution.jy.enums;

/**
 * 扫描类型
 **/
public enum ScanTypeEnum {

    SCAN_ONE(0, "按件扫描", "支持扫描包裹号或箱号"),
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号，或扫描运单号");

    private Integer code;

    private String name;

    private String desc;

    ScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        for (ScanTypeEnum scanTypeEnum : ScanTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum.name;
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }


}
