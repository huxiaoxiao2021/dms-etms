package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 包裹物流状态枚举
 */
public enum PackStatusEnum {
    RECEIVE(1,"收货"),
    INSPECTION(2, "验货"),
    REPRINT(3, "补打"),
    SORTING(4, "分拣"),
    SORTING_CANCEL(5,"取消分拣"),
    SEND(6, "发货"),
    SEND_CANCEL(7,"取消发货");

    private Integer code;

    private String desc;

    PackStatusEnum(Integer code, String desc) {
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
