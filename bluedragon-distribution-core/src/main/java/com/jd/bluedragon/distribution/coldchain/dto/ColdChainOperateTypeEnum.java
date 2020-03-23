package com.jd.bluedragon.distribution.coldchain.dto;

public enum ColdChainOperateTypeEnum {
    /**
     * 验货
     */
    INSPECTION("验货", 1),
    /**
     * 入库
     */
    IN_BOUND("入库", 2),
    /**
     * 出库
     */
    OUT_BOUND("出库", 3),
    /**
     * 发货
     */
    DELIVERY("发货", 4);

    private String name;

    private int type;

    ColdChainOperateTypeEnum(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
