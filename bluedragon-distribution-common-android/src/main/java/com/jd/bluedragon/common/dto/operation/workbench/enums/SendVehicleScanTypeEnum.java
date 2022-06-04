package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName SendVehicleScanTypeEnum
 * @Description 发货单号扫描类型
 * @Author wyh
 * @Date 2022/6/3 10:37
 **/
public enum SendVehicleScanTypeEnum {

    SCAN_PACKAGE(1, "包裹号"),
    SCAN_WAYBILL(2, "运单号"),
    SCAN_BOX(3, "箱号"),
    SCAN_BOARD(4, "板号"),
    ;

    private Integer code;

    private String name;

    SendVehicleScanTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (SendVehicleScanTypeEnum scanTypeEnum : SendVehicleScanTypeEnum.values()) {
            if (scanTypeEnum.code.equals(code)) {
                return scanTypeEnum.name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
