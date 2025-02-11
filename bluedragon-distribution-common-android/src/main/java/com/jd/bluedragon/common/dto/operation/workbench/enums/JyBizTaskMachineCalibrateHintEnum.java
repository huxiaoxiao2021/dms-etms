package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 设备校准提示枚举
 *
 * @author hujiping
 * @date 2022/12/7 8:26 PM
 */
public enum JyBizTaskMachineCalibrateHintEnum {

    WEIGHT_NOT_CALIBRATE(0, "重量待校验"),
    VOLUME_NOT_CALIBRATE(1, "体积待校验"),
    CALIBRATE_NOT_START(2, "重量/体积待校验"),
    CALIBRATE_COMPLETE(3, "校准任务已完成");

    private int code;

    private String name;

    JyBizTaskMachineCalibrateHintEnum(int code, String name) {
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
