package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums;

public enum SendTaskTypeEnum {

    VEHICLE(1,"发车任务"),
    AVIATION(2,"发航空任务"),
    RAILWAY(3,"发铁路任务"),
    ;
    private Integer code;
    private String name;


    SendTaskTypeEnum(Integer code, String name) {
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
