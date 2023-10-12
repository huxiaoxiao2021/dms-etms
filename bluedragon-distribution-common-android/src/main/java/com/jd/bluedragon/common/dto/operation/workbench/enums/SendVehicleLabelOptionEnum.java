package com.jd.bluedragon.common.dto.operation.workbench.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SendVehicleLabelOptionEnum
 * @Description 发车任务标签枚举
 * @Author wyh
 * @Date 2022/5/18 18:28
 **/
public enum SendVehicleLabelOptionEnum {

    DRIVER_RECEIVE(1, "司机已领", 1),
    CAR_LENGTH(2, "%s米车", 2),
    ABOUT_ARRIVE(3, "即将到达", 1),
    BE_ARRIVED(4, "已到达", 1),
    SCAN_DOCK(5, "司机靠台", 1),
    DRIVER_TIMEOUT(6, "超时未进", 1),
    LEAVE_TIMEOUT(7, "超时未离", 1),
    ;

    private Integer code;

    private String name;

    /**
     * APP展示标签的顺序。
     */
    private Integer displayOrder;

    private static Map<Integer, SendVehicleLabelOptionEnum> codeToSendVehicleLabelOptionEnumMap;

    static {
        codeToSendVehicleLabelOptionEnumMap = new HashMap<Integer, SendVehicleLabelOptionEnum>();
        for (SendVehicleLabelOptionEnum optionEnum : SendVehicleLabelOptionEnum.values()) {
            codeToSendVehicleLabelOptionEnumMap.put(optionEnum.code, optionEnum);
        }
    }

    SendVehicleLabelOptionEnum(Integer code, String name, Integer displayOrder) {
        this.code = code;
        this.name = name;
        this.displayOrder = displayOrder;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public static SendVehicleLabelOptionEnum getSendVehicleLabelOptionEnumByCode(Integer code) {
        return codeToSendVehicleLabelOptionEnumMap.get(code);
    }
}
