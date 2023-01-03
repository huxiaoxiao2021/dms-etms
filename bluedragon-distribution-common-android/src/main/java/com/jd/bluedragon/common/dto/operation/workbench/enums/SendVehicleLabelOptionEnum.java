package com.jd.bluedragon.common.dto.operation.workbench.enums;

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
    BE_ARRIVED(4, "已到达", 1)
    ;

    private Integer code;

    private String name;

    /**
     * APP展示标签的顺序。
     */
    private Integer displayOrder;

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
}
