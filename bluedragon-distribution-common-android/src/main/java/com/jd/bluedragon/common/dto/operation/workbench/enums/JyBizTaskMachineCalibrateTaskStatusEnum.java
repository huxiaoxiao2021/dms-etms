package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 设备校准任务状态枚举
 *
 * @author hujiping
 * @date 2022/12/12 3:27 PM
 */
public enum JyBizTaskMachineCalibrateTaskStatusEnum {

    TASK_STATUS_TODO(0, "待处理"),

    TASK_STATUS_COMPLETE(1, "已完成"),

    TASK_STATUS_OVERTIME(2, "超时"),

    TASK_STATUS_CLOSE(3, "关闭");

    JyBizTaskMachineCalibrateTaskStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;

    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
