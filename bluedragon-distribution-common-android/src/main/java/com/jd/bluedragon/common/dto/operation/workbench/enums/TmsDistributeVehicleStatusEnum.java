package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 待派车状态枚举
 */
public enum TmsDistributeVehicleStatusEnum {
    INIT(10, "初始化"),
    CONFIRMED(20, "已确认"),
    ;
    private Integer code;
    private String name;

    TmsDistributeVehicleStatusEnum(Integer code, String name) {
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
