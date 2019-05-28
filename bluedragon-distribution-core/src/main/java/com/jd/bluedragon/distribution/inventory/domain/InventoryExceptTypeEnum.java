package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 盘点异常类型枚举
 */
public enum InventoryExceptTypeEnum {
    INVENTORY_EXCEPT_TYPE_MORE(1, "多货"),
    INVENTORY_EXCEPT_TYPE_LOSS(2, "少货");

    private Integer code;

    private String desc;

    InventoryExceptTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
