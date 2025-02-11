package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

public class TransportResp implements Serializable {
    private static final long serialVersionUID = 8116324443064238624L;
    /**
     * 运输方式
     * TransTypeEnum
     */
    private Integer transType;

    private String transTypeName;

    /**
     * 运输方式
     */
    private Integer transWay;
    /**
     * 运输方式名称
     */
    private String transWayName;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 任务简码
     */
    private String transWorkItemCode;

    /**
     * 线路编码
     */
    private String routeLineCode;

    /**
     * 线路名称
     */
    private String routeLineName;

    /**
     * 批次号
     */
    private String sendCode;

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public String getTransWayName() {
        return transWayName;
    }

    public void setTransWayName(String transWayName) {
        this.transWayName = transWayName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getRouteLineCode() {
        return routeLineCode;
    }

    public void setRouteLineCode(String routeLineCode) {
        this.routeLineCode = routeLineCode;
    }

    public String getRouteLineName() {
        return routeLineName;
    }

    public void setRouteLineName(String routeLineName) {
        this.routeLineName = routeLineName;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }


}
