package com.jd.bluedragon.distribution.jy.enums;

/**
 * 扫描数据类型
 **/
public enum ScanCodeTypeEnum {

    SCAN_PACKAGE(101, "按包裹号扫描"),
    SCAN_WAYBILL(102, "按运单号扫描"),
    SCAN_BOX(103, "按箱号扫描");

    private Integer code;

    private String desc;

    ScanCodeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        for (ScanCodeTypeEnum scanTypeEnum : ScanCodeTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum.desc;
            }
        }
        return "";
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
