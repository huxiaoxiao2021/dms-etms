package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 零担线路类型列表
     */
    public static final List<Integer> LINGDAN_TYPE;

    /**
     * 空铁线路类型列表
     */
    public static final List<Integer> AR_TYPE;

    static {
        LINGDAN_TYPE = new ArrayList<Integer>();
        LINGDAN_TYPE.add(ROAD_LINGDAN.getType());
        LINGDAN_TYPE.add(COLD_CHAIN_LINGDAN.getType());
        LINGDAN_TYPE.add(ROAD_LINDGDAN_PANEL.getType());
        LINGDAN_TYPE.add(RAILWAY_LINGDAN.getType());

        AR_TYPE = new ArrayList<Integer>();
        LINGDAN_TYPE.add(AVIATION.getType());
        LINGDAN_TYPE.add(RAILWAY_ZHENGCHE.getType());
        LINGDAN_TYPE.add(RAILWAY_LINGDAN.getType());
    }


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
