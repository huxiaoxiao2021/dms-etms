package com.jd.bluedragon.common.dto.inventory.enums;

/**
 * 处理（责任）部门类型
 *
 */
public enum InventoryListTypeEnum {

    NEED_FIND(1, "需找"),
    FOUND(2, "已找"),
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

    InventoryListTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static final boolean isLegal(Integer code) {
        if(code == null) {
            return false;
        }
        for (InventoryListTypeEnum en : InventoryListTypeEnum.values()) {
            if(en.getCode() == code) {
                return true;
            }
        }
        return false;
    }
}
