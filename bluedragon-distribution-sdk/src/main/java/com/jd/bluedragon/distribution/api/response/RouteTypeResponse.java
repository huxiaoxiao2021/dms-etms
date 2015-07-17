package com.jd.bluedragon.distribution.api.response;

/**
 * Created by dudong on 2014/10/29.
 */
public class RouteTypeResponse extends BaseResponse {

    /** 运输类型 1：干线；2：支线； */
    private int routeType;

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

}
