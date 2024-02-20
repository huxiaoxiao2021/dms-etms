package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

public enum SendFlowDisplayEnum {

    DEFAULT(1, "只显示目的地"),
    COUNT(2, "显示流向统计数据"),
    ;

    private Integer code;
    private String name;
    SendFlowDisplayEnum(Integer code, String name) {
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
