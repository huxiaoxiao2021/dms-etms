package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

public enum PickingScanCodeTypeEnum {

    PACKAGE(1, "包裹"),
    BOX(2, "箱");
    private Integer code;
    private String name;

    PickingScanCodeTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
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
