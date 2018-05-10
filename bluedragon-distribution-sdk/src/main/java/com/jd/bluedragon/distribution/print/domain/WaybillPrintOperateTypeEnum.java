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
    BATCH_SORT_WEIGH_PRINT(100107, "批量分拣称重"),
    FAST_TRANSPORT_PRINT(100108, "快运称重打印");
    private Integer type;//操作类型
    private String name;//操作名称

    WaybillPrintOperateTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public static Integer PLATE_PRINT_TYPE = 100101;      //平台打印操作类型
    public static Integer SITE_PLATE_PRINT_TYPE = 100102;//站点平台打印操作类型
    public static Integer PACKAGE_AGAIN_PRINT_TYPE = 100103;//包裹补打操作类型
    public static Integer SWITCH_BILL_PRINT_TYPE = 100104;//换单打印操作类型
    public static Integer PACKAGE_WEIGH_PRINT_TYPE = 100105;//包裹称重操作类型
    public static Integer FIELD_PRINT_TYPE = 100106;       //驻场打印操作类型
    public static Integer BATCH_SORT_WEIGH_PRINT_TYPE = 100107;//批量分拣称重操作类型
    public static Integer FAST_TRANSPORT_PRINT_TYPE= 100108;//快运称重打印操作类型
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
