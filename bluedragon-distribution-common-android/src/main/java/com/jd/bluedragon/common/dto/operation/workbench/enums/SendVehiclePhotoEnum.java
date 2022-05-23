package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName SendVehiclePhoto
 * @Description 发货任务拍照枚举
 * @Author wyh
 * @Date 2022/5/19 15:01
 **/
public enum SendVehiclePhotoEnum {

    CAR_ARRIVED(1, "车辆已到"),
    CAR_NOT_ARRIVED(0, "车辆未到")
    ;

    private Integer code;

    private String name;

    SendVehiclePhotoEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        for (SendVehiclePhotoEnum labelOptionEnum : SendVehiclePhotoEnum.values()) {
            if (labelOptionEnum.code.equals(code)) {
                return labelOptionEnum.name;
            }
        }
        return "";
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
