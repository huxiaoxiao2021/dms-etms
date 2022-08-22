package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName SendVehicleScanTypeEnum
 * @Description 发货单号扫描类型
 * @Author wyh
 * @Date 2022/6/3 10:37
 **/
public enum SendVehicleScanTypeEnum {

    SCAN_ONE(1, "按件扫描", "支持扫描包裹号或箱号"),
    SCAN_WAYBILL(2, "按单扫描", "扫描包裹号转成运单号，或扫描运单号"),
    // SCAN_BOX(3, "箱号", "扫描包裹号转成箱号，或扫描箱号"),
    // SCAN_BOARD(4, "板号", "扫描包裹号转成板号，或扫描板号"),
    ;

    private Integer code;

    private String name;

    private String desc;

    SendVehicleScanTypeEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
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

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
