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
    SEND_CODE(4, "批次号"),
    /**
     * 包裹|运单号
     */
    PACKAGE_OR_WAYBILL_CODE(5, "包裹|运单号"),
    /**
     * 商家单号
     */
    BUSINESS_ORDER_CODE(6, "商家单号"),

    THIRD_WAYBILL_CODE(7, "三方运单号");

    private int code;

    private String name;

    BarCodeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
