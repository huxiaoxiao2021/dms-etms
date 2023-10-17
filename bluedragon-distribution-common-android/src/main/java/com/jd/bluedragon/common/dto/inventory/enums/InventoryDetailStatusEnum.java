package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum InventoryDetailStatusEnum {

    EXCEPTION(1, "异常待找"),
    FIND_GOOD(2, "找货找到"),
    PDA_REAL_OPERATE(3,"PDA实操找到"),
    ;

    private int code;

    private String name;

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

    InventoryDetailStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

}
