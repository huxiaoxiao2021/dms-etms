package com.jd.bluedragon.distribution.sealVehicle.domain;

/**
 * TransTypeEnum
 * 运输方式
 *
 * @author jiaowenqiang
 * @date 2019/7/2
 */
public enum TransTypeEnum {
    ROAD_LINGDAN(1, "公路零担"),

    ROAD_ZHENGCHE(2, "公路整车"),

    AVIATION(3, "航空"),

    RAILWAY_ZHENGCHE(4, "铁路整车"),

    EXPRESS(5, "快递"),

    COLD_CHAIN_ZHENGCHE(6, "冷链整车"),

    COLD_CHAIN_LINGDAN(7, "冷链零担"),

    ROAD_ZHENGCHE_PANEL(8, "公路整车平板"),

    ROAD_LINDGDAN_PANEL(9, "公路零担平板"),

    RAILWAY_LINGDAN(10, "铁路零担");


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

    TransTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static TransTypeEnum getEnum(int type) {
        for (TransTypeEnum status : TransTypeEnum.values()) {
            if (status.getType() == type) {
                return status;
            }
        }
        return null;
    }
}
