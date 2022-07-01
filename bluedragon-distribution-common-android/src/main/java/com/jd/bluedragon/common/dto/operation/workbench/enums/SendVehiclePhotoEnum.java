package com.jd.bluedragon.common.dto.operation.workbench.enums;

/**
 * @ClassName SendVehiclePhoto
 * @Description 发货任务拍照枚举
 * @Author wyh
 * @Date 2022/5/19 15:01
 **/
public enum SendVehiclePhotoEnum {

    CAR_ARRIVED(1, "车辆已到", "*需包含车辆信息"),
    CAR_NOT_ARRIVED(0, "车辆未到", "*可拍摄空卡位")
    ;

    private Integer code;

    private String name;

    private String desc;

    SendVehiclePhotoEnum(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public static String getNameByCode(Integer code) {
        for (SendVehiclePhotoEnum _enum : SendVehiclePhotoEnum.values()) {
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

    public String getDesc() {
        return desc;
    }
}
