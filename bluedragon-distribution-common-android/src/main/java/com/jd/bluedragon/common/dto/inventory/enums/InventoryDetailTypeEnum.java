package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum InventoryDetailTypeEnum {

    WAIT_SEND(1, "已验未发"),
    NULL_NEXT(2, "无下文"),
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

    InventoryDetailTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
