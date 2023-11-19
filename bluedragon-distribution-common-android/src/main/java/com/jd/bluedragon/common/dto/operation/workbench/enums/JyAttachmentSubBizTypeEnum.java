package com.jd.bluedragon.common.dto.operation.workbench.enums;

public enum JyAttachmentSubBizTypeEnum {
    TASK_WORK_GRID_MANAGER_IMPROVE("task_work_grid_manager_improve", "任务-线上管理化-指标改善");
    private final String code;

    private final String name;

    JyAttachmentSubBizTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
