package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * 发货任务照片类型枚举
 */
public enum SendImageTypeEnum {

    SEND_IMAGE(0, "发货前照片"),
    SEAL_IMAGE(1, "封车前照片");

    private Integer code;

    private String name;


    SendImageTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (SendImageTypeEnum _enum : SendImageTypeEnum.values()) {
            if (_enum.code.equals(code)) {
                return _enum.name;
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
