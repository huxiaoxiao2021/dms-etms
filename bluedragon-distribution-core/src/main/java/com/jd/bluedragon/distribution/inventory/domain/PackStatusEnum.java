package com.jd.bluedragon.distribution.inventory.domain;

/**
 * 包裹物流状态枚举
 */
public enum PackStatusEnum {
    INSPECTION(1, "验货"),
    REPRINT(2, "补打"),
    SORTING(3, "分拣"),
    SEND(4, "发货");

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
