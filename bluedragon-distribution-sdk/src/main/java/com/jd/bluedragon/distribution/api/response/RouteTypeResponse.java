package com.jd.bluedragon.distribution.api.response;

/**
 * Created by dudong on 2014/10/29.
 */
public class RouteTypeResponse extends BaseResponse {

    /** 运输类型 1：干线；2：支线； */
    private int routeType;

    /** 运输方式	公路整车，公路零担
     *
     1       公路零担
     2       公路整车
     3       航空
     4       铁路整车
     5       快递
     6       冷链整车
     7       冷链零担
     8       公路整车平板
     9       公路零担平板
     10     铁路零担 */
    private Integer transWay;


    /** 承运商类型 1 京东自营，2 第三方 */
    private Integer carrierType;

    public int getRouteType() {
        return routeType;
    }

    public void setRouteType(int routeType) {
        this.routeType = routeType;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public Integer getCarrierType() {
        return carrierType;
    }

    public void setCarrierType(Integer carrierType) {
        this.carrierType = carrierType;
    }
}
