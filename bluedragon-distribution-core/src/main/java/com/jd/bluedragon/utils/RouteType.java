package com.jd.bluedragon.utils;

/**
 * 线路类型
 * <p>
 * Created by lixin39 on 2017/3/16.
 */
public enum RouteType {

    /**
     * 直发到站
     */
    DIRECT_SITE(1, "直发到站"),

    /**
     * 直发分拣
     */
    DIRECT_DMS(2, "直发分拣"),

    /**
     * 多级分拣
     */
    MULTIPLE_DMS(3, "多级分拣");

    private int type;

    private String name;

    RouteType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static RouteType getEnum(int type) {
        for (RouteType route : RouteType.values()) {
            if (route.getType() == type) {
                return route;
            }
        }
        return null;
    }

}
