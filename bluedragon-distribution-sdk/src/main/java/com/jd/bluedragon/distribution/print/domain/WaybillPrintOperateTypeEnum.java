package com.jd.bluedragon.distribution.print.domain;

/**
 * 打印平台操作类型
 * Created by shipeilin on 2018/2/5.
 */
public enum WaybillPrintOperateTypeEnum {
    PLATE_PRINT(100101, "平台打印"),
    SITE_PLATE_PRINT(100102, "站点平台打印"),
    PACKAGE_AGAIN_PRINT(100103, "包裹补打"),
    SWITCH_BILL_PRINT(100104, "换单打印"),
    PACKAGE_WEIGH_PRINT(100105, "包裹称重"),
    FIELD_PRINT(100106, "驻场打印"),
    BATCH_SORT_WEIGH_PRINT(100107, "批量分拣称重");

    private Integer type;//操作类型
    private String name;//操作名称

    WaybillPrintOperateTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public Integer PLATE_PRINT_TYPE = 100101;      //平台打印操作类型
    public Integer SITE_PLATE_PRINT_TYPE = 100102;//站点平台打印操作类型
    public Integer PACKAGE_AGAIN_PRINT_TYPE = 100103;//包裹补打操作类型
    public Integer SWITCH_BILL_PRINT_TYPE = 100104;//换单打印操作类型
    public Integer PACKAGE_WEIGH_PRINT_TYPE = 100105;//包裹称重操作类型
    public Integer FIELD_PRINT_TYPE = 100106;       //驻场打印操作类型
    public Integer BATCH_SORT_WEIGH_PRINT_TYPE = 100107;//批量分拣称重操作类型
}
