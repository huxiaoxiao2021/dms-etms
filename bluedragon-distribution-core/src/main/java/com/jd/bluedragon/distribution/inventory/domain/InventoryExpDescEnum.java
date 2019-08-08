package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 盘点异常类型枚举
 */
public enum InventoryExpDescEnum {
    RECEIVE_LOSS(1,"已收货无实物"),
    INSPECTION_LOSS(2, "已验货无实物"),
    REPRINT_LOSS(3, "包裹补打无实物"),
    SORTING_LOSS(4, "已分拣无实物"),
    SORTING_CANCEL_LOSS(6,"取消分拣无实物"),
    SEND_CANCEL_LOSS(7,"取消发货无实物"),
    EXCEPTION_LOSS(8,"异常外呼无实物"),
    SEND_MORE(9, "已发货有实物"),
    NO_OPERATION_MORE(10,"无任何操作有实物"),
    DIRECTION_EXCEPTION_MORE(11,"流向异常有实物"),
    EXCEPTION_MORE(12, "异常外呼有实物");

    private Integer code;

    private String desc;

    InventoryExpDescEnum(Integer code, String desc) {
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
