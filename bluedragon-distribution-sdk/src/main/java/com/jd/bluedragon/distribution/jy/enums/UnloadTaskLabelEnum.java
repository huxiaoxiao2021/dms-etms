package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName UnloadTaskLabelEnum
 * @Description 卸车任务标签
 * @Author wyh
 * @Date 2022/4/9 18:40
 **/
public enum UnloadTaskLabelEnum {

    SPOT_CHECK(1, "抽检", 1),
    UNLOAD_SINGLE_BILL(2, "逐单卸", 2),
    UNLOAD_HALF_CAR(3, "半车卸", 3),
    ;

    private Integer code;

    private String name;

    /**
     * APP展示标签的顺序。
     */
    private Integer displayOrder;

    UnloadTaskLabelEnum(Integer code, String name, Integer displayOrder) {
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
