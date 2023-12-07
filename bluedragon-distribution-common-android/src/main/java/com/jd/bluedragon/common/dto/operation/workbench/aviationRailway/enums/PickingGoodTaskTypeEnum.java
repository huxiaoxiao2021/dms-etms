package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

public enum PickingGoodTaskTypeEnum {

    AVIATION(1,"航空提货任务"),
    RAILWAY(2,"铁路提货任务"),
//    VEHICLE(3,"发车任务"),
    ;
    private Integer code;
    private String name;


    PickingGoodTaskTypeEnum(Integer code, String name) {
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
