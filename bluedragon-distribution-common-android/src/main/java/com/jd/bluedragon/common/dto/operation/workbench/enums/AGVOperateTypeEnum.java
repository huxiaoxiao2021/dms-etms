package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum AGVOperateTypeEnum {
    unloadAuto(0, "卸车自动"),
    sendAuto(1, "发货自动"),
    sendHalfAuto(2, "发货半自动");

    private Integer operateType;
    private String description;

    AGVOperateTypeEnum() {
    }

    AGVOperateTypeEnum(Integer operateType, String description) {
        this.operateType = operateType;
        this.description = description;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
