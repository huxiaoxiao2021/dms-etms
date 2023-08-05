package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum InventoryTaskStatusEnum {

    NO_START(0, "未开始"),
    ONGOING(1, "进行中"),
    COMPLETE(2, "已完成"),
    ;

    private Integer code;

    private String name;

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

    InventoryTaskStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
