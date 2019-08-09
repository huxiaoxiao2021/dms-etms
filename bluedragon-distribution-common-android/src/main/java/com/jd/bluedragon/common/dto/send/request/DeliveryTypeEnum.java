package com.jd.bluedragon.common.dto.send.request;

/**
 * create by jiaowenqiang
 * 发货类型配置枚举
 */
public enum DeliveryTypeEnum {

    DELIVERY_BY_ROUTE(1, "按路由配置"),

    DELIVERY_BY_FRAME(2, "按龙门架配置");

    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    DeliveryTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static DeliveryTypeEnum getEnum(int type) {
        for (DeliveryTypeEnum status : DeliveryTypeEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
