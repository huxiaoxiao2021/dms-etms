package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 设备校准类型枚举
 *
 * @author hujiping
 * @date 2022/12/7 8:26 PM
 */
public enum JyBizTaskMachineCalibrateTypeEnum {

    CALIBRATE_TYPE_W(0, "重量校验"),
    CALIBRATE_TYPE_V(1, "体积校验"),
    CALIBRATE_TYPE_W_V(2, "重量体积校验");

    private int code;

    private String name;

    JyBizTaskMachineCalibrateTypeEnum(int code, String name) {
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
