package com.jd.bluedragon.dms.utils;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName BarCodeType
 * @date 2020/3/19
 */
public enum BarCodeType {

    /**
     * 包裹号
     */
    PACKAGE_CODE(1, "包裹号"),

    /**
     * 运单号
     */
    WAYBILL_CODE(2, "运单号"),

    /**
     * 箱号
     */
    BOX_CODE(3, "箱号"),

    /**
     * 批次号
     */
    SEND_CODE(4, "批次号");

    private int code;

    private String name;

    BarCodeType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
