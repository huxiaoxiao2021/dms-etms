package com.jd.bluedragon.distribution.jy.service.task.enums;

public enum JySendTaskTypeEnum {

    DEFAULT_VEHICLE(1, "（默认）发车任务，历史发货任务都是发车任务"),
    AVIATION(2,"航空计划发货任务"),
    RAILWAY(3, "铁路发货任务"),
    ;
    private Integer code;

    private String desc;

    JySendTaskTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
