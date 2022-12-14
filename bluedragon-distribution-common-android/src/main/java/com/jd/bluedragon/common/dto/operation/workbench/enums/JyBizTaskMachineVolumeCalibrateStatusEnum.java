package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 设备体积校准状态枚举
 *
 * @author hujiping
 * @date 2022/12/7 8:26 PM
 */
public enum JyBizTaskMachineVolumeCalibrateStatusEnum {

    NO_CALIBRATE(0, "未校准"),
    ELIGIBLE(1, "合格"),
    UN_ELIGIBLE(2, "不合格");

    private int code;

    private String name;

    JyBizTaskMachineVolumeCalibrateStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

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
