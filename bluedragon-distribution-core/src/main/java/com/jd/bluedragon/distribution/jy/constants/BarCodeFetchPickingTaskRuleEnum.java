package com.jd.bluedragon.distribution.jy.constants;

public enum BarCodeFetchPickingTaskRuleEnum {

    WAIT_PICKING_TASK(1, "待提任务"),
    LAST_PICKING_TASK(2, "待提任务查不到的场景，去上次扫描的任务作为本次任务，扫描记录多扫"),
    MANUAL_CREATE_TASK_EXIST(3, "待提任务为空，首次进入无上次扫描，查找已有未完成自建任务"),
    MANUAL_CREATE_TASK_GENERATE(4, "待提任务为空，首次进入无上次扫描，查无有效自建任务，生成新的自建任务"),
    ;

    BarCodeFetchPickingTaskRuleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

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
